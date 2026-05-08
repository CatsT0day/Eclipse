package me.catst0day.Eclipse.Economy;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Economy.Settings.EconomySettings;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class VaultEconomy implements Economy {
    private final Eclipse plugin;
    private final me.catst0day.Eclipse.Managers.EclipseEconomyManager economyManager;
    
    public VaultEconomy(Eclipse plugin, me.catst0day.Eclipse.Managers.EclipseEconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public String getName() {
        return "EclipseEconomy";
    }
    
    @Override
    public boolean hasAccount(String playerName) {
        return hasAccount(plugin.getServer().getOfflinePlayer(playerName));
    }
    
    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return economyManager.getAccount(player.getUniqueId()) != null;
    }
    
    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }
    
    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }
    
    @Override
    public boolean createAccount(String playerName) {
        return createAccount(plugin.getServer().getOfflinePlayer(playerName));
    }
    
    @Override
    public boolean createAccount(OfflinePlayer player) {
        if (hasAccount(player)) {
            return false;
        }
        economyManager.getBalance(player.getUniqueId());
        return true;
    }
    
    @Override
    public boolean createAccount(String playerName, String worldName) {
        return createAccount(playerName);
    }
    
    @Override
    public boolean createAccount(OfflinePlayer player, String worldName) {
        return createAccount(player);
    }
    
    @Override
    public boolean deleteAccount(String playerName) {
        return false;
    }
    
    @Override
    public boolean deleteAccount(OfflinePlayer player) {
        return false;
    }
    
    @Override
    public double getBalance(String playerName) {
        return getBalance(plugin.getServer().getOfflinePlayer(playerName));
    }
    
    @Override
    public double getBalance(OfflinePlayer player) {
        return economyManager.getBalance(player.getUniqueId());
    }
    
    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }
    
    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }
    
    @Override
    public boolean has(String playerName, double amount) {
        return has(plugin.getServer().getOfflinePlayer(playerName), amount);
    }
    
    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return economyManager.hasBalance(player.getUniqueId(), amount);
    }
    
    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }
    
    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }
    
    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(plugin.getServer().getOfflinePlayer(playerName), amount);
    }
    
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        double balanceBefore = economyManager.getBalance(player.getUniqueId());
        boolean success = economyManager.removeBalance(player.getUniqueId(), amount);
        
        if (success) {
            return new EconomyResponse(EconomyResponse.ResponseType.SUCCESS, amount, balanceBefore, economyManager.getBalance(player.getUniqueId()));
        } else {
            return new EconomyResponse(EconomyResponse.ResponseType.FAILURE, 0, balanceBefore, balanceBefore);
        }
    }
    
    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }
    
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }
    
    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(plugin.getServer().getOfflinePlayer(playerName), amount);
    }
    
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        double balanceBefore = economyManager.getBalance(player.getUniqueId());
        boolean success = economyManager.addBalance(player.getUniqueId(), amount);
        
        if (success) {
            return new EconomyResponse(EconomyResponse.ResponseType.SUCCESS, amount, balanceBefore, economyManager.getBalance(player.getUniqueId()));
        } else {
            return new EconomyResponse(EconomyResponse.ResponseType.FAILURE, 0, balanceBefore, balanceBefore);
        }
    }
    
    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }
    
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }
    
    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(EconomyResponse.ResponseType.NOT_IMPLEMENTED, 0, 0, 0);
    }
    
    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(EconomyResponse.ResponseType.NOT_IMPLEMENTED, 0, 0, 0);
    }
    
    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(EconomyResponse.ResponseType.NOT_IMPLEMENTED, 0, 0, 0);
    }
    
    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(EconomyResponse.ResponseType.NOT_IMPLEMENTED, 0, 0, 0);
    }
    
    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(EconomyResponse.ResponseType.NOT_IMPLEMENTED, 0, 0, 0);
    }
    
    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(EconomyResponse.ResponseType.NOT_IMPLEMENTED, 0, 0, 0);
    }
    
    @Override
    public boolean isBankOwner(String name, String playerName) {
        return false;
    }
    
    @Override
    public boolean isBankOwner(String name, OfflinePlayer player) {
        return false;
    }
    
    @Override
    public boolean isBankMember(String name, String playerName) {
        return false;
    }
    
    @Override
    public boolean isBankMember(String name, OfflinePlayer player) {
        return false;
    }
    
    @Override
    public List<String> getBanks() {
        return null;
    }
    
    @Override
    public boolean createPlayerBankAccount(String playerName, String bankName) {
        return false;
    }
    
    @Override
    public boolean createPlayerBankAccount(OfflinePlayer player, String bankName) {
        return false;
    }
    
    @Override
    public boolean deletePlayerBankAccount(String playerName, String bankName) {
        return false;
    }
    
    @Override
    public boolean deletePlayerBankAccount(OfflinePlayer player, String bankName) {
        return false;
    }
    
    @Override
    public boolean isPlayerBankAccountOwner(String playerName, String bankName) {
        return false;
    }
    
    @Override
    public boolean isPlayerBankAccountOwner(OfflinePlayer player, String bankName) {
        return false;
    }
    
    @Override
    public boolean isPlayerBankAccountMember(String playerName, String bankName) {
        return false;
    }
    
    @Override
    public boolean isPlayerBankAccountMember(OfflinePlayer player, String bankName) {
        return false;
    }
    
    @Override
    public String format(double amount) {
        return economyManager.formatAmount(amount);
    }
    
    @Override
    public String currencyNameSingular() {
        return economyManager.getSettings().getCurrencyName();
    }
    
    @Override
    public String currencyNamePlural() {
        return economyManager.getSettings().getCurrencyName() + "s";
    }
    
    @Override
    public boolean fractionalDigits() {
        return economyManager.getSettings().getDecimalPlaces() > 0;
    }
    
    @Override
    public int fractionalDigitsPlace() {
        return economyManager.getSettings().getDecimalPlaces();
    }
}
