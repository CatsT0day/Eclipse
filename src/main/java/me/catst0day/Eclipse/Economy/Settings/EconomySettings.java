package me.catst0day.Eclipse.Economy.Settings;

import java.util.HashMap;
import java.util.Map;

public class EconomySettings {
    private String currencyName = "Coins";
    private String currencySymbol = "$";
    private double defaultBalance = 1000.0;
    private boolean logTransactions = false;
    private boolean allowNegativeBalance = false;
    private double maxBalance = Double.MAX_VALUE;
    private int decimalPlaces = 2;
    private boolean taxEnabled = false;
    private double taxRate = 0.0;
    
    public String getCurrencyName() {
        return currencyName;
    }
    
    public EconomySettings setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
        return this;
    }
    
    public String getCurrencySymbol() {
        return currencySymbol;
    }
    
    public EconomySettings setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
        return this;
    }
    
    public double getDefaultBalance() {
        return defaultBalance;
    }
    
    public EconomySettings setDefaultBalance(double defaultBalance) {
        this.defaultBalance = defaultBalance;
        return this;
    }
    
    public boolean isLogTransactions() {
        return logTransactions;
    }
    
    public EconomySettings setLogTransactions(boolean logTransactions) {
        this.logTransactions = logTransactions;
        return this;
    }
    
    public boolean isAllowNegativeBalance() {
        return allowNegativeBalance;
    }
    
    public EconomySettings setAllowNegativeBalance(boolean allowNegativeBalance) {
        this.allowNegativeBalance = allowNegativeBalance;
        return this;
    }
    
    public double getMaxBalance() {
        return maxBalance;
    }
    
    public EconomySettings setMaxBalance(double maxBalance) {
        this.maxBalance = maxBalance;
        return this;
    }
    
    public int getDecimalPlaces() {
        return decimalPlaces;
    }
    
    public EconomySettings setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        return this;
    }
    
    public boolean isTaxEnabled() {
        return taxEnabled;
    }
    
    public EconomySettings setTaxEnabled(boolean taxEnabled) {
        this.taxEnabled = taxEnabled;
        return this;
    }
    
    public double getTaxRate() {
        return taxRate;
    }
    
    public EconomySettings setTaxRate(double taxRate) {
        this.taxRate = taxRate;
        return this;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("currencyName", currencyName);
        map.put("currencySymbol", currencySymbol);
        map.put("defaultBalance", defaultBalance);
        map.put("logTransactions", logTransactions);
        map.put("allowNegativeBalance", allowNegativeBalance);
        map.put("maxBalance", maxBalance);
        map.put("decimalPlaces", decimalPlaces);
        map.put("taxEnabled", taxEnabled);
        map.put("taxRate", taxRate);
        return map;
    }
    
    public static EconomySettings deserialize(Map<String, Object> entry) {
        EconomySettings settings = new EconomySettings();
        if (entry.containsKey("currencyName")) {
            settings.currencyName = (String) entry.get("currencyName");
        }
        if (entry.containsKey("currencySymbol")) {
            settings.currencySymbol = (String) entry.get("currencySymbol");
        }
        if (entry.containsKey("defaultBalance")) {
            settings.defaultBalance = (double) entry.get("defaultBalance");
        }
        if (entry.containsKey("logTransactions")) {
            settings.logTransactions = (boolean) entry.get("logTransactions");
        }
        if (entry.containsKey("allowNegativeBalance")) {
            settings.allowNegativeBalance = (boolean) entry.get("allowNegativeBalance");
        }
        if (entry.containsKey("maxBalance")) {
            settings.maxBalance = (double) entry.get("maxBalance");
        }
        if (entry.containsKey("decimalPlaces")) {
            settings.decimalPlaces = (int) entry.get("decimalPlaces");
        }
        if (entry.containsKey("taxEnabled")) {
            settings.taxEnabled = (boolean) entry.get("taxEnabled");
        }
        if (entry.containsKey("taxRate")) {
            settings.taxRate = (double) entry.get("taxRate");
        }
        return settings;
    }
}
