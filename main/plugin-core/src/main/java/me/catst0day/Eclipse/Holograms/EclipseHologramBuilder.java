package me.catst0day.Eclipse.Holograms;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;


public class EclipseHologramBuilder {
    private String name;
    private Location location;
    private List<String> lines;
    private boolean clickable;
    private String clickCommand;
    private double clickCost;
    private boolean showParticles;
    private boolean enabled;
    private String permission;
    private int viewDistance;
    private boolean alwaysVisible;
    private int updateInterval;
    private int updateRange;
    private boolean lineOfSight;
    private EclipseHologram.FollowType followType;
    private boolean doubleSided;
    private EclipseHologram.TextAlignment textAlignment;
    private boolean textShadow;
    private int textAlpha;
    private int textWidth;
    private int textFillerWidth;
    private boolean textSeeThrough;
    private int lightLevel;
    private String backgroundColor;
    private int backgroundAlpha;
    private double scale;
    private double yawOffset;
    private double pitchOffset;
    private boolean boardEnabled;
    private Material boardMaterial;
    private double boardScale;
    private double boardYawOffset;
    private double boardPitchOffset;
    private double boardThickness;
    private double iconScale;
    private double iconYawOffset;
    private double iconPitchOffset;
    private int fadeInTicks;
    private int fadeOutTicks;

