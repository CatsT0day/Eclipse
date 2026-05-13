package me.catst0day.Eclipse.Economy.Settings;

import java.util.HashMap;
import java.util.Map;

public class EconomyAccountSettings {
    private boolean enabled = true;
    private double balance = 1000.0;
    private double minimumBalance = 0.0;
    private double maximumBalance = Double.MAX_VALUE;
    private boolean interestEnabled = false;
    private double interestRate = 0.0;
    private long interestIntervalTicks = 72000;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public EconomyAccountSettings setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public EconomyAccountSettings setBalance(double balance) {
        this.balance = balance;
        return this;
    }
    
    public double getMinimumBalance() {
        return minimumBalance;
    }
    
    public EconomyAccountSettings setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
        return this;
    }
    
    public double getMaximumBalance() {
        return maximumBalance;
    }
    
    public EconomyAccountSettings setMaximumBalance(double maximumBalance) {
        this.maximumBalance = maximumBalance;
        return this;
    }
    
    public boolean isInterestEnabled() {
        return interestEnabled;
    }
    
    public EconomyAccountSettings setInterestEnabled(boolean interestEnabled) {
        this.interestEnabled = interestEnabled;
        return this;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public EconomyAccountSettings setInterestRate(double interestRate) {
        this.interestRate = interestRate;
        return this;
    }
    
    public long getInterestIntervalTicks() {
        return interestIntervalTicks;
    }
    
    public EconomyAccountSettings setInterestIntervalTicks(long interestIntervalTicks) {
        this.interestIntervalTicks = interestIntervalTicks;
        return this;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("enabled", enabled);
        map.put("balance", balance);
        map.put("minimumBalance", minimumBalance);
        map.put("maximumBalance", maximumBalance);
        map.put("interestEnabled", interestEnabled);
        map.put("interestRate", interestRate);
        map.put("interestIntervalTicks", interestIntervalTicks);
        return map;
    }
    
    public static EconomyAccountSettings deserialize(Map<String, Object> entry) {
        EconomyAccountSettings settings = new EconomyAccountSettings();
        if (entry.containsKey("enabled")) {
            settings.enabled = (boolean) entry.get("enabled");
        }
        if (entry.containsKey("balance")) {
            settings.balance = (double) entry.get("balance");
        }
        if (entry.containsKey("minimumBalance")) {
            settings.minimumBalance = (double) entry.get("minimumBalance");
        }
        if (entry.containsKey("maximumBalance")) {
            settings.maximumBalance = (double) entry.get("maximumBalance");
        }
        if (entry.containsKey("interestEnabled")) {
            settings.interestEnabled = (boolean) entry.get("interestEnabled");
        }
        if (entry.containsKey("interestRate")) {
            settings.interestRate = (double) entry.get("interestRate");
        }
        if (entry.containsKey("interestIntervalTicks")) {
            settings.interestIntervalTicks = (long) entry.get("interestIntervalTicks");
        }
        return settings;
    }
}
