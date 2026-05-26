package me.catst0day.Eclipse.Holograms.Settings;

import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class EclipseHoloIconSettings {
    private Vector scale = new Vector(1.0, 1.0, 1.0);
    private Vector direction = new Vector(0, 0, 0);
    private Vector offset = new Vector(0, 0, 0);
    private Billboard billboard = Billboard.FIXED;

    public EclipseHoloIconSettings() {
    }

    public Billboard getBillboard() {
        return billboard;
    }

    public EclipseHoloIconSettings setBillboard(Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    public Vector getOffset() {
        return offset;
    }

    public EclipseHoloIconSettings setOffset(Vector offset) {
        this.offset = offset;
        return this;
    }

    public Vector getScale() {
        return scale;
    }

    public Vector getDirection() {
        return direction;
    }

    public EclipseHoloIconSettings setDirection(Vector direction) {
        this.direction = direction;
        return this;
    }

    public EclipseHoloIconSettings setScale(double iconScale) {
        this.scale = new Vector(iconScale, iconScale, iconScale);
        return this;
    }

    public EclipseHoloIconSettings setScale(Vector iconScale) {
        this.scale = iconScale;
        return this;
    }

    public int getPitch() {
        return (int) direction.getX();
    }

    public EclipseHoloIconSettings setPitch(int iconPitch) {
        direction.setX(iconPitch);
        return this;
    }

    public int getYaw() {
        return (int) direction.getY();
    }

    public EclipseHoloIconSettings setYaw(int iconYaw) {
        direction.setY(iconYaw);
        return this;
    }

    public int getRoll() {
        return (int) direction.getZ();
    }

    public EclipseHoloIconSettings setRoll(int iconRoll) {
        direction.setZ(iconRoll);
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("scale", scale);
        map.put("direction", direction);
        map.put("offset", offset);
        map.put("billboard", billboard.name());
        return map;
    }

    public static EclipseHoloIconSettings deserialize(Map<String, Object> entry) {
        EclipseHoloIconSettings settings = new EclipseHoloIconSettings();
        if (entry.containsKey("scale")) {
            settings.scale = (Vector) entry.get("scale");
        }
        if (entry.containsKey("direction")) {
            settings.direction = (Vector) entry.get("direction");
        }
        if (entry.containsKey("offset")) {
            settings.offset = (Vector) entry.get("offset");
        }
        if (entry.containsKey("billboard")) {
            try {
                settings.billboard = Billboard.valueOf((String) entry.get("billboard"));
            } catch (Exception e) {
            }
        }
        return settings;
    }
}
