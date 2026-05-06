package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Managers.Database.EclipseSQLiteManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EclipseEconomyManager {
    private final Eclipse plugin;
    private final EclipseSQLiteManager database;
    private final ConcurrentHashMap<UUID, Double> cache;
    private final double defaultBalance;

    public EclipseEconomyManager(Eclipse plugin) {
        this.plugin = plugin;
        this.database = new EclipseSQLiteManager(plugin.getDataFolder(), "Eclipse.economy.db");
        this.cache = new ConcurrentHashMap<>();
        this.defaultBalance = plugin.getConfig().getDouble("economy.defaultBalance", 1000.0);
        initializeOnlinePlayers();
    }

    private void initializeOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID uuid = player.getUniqueId();
            double balance = database.loadDouble(uuid.toString(), "balance");
            if (balance == -1) {
                balance = defaultBalance;
                database.saveDouble(uuid.toString(), "balance", balance);
            }
            cache.put(uuid, balance);
        });
    }

    public double getBalance(UUID playerId) {
        return cache.computeIfAbsent(playerId, uuid -> loadBalanceFromDatabase(uuid));
    }

    private double loadBalanceFromDatabase(UUID playerId) {
        double balance = database.loadDouble(playerId.toString(), "balance");
        return balance == -1 ? defaultBalance : balance;
    }

    public boolean setBalance(UUID playerId, double amount) {
        if (amount < 0) return false;
        
        cache.put(playerId, amount);
        database.saveDouble(playerId.toString(), "balance", amount);
        
        logTransaction(playerId, "SET", amount, "Admin set balance");
        return true;
    }

    public boolean addBalance(UUID playerId, double amount) {
        if (amount <= 0) return false;
        
        double newBalance = cache.merge(playerId, amount, Double::sum);
        database.saveDouble(playerId.toString(), "balance", newBalance);
        
        logTransaction(playerId, "ADD", amount, "Balance added");
        return true;
    }

    public boolean removeBalance(UUID playerId, double amount) {
        if (amount <= 0) return false;
        
        double currentBalance = getBalance(playerId);
        if (currentBalance >= amount) {
            double newBalance = currentBalance - amount;
            cache.put(playerId, newBalance);
            database.saveDouble(playerId.toString(), "balance", newBalance);
            
            logTransaction(playerId, "REMOVE", amount, "Balance removed");
            return true;
        }
        return false;
    }

    public boolean hasBalance(UUID playerId, double amount) {
        return getBalance(playerId) >= amount;
    }

    public boolean transferBalance(UUID fromPlayer, UUID toPlayer, double amount) {
        if (amount <= 0) return false;
        if (!hasBalance(fromPlayer, amount)) return false;
        
        if (removeBalance(fromPlayer, amount)) {
            addBalance(toPlayer, amount);
            logTransaction(fromPlayer, "TRANSFER_OUT", amount, "To: " + toPlayer.toString());
            logTransaction(toPlayer, "TRANSFER_IN", amount, "From: " + fromPlayer.toString());
            return true;
        }
        return false;
    }

    public void resetBalance(UUID playerId) {
        setBalance(playerId, defaultBalance);
        logTransaction(playerId, "RESET", defaultBalance, "Balance reset to default");
    }

    public void resetAllBalances() {
        cache.replaceAll((uuid, oldValue) -> defaultBalance);
        logTransaction(null, "RESET_ALL", defaultBalance, "All balances reset");
    }

    public double getDefaultBalance() {
        return defaultBalance;
    }

    public void removeFromCache(UUID playerId) {
        cache.remove(playerId);
    }

    private void logTransaction(UUID playerId, String type, double amount, String details) {
        if (!plugin.getConfig().getBoolean("economy.logTransactions", false)) return;
        
        String logEntry = String.format("[%s] Player: %s | Amount: %.2f | Details: %s",
                type, playerId != null ? playerId.toString() : "SYSTEM", amount, details);
        
        plugin.getLogger().info("[ECONOMY] " + logEntry);
    }

    public void shutdown() {
        cache.forEach((uuid, balance) -> {
            database.saveDouble(uuid.toString(), "balance", balance);
        });
        database.close();
    }
}
