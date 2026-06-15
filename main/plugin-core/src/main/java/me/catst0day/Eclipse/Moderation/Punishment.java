package me.catst0day.Eclipse.Moderation;

import java.util.UUID;

public class Punishment {
    private final int id;
    private final PunishmentType type;
    private final UUID targetUUID;
    private final String targetName;
    private final UUID issuerUUID;
    private final String issuerName;
    private final String reason;
    private final long date;
    private final long expiry;
    private boolean active;
    private final boolean silent;

    public Punishment(int id, PunishmentType type, UUID targetUUID, String targetName,
                      UUID issuerUUID, String issuerName, String reason,
                      long date, long expiry, boolean active, boolean silent) {
        this.id = id;
        this.type = type;
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.issuerUUID = issuerUUID;
        this.issuerName = issuerName;
        this.reason = reason;
        this.date = date;
        this.expiry = expiry;
        this.active = active;
        this.silent = silent;
    }

    public boolean isExpired() {
        return expiry > 0 && System.currentTimeMillis() > expiry;
    }

    public boolean isActive() {
        return active && !isExpired();
    }

    public String getDurationString() {
        if (expiry <= 0) return "permanent";
        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0) return "expired";
        return formatDuration(remaining);
    }

    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        StringBuilder sb = new StringBuilder();
        if (weeks > 0) { sb.append(weeks).append("w "); days %= 7; }
        if (days > 0) { sb.append(days).append("d "); hours %= 24; }
        if (hours > 0) { sb.append(hours).append("h "); minutes %= 60; }
        if (minutes > 0) { sb.append(minutes).append("m "); seconds %= 60; }
        if (seconds > 0 || sb.isEmpty()) { sb.append(seconds).append("s"); }

        return sb.toString().trim();
    }

    public static long parseDuration(String input) {
        if (input.equalsIgnoreCase("perm") || input.equalsIgnoreCase("permanent")) {
            return 0;
        }
        long total = 0;
        StringBuilder num = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
            } else {
                if (num.isEmpty()) continue;
                long value = Long.parseLong(num.toString());
                switch (c) {
                    case 's' -> total += value * 1000;
                    case 'm' -> total += value * 60000;
                    case 'h' -> total += value * 3600000;
                    case 'd' -> total += value * 86400000;
                    case 'w' -> total += value * 604800000;
                }
                num.setLength(0);
            }
        }
        return total;
    }

    public int getId() { return id; }
    public PunishmentType getType() { return type; }
    public UUID getTargetUUID() { return targetUUID; }
    public String getTargetName() { return targetName; }
    public UUID getIssuerUUID() { return issuerUUID; }
    public String getIssuerName() { return issuerName; }
    public String getReason() { return reason; }
    public long getDate() { return date; }
    public long getExpiry() { return expiry; }
    public boolean isSilent() { return silent; }
    public void setActive(boolean active) { this.active = active; }
}
