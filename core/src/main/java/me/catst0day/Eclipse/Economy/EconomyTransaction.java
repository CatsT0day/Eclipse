package me.catst0day.Eclipse.Economy;

import java.util.UUID;

public class EconomyTransaction {
    private final UUID transactionId;
    private final UUID playerId;
    private final EconomyTransactionType type;
    private final double amount;
    private final double balanceBefore;
    private final double balanceAfter;
    private final long timestamp;
    private final String description;
    private UUID relatedPlayerId;
    private String reason;
    
    public EconomyTransaction(UUID playerId, EconomyTransactionType type, double amount, 
                             double balanceBefore, double balanceAfter, String description) {
        this.transactionId = UUID.randomUUID();
        this.playerId = playerId;
        this.type = type;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.timestamp = System.currentTimeMillis();
        this.description = description;
        this.relatedPlayerId = null;
        this.reason = null;
    }
    
    public EconomyTransaction(UUID playerId, EconomyTransactionType type, double amount, 
                             double balanceBefore, double balanceAfter, String description, 
                             UUID relatedPlayerId) {
        this.transactionId = UUID.randomUUID();
        this.playerId = playerId;
        this.type = type;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.timestamp = System.currentTimeMillis();
        this.description = description;
        this.relatedPlayerId = relatedPlayerId;
        this.reason = null;
    }
    
    public UUID getTransactionId() {
        return transactionId;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public EconomyTransactionType getType() {
        return type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public double getBalanceBefore() {
        return balanceBefore;
    }
    
    public double getBalanceAfter() {
        return balanceAfter;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getDescription() {
        return description;
    }
    
    public UUID getRelatedPlayerId() {
        return relatedPlayerId;
    }
    
    public void setRelatedPlayerId(UUID relatedPlayerId) {
        this.relatedPlayerId = relatedPlayerId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