    private EclipseHologramBuilder() {
        this.lines = new ArrayList<>();
        this.clickable = false;
        this.clickCommand = "";
        this.clickCost = 0.0;
        this.showParticles = true;
        this.enabled = true;
        this.permission = null;
        this.viewDistance = 48;
        this.alwaysVisible = true;
        this.updateInterval = 20;
        this.updateRange = 48;
        this.lineOfSight = false;
        this.followType = EclipseHologram.FollowType.FIXED;
        this.doubleSided = false;
        this.textAlignment = EclipseHologram.TextAlignment.LEFT;
        this.textShadow = false;
        this.textAlpha = 255;
        this.textWidth = 200;
        this.textFillerWidth = 0;
        this.textSeeThrough = false;
        this.lightLevel = -1;
        this.backgroundColor = null;
        this.backgroundAlpha = 0;
        this.scale = 1.0;
        this.yawOffset = 0.0;
        this.pitchOffset = 0.0;
        this.boardEnabled = false;
        this.boardMaterial = null;
        this.boardScale = 1.0;
        this.boardYawOffset = 0.0;
        this.boardPitchOffset = 0.0;
        this.boardThickness = 0.1;
        this.iconScale = 1.0;
        this.iconYawOffset = 0.0;
        this.iconPitchOffset = 0.0;
        this.fadeInTicks = 0;
        this.fadeOutTicks = 0;
    }

    
    public static EclipseHologramBuilder create(String name, Location location) {
        EclipseHologramBuilder builder = new EclipseHologramBuilder();
        builder.name = name;
        builder.location = location.clone();
        return builder;
    }

    
    public EclipseHologramBuilder lines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        return this;
    }

    
    public EclipseHologramBuilder addLine(String line) {
        this.lines.add(line);
        return this;
    }

    
    public EclipseHologramBuilder addLines(String... lines) {
        for (String line : lines) {
            this.lines.add(line);
        }
        return this;
    }

    
    public EclipseHologramBuilder clickable(String command) {
        this.clickable = true;
        this.clickCommand = command;
        return this;
    }

    
    public EclipseHologramBuilder clickable(String command, double cost) {
        this.clickable = true;
        this.clickCommand = command;
        this.clickCost = cost;
        return this;
    }

    
    public EclipseHologramBuilder showParticles(boolean showParticles) {
        this.showParticles = showParticles;
        return this;
    }

    
    public EclipseHologramBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    
    public EclipseHologramBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    
    public EclipseHologramBuilder viewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
        return this;
    }

    
    public EclipseHologramBuilder alwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
        return this;
    }

    
    public EclipseHologramBuilder updateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    
    public EclipseHologramBuilder updateRange(int updateRange) {
        this.updateRange = updateRange;
        return this;
    }

    
    public EclipseHologramBuilder lineOfSight(boolean lineOfSight) {
        this.lineOfSight = lineOfSight;
        return this;
    }

    
    public EclipseHologramBuilder followType(EclipseHologram.FollowType followType) {
        this.followType = followType;
        return this;
    }

    
    public EclipseHologramBuilder doubleSided(boolean doubleSided) {
        this.doubleSided = doubleSided;
        return this;
    }

    
    public EclipseHologramBuilder textAlignment(EclipseHologram.TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    
    public EclipseHologramBuilder textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    
    public EclipseHologramBuilder textAlpha(int textAlpha) {
        this.textAlpha = textAlpha;
        return this;
    }

    
    public EclipseHologramBuilder textWidth(int textWidth) {
        this.textWidth = textWidth;
        return this;
    }

    
    public EclipseHologramBuilder textFillerWidth(int textFillerWidth) {
        this.textFillerWidth = textFillerWidth;
        return this;
    }

    
    public EclipseHologramBuilder textSeeThrough(boolean textSeeThrough) {
        this.textSeeThrough = textSeeThrough;
        return this;
    }

    
    public EclipseHologramBuilder lightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
        return this;
    }

    
    public EclipseHologramBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    
    public EclipseHologramBuilder backgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        return this;
    }

    
    public EclipseHologramBuilder scale(double scale) {
        this.scale = scale;
        return this;
    }

    
    public EclipseHologramBuilder yawOffset(double yawOffset) {
        this.yawOffset = yawOffset;
        return this;
    }

    
    public EclipseHologramBuilder pitchOffset(double pitchOffset) {
        this.pitchOffset = pitchOffset;
        return this;
    }

    
    public EclipseHologramBuilder boardEnabled(boolean boardEnabled) {
        this.boardEnabled = boardEnabled;
        return this;
    }

    
    public EclipseHologramBuilder boardMaterial(Material boardMaterial) {
        this.boardMaterial = boardMaterial;
        return this;
    }

    
    public EclipseHologramBuilder boardScale(double boardScale) {
        this.boardScale = boardScale;
        return this;
    }

    
    public EclipseHologramBuilder boardYawOffset(double boardYawOffset) {
        this.boardYawOffset = boardYawOffset;
        return this;
    }

    
    public EclipseHologramBuilder boardPitchOffset(double boardPitchOffset) {
        this.boardPitchOffset = boardPitchOffset;
        return this;
    }

    
    public EclipseHologramBuilder boardThickness(double boardThickness) {
        this.boardThickness = boardThickness;
        return this;
    }

    
    public EclipseHologramBuilder iconScale(double iconScale) {
        this.iconScale = iconScale;
        return this;
    }

    
    public EclipseHologramBuilder iconYawOffset(double iconYawOffset) {
        this.iconYawOffset = iconYawOffset;
        return this;
    }

    
    public EclipseHologramBuilder iconPitchOffset(double iconPitchOffset) {
        this.iconPitchOffset = iconPitchOffset;
        return this;
    }

    
    public EclipseHologramBuilder fadeInTicks(int fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
        return this;
    }

    
    public EclipseHologramBuilder fadeOutTicks(int fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
        return this;
    }

    
    public EclipseHologram build() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Hologram name cannot be null or empty");
        }
        if (location == null) {
            throw new IllegalStateException("Hologram location cannot be null");
        }

        EclipseHologram hologram = new EclipseHologram(name, location, lines);
        hologram.setClickable(clickable);
        hologram.setClickCommand(clickCommand);
        hologram.setClickCost(clickCost);
        hologram.setShowParticles(showParticles);
        hologram.setEnabled(enabled);
        hologram.setPermission(permission);
        hologram.setViewDistance(viewDistance);
        hologram.setAlwaysVisible(alwaysVisible);
        hologram.setUpdateInterval(updateInterval);
        hologram.setUpdateRange(updateRange);
        hologram.setLineOfSight(lineOfSight);
        hologram.setFollowType(followType);
        hologram.setDoubleSided(doubleSided);
        hologram.setTextAlignment(textAlignment);
        hologram.setTextShadow(textShadow);
        hologram.setTextAlpha(textAlpha);
        hologram.setTextWidth(textWidth);
        hologram.setTextFillerWidth(textFillerWidth);
        hologram.setTextSeeThrough(textSeeThrough);
        hologram.setLightLevel(lightLevel);
        hologram.setBackgroundColor(backgroundColor);
        hologram.setBackgroundAlpha(backgroundAlpha);
        hologram.setScale(scale);
        hologram.setYawOffset(yawOffset);
        hologram.setPitchOffset(pitchOffset);
        hologram.setBoardEnabled(boardEnabled);
        if (boardMaterial != null) {
            hologram.setBoardMaterial(boardMaterial);
        }
        hologram.setBoardScale(boardScale);
        hologram.setBoardYawOffset(boardYawOffset);
        hologram.setBoardPitchOffset(boardPitchOffset);
        hologram.setBoardThickness(boardThickness);
        hologram.setIconScale(iconScale);
        hologram.setIconYawOffset(iconYawOffset);
        hologram.setIconPitchOffset(iconPitchOffset);
        hologram.setFadeInTicks(fadeInTicks);
        hologram.setFadeOutTicks(fadeOutTicks);

        return hologram;
    }
}
