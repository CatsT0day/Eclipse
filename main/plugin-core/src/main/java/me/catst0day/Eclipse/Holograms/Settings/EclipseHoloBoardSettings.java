package me.catst0day.Eclipse.Holograms.Settings;

import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class EclipseHoloBoardSettings {
    private boolean enabled = false;
    private Material material = Material.BARRIER;
    private Vector scale = new Vector(1.0, 1.0, 1.0);
    private Vector direction = new Vector(0, 0, 0);
    private double thickness = 0.05;

    public boolean isEnabled() {
        return enabled;
    }

    public EclipseHoloBoardSettings setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public EclipseHoloBoardSettings setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public Vector getScale() {
        return scale;
    }

    public EclipseHoloBoardSettings setScale(Vector scale) {
        this.scale = scale;
        return this;
    }

    public Vector getDirection() {
        return direction;
    }

    public EclipseHoloBoardSettings setDirection(Vector direction) {
        this.direction = direction;
        return this;
    }

    public double getThickness() {
        return thickness;
    }

    public EclipseHoloBoardSettings setThickness(double thickness) {
        this.thickness = thickness;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("enabled", enabled);
        map.put("material", material.name());
        map.put("scale", scale);
        map.put("direction", direction);
        map.put("thickness", thickness);
        return map;
    }

    public static EclipseHoloBoardSettings deserialize(Map<String, Object> entry) {
        EclipseHoloBoardSettings settings = new EclipseHoloBoardSettings();
        if (entry.containsKey("enabled")) {
            settings.enabled = (boolean) entry.get("enabled");
        }
        if (entry.containsKey("material")) {
            try {
                settings.material = Material.valueOf((String) entry.get("material"));
            } catch (Exception e) {
            }
        }
        if (entry.containsKey("scale")) {
            settings.scale = (Vector) entry.get("scale");
        }
        if (entry.containsKey("direction")) {
            settings.direction = (Vector) entry.get("direction");
        }
        if (entry.containsKey("thickness")) {
            settings.thickness = (double) entry.get("thickness");
        }
        return settings;
    }
}
