package me.catst0day.Eclipse.Economy;

import me.catst0day.Eclipse.Economy.Settings.EconomyAccountSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EconomyAccount {
    private final UUID playerId;
    private final String playerName;
    private double balance;
    private final EconomyAccountSettings settings;
    private final List<EconomyTransaction> transactionHistory;
    
    public EconomyAccount(UUID playerId, String playerName, double initialBalance, EconomyAccountSettings settings) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.balance = initialBalance;
        this.settings = settings;
        this.transactionHistory = new ArrayList<>();
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public EconomyAccountSettings getSettings() {
        return settings;
    }
    
    public List<EconomyTransaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
    
    public void addTransaction(EconomyTransaction transaction) {
        transactionHistory.add(transaction);
    }
    
    public List<EconomyTransaction> getRecentTransactions(int limit) {
        int size = transactionHistory.size();
        int fromIndex = Math.max(0, size - limit);
        return new ArrayList<>(transactionHistory.subList(fromIndex, size));
    }
    
    public void clearTransactionHistory() {
        transactionHistory.clear();
    }
    
    public boolean canAfford(double amount) {
        return balance >= amount;
    }
    
    public boolean hasMinimumBalance() {
        return balance >= settings.getMinimumBalance();
    }
    
    public boolean isAtMaximumBalance() {
        return balance >= settings.getMaximumBalance();
    }
}
