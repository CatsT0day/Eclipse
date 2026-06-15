package me.catst0day.Eclipse.Moderation;

public enum PunishmentType {
    BAN,
    TEMP_BAN,
    IP_BAN,
    MUTE,
    TEMP_MUTE,
    WARN,
    KICK;

    public boolean isBan() {
        return this == BAN || this == TEMP_BAN || this == IP_BAN;
    }

    public boolean isMute() {
        return this == MUTE || this == TEMP_MUTE;
    }

    public boolean isTemp() {
        return this == TEMP_BAN || this == TEMP_MUTE;
    }

    public String getPastAction() {
        return switch (this) {
            case BAN, TEMP_BAN, IP_BAN -> "banned";
            case MUTE, TEMP_MUTE -> "muted";
            case WARN -> "warned";
            case KICK -> "kicked";
        };
    }
}
