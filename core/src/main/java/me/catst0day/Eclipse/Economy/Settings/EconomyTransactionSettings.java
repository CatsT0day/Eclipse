package me.catst0day.Eclipse.Economy.Settings;

import java.util.HashMap;
import java.util.Map;

public class EconomyTransactionSettings {
    private boolean logTransactions = false;
    private boolean requireConfirmation = false;
    private double minimumTransactionAmount = 0.01;
    private double maximumTransactionAmount = Double.MAX_VALUE;
    private boolean taxEnabled = false;
    private double taxRate = 0.0;
    private String taxReceiver = null;
    private boolean allowNegativeTransactions = false;
    
    public boolean isLogTransactions() {
        return logTransactions;
    }
    
    public EconomyTransactionSettings setLogTransactions(boolean logTransactions) {
        this.logTransactions = logTransactions;
        return this;
    }
    
    public boolean isRequireConfirmation() {
        return requireConfirmation;
    }
    
    public EconomyTransactionSettings setRequireConfirmation(boolean requireConfirmation) {
        this.requireConfirmation = requireConfirmation;
        return this;
    }
    
    public double getMinimumTransactionAmount() {
        return minimumTransactionAmount;
    }
    
    public EconomyTransactionSettings setMinimumTransactionAmount(double minimumTransactionAmount) {
        this.minimumTransactionAmount = minimumTransactionAmount;
        return this;
    }
    
    public double getMaximumTransactionAmount() {
        return maximumTransactionAmount;
    }
    
    public EconomyTransactionSettings setMaximumTransactionAmount(double maximumTransactionAmount) {
        this.maximumTransactionAmount = maximumTransactionAmount;
        return this;
    }
    
    public boolean isTaxEnabled() {
        return taxEnabled;
    }
    
    public EconomyTransactionSettings setTaxEnabled(boolean taxEnabled) {
        this.taxEnabled = taxEnabled;
        return this;
    }
    
    public double getTaxRate() {
        return taxRate;
    }
    
    public EconomyTransactionSettings setTaxRate(double taxRate) {
        this.taxRate = taxRate;
        return this;
    }
    
    public String getTaxReceiver() {
        return taxReceiver;
    }
    
    public EconomyTransactionSettings setTaxReceiver(String taxReceiver) {
        this.taxReceiver = taxReceiver;
        return this;
    }
    
    public boolean isAllowNegativeTransactions() {
        return allowNegativeTransactions;
    }
    
    public EconomyTransactionSettings setAllowNegativeTransactions(boolean allowNegativeTransactions) {
        this.allowNegativeTransactions = allowNegativeTransactions;
        return this;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("logTransactions", logTransactions);
        map.put("requireConfirmation", requireConfirmation);
        map.put("minimumTransactionAmount", minimumTransactionAmount);
        map.put("maximumTransactionAmount", maximumTransactionAmount);
        map.put("taxEnabled", taxEnabled);
        map.put("taxRate", taxRate);
        map.put("taxReceiver", taxReceiver);
        map.put("allowNegativeTransactions", allowNegativeTransactions);
        return map;
    }
    
    public static EconomyTransactionSettings deserialize(Map<String, Object> entry) {
        EconomyTransactionSettings settings = new EconomyTransactionSettings();
        if (entry.containsKey("logTransactions")) {
            settings.logTransactions = (boolean) entry.get("logTransactions");
        }
        if (entry.containsKey("requireConfirmation")) {
            settings.requireConfirmation = (boolean) entry.get("requireConfirmation");
        }
        if (entry.containsKey("minimumTransactionAmount")) {
            settings.minimumTransactionAmount = (double) entry.get("minimumTransactionAmount");
        }
        if (entry.containsKey("maximumTransactionAmount")) {
            settings.maximumTransactionAmount = (double) entry.get("maximumTransactionAmount");
        }
        if (entry.containsKey("taxEnabled")) {
            settings.taxEnabled = (boolean) entry.get("taxEnabled");
        }
        if (entry.containsKey("taxRate")) {
            settings.taxRate = (double) entry.get("taxRate");
        }
        if (entry.containsKey("taxReceiver")) {
            settings.taxReceiver = (String) entry.get("taxReceiver");
        }
        if (entry.containsKey("allowNegativeTransactions")) {
            settings.allowNegativeTransactions = (boolean) entry.get("allowNegativeTransactions");
        }
        return settings;
    }
}
