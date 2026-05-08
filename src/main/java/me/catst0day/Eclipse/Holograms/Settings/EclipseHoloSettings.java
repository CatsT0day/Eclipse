package me.catst0day.Eclipse.Holograms.Settings;

import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class EclipseHoloSettings {
    private Billboard billboard = Billboard.FIXED;
    private String group = null;
    private boolean checkLineOfSight = false;
    private boolean requiresPermission = false;
    private boolean saveToFile = false;
    private Vector lightLevels = null;
    private Vector direction = null;
    private Vector centerOffset = null;
    private Vector scale = new Vector(1.0, 1.0, 1.0);
    private int updateRange = 64;
    private int visibilityRange = 48;
    private int updateIntervalTicks = 20;

    public Billboard getBillboard() {
        return billboard;
    }

    public EclipseHoloSettings setBillboard(Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    public Vector getScale() {
        return scale;
    }

    public EclipseHoloSettings setScale(Vector scale) {
        this.scale = scale;
        return this;
    }

    public EclipseHoloSettings setScale(double x, double y, double z) {
        this.scale = new Vector(x, y, z);
        return this;
    }

    public double getYaw() {
        return direction != null ? direction.getY() : 0.0;
    }

    public EclipseHoloSettings setYaw(double yaw) {
        if (direction == null) direction = new Vector(0, yaw, 0);
        else direction.setY(yaw);
        return this;
    }

    public double getPitch() {
        return direction != null ? direction.getX() : 0.0;
    }

    public EclipseHoloSettings setPitch(double pitch) {
        if (direction == null) direction = new Vector(pitch, 0, 0);
        else direction.setX(pitch);
        return this;
    }

    public Vector getDirection() {
        return direction;
    }

    public EclipseHoloSettings setDirection(Vector direction) {
        this.direction = direction;
        return this;
    }

    public Vector getLightLevel() {
        return lightLevels;
    }

    public EclipseHoloSettings setLightLevel(Vector lightLevels) {
        this.lightLevels = lightLevels;
        return this;
    }

    public int getSkyLevel() {
        return lightLevels != null ? (int) lightLevels.getX() : 15;
    }

    public EclipseHoloSettings setSkyLevel(int skyLevel) {
        if (lightLevels == null) lightLevels = new Vector(skyLevel, 0, 0);
        else lightLevels.setX(skyLevel);
        return this;
    }

    public int getBlockLevel() {
        return lightLevels != null ? (int) lightLevels.getY() : 0;
    }

    public EclipseHoloSettings setBlockLevel(int blockLevel) {
        if (lightLevels == null) lightLevels = new Vector(0, blockLevel, 0);
        else lightLevels.setY(blockLevel);
        return this;
    }

    public Vector getOffset() {
        return centerOffset;
    }

    public EclipseHoloSettings setOffset(Vector centerOffset) {
        this.centerOffset = centerOffset;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public EclipseHoloSettings setGroup(String group) {
        this.group = group;
        return this;
    }

    public boolean isRequiresPermission() {
        return requiresPermission;
    }

    public EclipseHoloSettings setRequiresPermission(boolean requiresPermission) {
        this.requiresPermission = requiresPermission;
        return this;
    }

    public boolean isSaveToFile() {
        return saveToFile;
    }

    public EclipseHoloSettings setSaveToFile(boolean saveToFile) {
        this.saveToFile = saveToFile;
        return this;
    }

    public boolean isCheckLineOfSight() {
        return checkLineOfSight;
    }

    public EclipseHoloSettings setCheckLineOfSight(boolean checkLineOfSight) {
        this.checkLineOfSight = checkLineOfSight;
        return this;
    }

    public EclipseHoloSettings setVisibilityRange(int visibilityRange) {
        this.visibilityRange = visibilityRange;
        return this;
    }

    public int getVisibilityRange() {
        return visibilityRange;
    }

    public int getUpdateRange() {
        return updateRange;
    }

    public EclipseHoloSettings setUpdateRange(int updateRange) {
        this.updateRange = updateRange;
        return this;
    }

    public int getUpdateIntervalTicks() {
        return updateIntervalTicks;
    }

    public EclipseHoloSettings setUpdateIntervalTicks(int updateIntervalTicks) {
        this.updateIntervalTicks = updateIntervalTicks;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("billboard", billboard.name());
        if (group != null) map.put("group", group);
        map.put("checkLineOfSight", checkLineOfSight);
        map.put("requiresPermission", requiresPermission);
        map.put("saveToFile", saveToFile);
        if (lightLevels != null) map.put("lightLevels", lightLevels);
        if (direction != null) map.put("direction", direction);
        if (centerOffset != null) map.put("centerOffset", centerOffset);
        map.put("scale", scale);
        map.put("updateRange", updateRange);
        map.put("visibilityRange", visibilityRange);
        map.put("updateIntervalTicks", updateIntervalTicks);
        return map;
    }

    public static EclipseHoloSettings deserialize(Map<String, Object> entry) {
        EclipseHoloSettings settings = new EclipseHoloSettings();
        if (entry.containsKey("billboard")) {
            try {
                settings.billboard = Billboard.valueOf((String) entry.get("billboard"));
            } catch (Exception e) {
            }
        }
        if (entry.containsKey("group")) {
            settings.group = (String) entry.get("group");
        }
        if (entry.containsKey("checkLineOfSight")) {
            settings.checkLineOfSight = (boolean) entry.get("checkLineOfSight");
        }
        if (entry.containsKey("requiresPermission")) {
            settings.requiresPermission = (boolean) entry.get("requiresPermission");
        }
        if (entry.containsKey("saveToFile")) {
            settings.saveToFile = (boolean) entry.get("saveToFile");
        }
        if (entry.containsKey("lightLevels")) {
            settings.lightLevels = (Vector) entry.get("lightLevels");
        }
        if (entry.containsKey("direction")) {
            settings.direction = (Vector) entry.get("direction");
        }
        if (entry.containsKey("centerOffset")) {
            settings.centerOffset = (Vector) entry.get("centerOffset");
        }
        if (entry.containsKey("scale")) {
            settings.scale = (Vector) entry.get("scale");
        }
        if (entry.containsKey("updateRange")) {
            settings.updateRange = (int) entry.get("updateRange");
        }
        if (entry.containsKey("visibilityRange")) {
            settings.visibilityRange = (int) entry.get("visibilityRange");
        }
        if (entry.containsKey("updateIntervalTicks")) {
            settings.updateIntervalTicks = (int) entry.get("updateIntervalTicks");
        }
        return settings;
    }
}
