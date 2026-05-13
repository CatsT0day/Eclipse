package me.catst0day.Eclipse.Economy;

import me.catst0day.Eclipse.Eclipse;
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
        return "Eclipse-eco";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
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
    public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(plugin.getServer().getOfflinePlayer(playerName));
    }
    
    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (hasAccount(player)) {
            return false;
        }
        economyManager.getBalance(player.getUniqueId());
        return true;
    }
    
    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }
    
    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
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
            return new EconomyResponse(amount, economyManager.getBalance(player.getUniqueId()), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, balanceBefore, EconomyResponse.ResponseType.FAILURE, null);
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
            return new EconomyResponse(amount, economyManager.getBalance(player.getUniqueId()), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, balanceBefore, EconomyResponse.ResponseType.FAILURE, null);
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
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }
    
    @Override
    public List<String> getBanks() {
        return null;
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
    public int fractionalDigits() {
        return economyManager.getSettings().getDecimalPlaces();
    }
}
