package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Economy.EconomyAccount;
import me.catst0day.Eclipse.Economy.EconomyTransaction;
import me.catst0day.Eclipse.Economy.EconomyTransactionType;
import me.catst0day.Eclipse.Economy.Settings.EconomyAccountSettings;
import me.catst0day.Eclipse.Economy.Settings.EconomySettings;
import me.catst0day.Eclipse.Economy.Settings.EconomyTransactionSettings;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Managers.Database.EclipseSQLiteManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EclipseEconomyManager {
    private final Eclipse plugin;
    private final EclipseSQLiteManager database;
    private final ConcurrentHashMap<UUID, EconomyAccount> accounts;
    private final EconomySettings settings;
    private final EconomyTransactionSettings transactionSettings;
    private final EconomyAccountSettings accountSettings;
    private Economy vaultEconomy;
    private boolean useVault;

    public EclipseEconomyManager(Eclipse plugin) {
        this.plugin = plugin;
        this.database = new EclipseSQLiteManager(plugin.getDataFolder(), "Eclipse.economy.db");
        this.accounts = new ConcurrentHashMap<>();
        this.settings = loadEconomySettings();
        this.transactionSettings = loadTransactionSettings();
        this.accountSettings = loadAccountSettings();
        this.useVault = setupVault();
        initializeOnlinePlayers();
    }

    private boolean setupVault() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        this.vaultEconomy = rsp.getProvider();
        plugin.getLogger().info("Vault economy detected and will be used.");
        return true;
    }

    private EconomySettings loadEconomySettings() {
        File configFile = new File(plugin.getDataFolder(), "economy.yml");
        if (!configFile.exists()) {
            plugin.saveResource("economy.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        EconomySettings settings = new EconomySettings();

        settings.setCurrencyName(config.getString("currencyName", "Coins"));
        settings.setCurrencySymbol(config.getString("currencySymbol", "$"));
        settings.setDefaultBalance(config.getDouble("defaultBalance", 1000.0));
        settings.setLogTransactions(config.getBoolean("logTransactions", false));
        settings.setAllowNegativeBalance(config.getBoolean("allowNegativeBalance", false));
        settings.setMaxBalance(config.getDouble("maxBalance", 999999999.0));
        settings.setDecimalPlaces(config.getInt("decimalPlaces", 2));
        settings.setTaxEnabled(config.getBoolean("taxEnabled", false));
        settings.setTaxRate(config.getDouble("taxRate", 0.0));

        return settings;
    }

    private EconomyTransactionSettings loadTransactionSettings() {
        File configFile = new File(plugin.getDataFolder(), "economy.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        EconomyTransactionSettings settings = new EconomyTransactionSettings();

        settings.setLogTransactions(config.getBoolean("transaction.logTransactions", false));
        settings.setRequireConfirmation(config.getBoolean("transaction.requireConfirmation", false));
        settings.setMinimumTransactionAmount(config.getDouble("transaction.minimumTransactionAmount", 0.01));
        settings.setMaximumTransactionAmount(config.getDouble("transaction.maximumTransactionAmount", 999999999.0));
        settings.setTaxEnabled(config.getBoolean("transaction.taxEnabled", false));
        settings.setTaxRate(config.getDouble("transaction.taxRate", 0.0));
        settings.setTaxReceiver(config.getString("transaction.taxReceiver", ""));
        settings.setAllowNegativeTransactions(config.getBoolean("transaction.allowNegativeTransactions", false));

        return settings;
    }

    private EconomyAccountSettings loadAccountSettings() {
        File configFile = new File(plugin.getDataFolder(), "economy.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        EconomyAccountSettings settings = new EconomyAccountSettings();

        settings.setEnabled(config.getBoolean("account.enabled", true));
        settings.setMinimumBalance(config.getDouble("account.minimumBalance", 0.0));
        settings.setMaximumBalance(config.getDouble("account.maximumBalance", 999999999.0));
        settings.setInterestEnabled(config.getBoolean("account.interestEnabled", false));
        settings.setInterestRate(config.getDouble("account.interestRate", 0.01));
        settings.setInterestIntervalTicks(config.getLong("account.interestIntervalTicks", 72000));

        return settings;
    }

    private void initializeOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID uuid = player.getUniqueId();
            EconomyAccount account = loadAccountFromDatabase(uuid, player.getName());
            if (account == null) {
                account = createAccount(uuid, player.getName());
            }
            accounts.put(uuid, account);
        });
    }

    private EconomyAccount createAccount(UUID playerId, String playerName) {
        EconomyAccount account = new EconomyAccount(playerId, playerName, settings.getDefaultBalance(), accountSettings);
        saveAccountToDatabase(account);
        return account;
    }

    private EconomyAccount loadAccountFromDatabase(UUID playerId, String playerName) {
        double balance = database.loadDouble(playerId.toString(), "balance");
        if (balance == -1) {
            return null;
        }

        EconomyAccount account = new EconomyAccount(playerId, playerName, balance, accountSettings);
        return account;
    }

    private void saveAccountToDatabase(EconomyAccount account) {
        database.saveDouble(account.getPlayerId().toString(), "balance", account.getBalance());
    }

    public double getBalance(UUID playerId) {
        if (useVault && vaultEconomy != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            return vaultEconomy.getBalance(offlinePlayer);
        }

        EconomyAccount account = accounts.computeIfAbsent(playerId, uuid -> {
            EconomyAccount newAccount = loadAccountFromDatabase(uuid, "Unknown");
            if (newAccount == null) {
                newAccount = createAccount(uuid, "Unknown");
            }
            return newAccount;
        });
        return account.getBalance();
    }

    public boolean setBalance(UUID playerId, double amount) {
        if (amount < 0 && !settings.isAllowNegativeBalance()) return false;
        if (amount > settings.getMaxBalance()) return false;

        if (useVault && vaultEconomy != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            double balanceBefore = vaultEconomy.getBalance(offlinePlayer);

            if (balanceBefore > amount) {
                vaultEconomy.withdrawPlayer(offlinePlayer, balanceBefore - amount);
            } else if (balanceBefore < amount) {
                vaultEconomy.depositPlayer(offlinePlayer, amount - balanceBefore);
            }
            return true;
        }

        EconomyAccount account = accounts.get(playerId);
        if (account == null) {
            account = createAccount(playerId, "Unknown");
            accounts.put(playerId, account);
        }

        double balanceBefore = account.getBalance();
        account.setBalance(amount);
        saveAccountToDatabase(account);

        EconomyTransaction transaction = new EconomyTransaction(
                playerId, EconomyTransactionType.SET, amount, balanceBefore, amount, "Admin set balance"
        );
        account.addTransaction(transaction);

        if (settings.isLogTransactions()) {
            logTransaction(transaction);
        }

        return true;
    }

    public boolean addBalance(UUID playerId, double amount) {
        if (amount <= 0) return false;
        if (amount < transactionSettings.getMinimumTransactionAmount()) return false;
        if (amount > transactionSettings.getMaximumTransactionAmount()) return false;

        if (useVault && vaultEconomy != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            vaultEconomy.depositPlayer(offlinePlayer, amount);
            return true;
        }

        EconomyAccount account = accounts.get(playerId);
        if (account == null) {
            account = createAccount(playerId, "Unknown");
            accounts.put(playerId, account);
        }

        double balanceBefore = account.getBalance();
        double newBalance = balanceBefore + amount;

        if (newBalance > settings.getMaxBalance()) return false;

        account.setBalance(newBalance);
        saveAccountToDatabase(account);

        EconomyTransaction transaction = new EconomyTransaction(
                playerId, EconomyTransactionType.DEPOSIT, amount, balanceBefore, newBalance, "Balance added"
        );
        account.addTransaction(transaction);

        if (settings.isLogTransactions()) {
            logTransaction(transaction);
        }

        return true;
    }

    public boolean removeBalance(UUID playerId, double amount) {
        if (amount <= 0) return false;
        if (amount < transactionSettings.getMinimumTransactionAmount()) return false;
        if (amount > transactionSettings.getMaximumTransactionAmount()) return false;

        if (useVault && vaultEconomy != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            if (!vaultEconomy.has(offlinePlayer, amount)) {
                return false;
            }
            vaultEconomy.withdrawPlayer(offlinePlayer, amount);
            return true;
        }

        EconomyAccount account = accounts.get(playerId);
        if (account == null) {
            account = createAccount(playerId, "Unknown");
            accounts.put(playerId, account);
        }

        double balanceBefore = account.getBalance();
        double newBalance = balanceBefore - amount;

        if (newBalance < accountSettings.getMinimumBalance() && !settings.isAllowNegativeBalance()) return false;

        account.setBalance(newBalance);
        saveAccountToDatabase(account);

        EconomyTransaction transaction = new EconomyTransaction(
                playerId, EconomyTransactionType.WITHDRAW, amount, balanceBefore, newBalance, "Balance removed"
        );
        account.addTransaction(transaction);

        if (settings.isLogTransactions()) {
            logTransaction(transaction);
        }

        return true;
    }

    public boolean hasBalance(UUID playerId, double amount) {
        return getBalance(playerId) >= amount;
    }

    public boolean transferBalance(UUID fromPlayer, UUID toPlayer, double amount) {
        if (amount <= 0) return false;
        if (amount < transactionSettings.getMinimumTransactionAmount()) return false;
        if (amount > transactionSettings.getMaximumTransactionAmount()) return false;
        if (!hasBalance(fromPlayer, amount)) return false;

        if (useVault && vaultEconomy != null) {
            OfflinePlayer fromOffline = Bukkit.getOfflinePlayer(fromPlayer);
            OfflinePlayer toOffline = Bukkit.getOfflinePlayer(toPlayer);

            if (!vaultEconomy.has(fromOffline, amount)) {
                return false;
            }

            vaultEconomy.withdrawPlayer(fromOffline, amount);
            vaultEconomy.depositPlayer(toOffline, amount);
            return true;
        }

        EconomyAccount fromAccount = accounts.get(fromPlayer);
        EconomyAccount toAccount = accounts.get(toPlayer);

        if (fromAccount == null) return false;
        if (toAccount == null) {
            toAccount = createAccount(toPlayer, "Unknown");
            accounts.put(toPlayer, toAccount);
        }

        double fromBalanceBefore = fromAccount.getBalance();
        double toBalanceBefore = toAccount.getBalance();

        if (removeBalance(fromPlayer, amount)) {
            addBalance(toPlayer, amount);

            EconomyTransaction fromTransaction = new EconomyTransaction(
                    fromPlayer, EconomyTransactionType.TRANSFER, amount, fromBalanceBefore, fromAccount.getBalance(),
                    "Transfer to: " + toPlayer.toString(), toPlayer
            );
            fromAccount.addTransaction(fromTransaction);

            EconomyTransaction toTransaction = new EconomyTransaction(
                    toPlayer, EconomyTransactionType.TRANSFER, amount, toBalanceBefore, toAccount.getBalance(),
                    "Transfer from: " + fromPlayer.toString(), fromPlayer
            );
            toAccount.addTransaction(toTransaction);

            if (settings.isLogTransactions()) {
                logTransaction(fromTransaction);
                logTransaction(toTransaction);
            }

            return true;
        }
        return false;
    }

    public void resetBalance(UUID playerId) {
        setBalance(playerId, settings.getDefaultBalance());
    }

    public void resetAllBalances() {
        accounts.forEach((uuid, account) -> {
            setBalance(uuid, settings.getDefaultBalance());
        });
    }

    public double getDefaultBalance() {
        return settings.getDefaultBalance();
    }

    public EconomySettings getSettings() {
        return settings;
    }

    public EconomyTransactionSettings getTransactionSettings() {
        return transactionSettings;
    }

    public EconomyAccountSettings getAccountSettings() {
        return accountSettings;
    }

    public EconomyAccount getAccount(UUID playerId) {
        return accounts.get(playerId);
    }

    public void removeFromCache(UUID playerId) {
        EconomyAccount account = accounts.remove(playerId);
        if (account != null) {
            saveAccountToDatabase(account);
        }
    }

    private void logTransaction(EconomyTransaction transaction) {
        String logEntry = String.format("[%s] Player: %s | Amount: %.2f | Balance Before: %.2f | Balance After: %.2f | Details: %s",
                transaction.getType(),
                transaction.getPlayerId() != null ? transaction.getPlayerId().toString() : "SYSTEM",
                transaction.getAmount(),
                transaction.getBalanceBefore(),
                transaction.getBalanceAfter(),
                transaction.getDescription());

        plugin.getLogger().info("[ECONOMY] " + logEntry);
    }

    public String formatAmount(double amount) {
        return String.format("%s%,." + settings.getDecimalPlaces() + "f %s",
                settings.getCurrencySymbol(), amount, settings.getCurrencyName());
    }

    public boolean isUsingVault() {
        return useVault;
    }
    
    public Economy getVaultEconomy() {
        return vaultEconomy;
    }

    public void shutdown() {
        accounts.forEach((uuid, account) -> {
            saveAccountToDatabase(account);
        });
        database.close();
    }

    public double getBalance(EclipsePlr player) {
        return getBalance(player.getUniqueId());
    }

    public boolean setBalance(EclipsePlr player, double amount) {
        return setBalance(player.getUniqueId(), amount);
    }

    public boolean addBalance(EclipsePlr player, double amount) {
        return addBalance(player.getUniqueId(), amount);
    }

    public boolean removeBalance(EclipsePlr player, double amount) {
        return removeBalance(player.getUniqueId(), amount);
    }

    public boolean hasBalance(EclipsePlr player, double amount) {
        return hasBalance(player.getUniqueId(), amount);
    }

    public boolean transferBalance(EclipsePlr fromPlayer, EclipsePlr toPlayer, double amount) {
        return transferBalance(fromPlayer.getUniqueId(), toPlayer.getUniqueId(), amount);
    }

    public EconomyAccount getAccount(EclipsePlr player) {
        return getAccount(player.getUniqueId());
    }

    public EconomyAccount createAccount(EclipsePlr player) {
        return createAccount(player.getUniqueId(), player.getName());
    }
}
