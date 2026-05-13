package me.catst0day.Eclipse.Enums;

import org.bukkit.potion.PotionEffectType;

public enum EclipsePotionEffectType {
    SPEED(PotionEffectType.SPEED),
    SLOWNESS(PotionEffectType.SLOWNESS),
    FAST_DIGGING(PotionEffectType.HASTE),
    SLOW_DIGGING(PotionEffectType.MINING_FATIGUE),
    INCREASE_DAMAGE(PotionEffectType.STRENGTH),
    HEAL(PotionEffectType.INSTANT_HEALTH),
    HARM(PotionEffectType.INSTANT_DAMAGE),
    JUMP(PotionEffectType.JUMP_BOOST),
    CONFUSION(PotionEffectType.NAUSEA),
    REGENERATION(PotionEffectType.REGENERATION),
    DAMAGE_RESISTANCE(PotionEffectType.RESISTANCE),
    FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE),
    WATER_BREATHING(PotionEffectType.WATER_BREATHING),
    INVISIBILITY(PotionEffectType.INVISIBILITY),
    BLINDNESS(PotionEffectType.BLINDNESS),
    NIGHT_VISION(PotionEffectType.NIGHT_VISION),
    HUNGER(PotionEffectType.HUNGER),
    WEAKNESS(PotionEffectType.WEAKNESS),
    POISON(PotionEffectType.POISON),
    WITHER(PotionEffectType.WITHER),
    HEALTH_BOOST(PotionEffectType.HEALTH_BOOST),
    ABSORPTION(PotionEffectType.ABSORPTION),
    SATURATION(PotionEffectType.SATURATION),
    GLOWING(PotionEffectType.GLOWING);

    private final PotionEffectType potionEffectType;

    EclipsePotionEffectType(PotionEffectType potionEffectType) {
        this.potionEffectType = potionEffectType;
    }

    public PotionEffectType getBukkitEffect() {
        return potionEffectType;
    }

    public static EclipsePotionEffectType fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}