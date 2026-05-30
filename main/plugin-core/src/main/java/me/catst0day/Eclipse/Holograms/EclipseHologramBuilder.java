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

    /**
     * Starts building a new hologram.
     *
     * @param name The hologram name
     * @param location The hologram location
     * @return A new builder instance
     */
    public static EclipseHologramBuilder create(String name, Location location) {
        EclipseHologramBuilder builder = new EclipseHologramBuilder();
        builder.name = name;
        builder.location = location.clone();
        return builder;
    }

    /**
     * Sets the hologram lines.
     *
     * @param lines The lines to display
     * @return This builder
     */
    public EclipseHologramBuilder lines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        return this;
    }

    /**
     * Adds a single line to the hologram.
     *
     * @param line The line to add
     * @return This builder
     */
    public EclipseHologramBuilder addLine(String line) {
        this.lines.add(line);
        return this;
    }

    /**
     * Adds multiple lines to the hologram.
     *
     * @param lines The lines to add
     * @return This builder
     */
    public EclipseHologramBuilder addLines(String... lines) {
        for (String line : lines) {
            this.lines.add(line);
        }
        return this;
    }

    /**
     * Makes the hologram clickable with a command.
     *
     * @param command The command to execute on click
     * @return This builder
     */
    public EclipseHologramBuilder clickable(String command) {
        this.clickable = true;
        this.clickCommand = command;
        return this;
    }

    /**
     * Makes the hologram clickable with a command and cost.
     *
     * @param command The command to execute on click
     * @param cost The cost to click
     * @return This builder
     */
    public EclipseHologramBuilder clickable(String command, double cost) {
        this.clickable = true;
        this.clickCommand = command;
        this.clickCost = cost;
        return this;
    }

    /**
     * Sets whether particles are shown.
     *
     * @param showParticles Whether to show particles
     * @return This builder
     */
    public EclipseHologramBuilder showParticles(boolean showParticles) {
        this.showParticles = showParticles;
        return this;
    }

    /**
     * Sets whether the hologram is enabled.
     *
     * @param enabled Whether the hologram is enabled
     * @return This builder
     */
    public EclipseHologramBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Sets the permission required to view the hologram.
     *
     * @param permission The permission node
     * @return This builder
     */
    public EclipseHologramBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets the view distance.
     *
     * @param viewDistance The view distance in blocks
     * @return This builder
     */
    public EclipseHologramBuilder viewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
        return this;
    }

    /**
     * Sets whether the hologram is always visible.
     *
     * @param alwaysVisible Whether always visible
     * @return This builder
     */
    public EclipseHologramBuilder alwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
        return this;
    }

    /**
     * Sets the update interval in ticks.
     *
     * @param updateInterval The update interval
     * @return This builder
     */
    public EclipseHologramBuilder updateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    /**
     * Sets the update range in blocks.
     *
     * @param updateRange The update range
     * @return This builder
     */
    public EclipseHologramBuilder updateRange(int updateRange) {
        this.updateRange = updateRange;
        return this;
    }

    /**
     * Sets whether line of sight is checked.
     *
     * @param lineOfSight Whether to check line of sight
     * @return This builder
     */
    public EclipseHologramBuilder lineOfSight(boolean lineOfSight) {
        this.lineOfSight = lineOfSight;
        return this;
    }

    /**
     * Sets the follow type (billboard).
     *
     * @param followType The follow type
     * @return This builder
     */
    public EclipseHologramBuilder followType(EclipseHologram.FollowType followType) {
        this.followType = followType;
        return this;
    }

    /**
     * Sets whether text is double-sided.
     *
     * @param doubleSided Whether double-sided
     * @return This builder
     */
    public EclipseHologramBuilder doubleSided(boolean doubleSided) {
        this.doubleSided = doubleSided;
        return this;
    }

    /**
     * Sets the text alignment.
     *
     * @param textAlignment The text alignment
     * @return This builder
     */
    public EclipseHologramBuilder textAlignment(EclipseHologram.TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    /**
     * Sets whether text has shadow.
     *
     * @param textShadow Whether text has shadow
     * @return This builder
     */
    public EclipseHologramBuilder textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    /**
     * Sets the text alpha (transparency).
     *
     * @param textAlpha The alpha value (0-255)
     * @return This builder
     */
    public EclipseHologramBuilder textAlpha(int textAlpha) {
        this.textAlpha = textAlpha;
        return this;
    }

    /**
     * Sets the text width.
     *
     * @param textWidth The text width
     * @return This builder
     */
    public EclipseHologramBuilder textWidth(int textWidth) {
        this.textWidth = textWidth;
        return this;
    }

    /**
     * Sets the text filler width.
     *
     * @param textFillerWidth The filler width
     * @return This builder
     */
    public EclipseHologramBuilder textFillerWidth(int textFillerWidth) {
        this.textFillerWidth = textFillerWidth;
        return this;
    }

    /**
     * Sets whether text is see-through.
     *
     * @param textSeeThrough Whether see-through
     * @return This builder
     */
    public EclipseHologramBuilder textSeeThrough(boolean textSeeThrough) {
        this.textSeeThrough = textSeeThrough;
        return this;
    }

    /**
     * Sets the light level.
     *
     * @param lightLevel The light level (-1 to 15)
     * @return This builder
     */
    public EclipseHologramBuilder lightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
        return this;
    }

    /**
     * Sets the background color (hex format).
     *
     * @param backgroundColor The background color
     * @return This builder
     */
    public EclipseHologramBuilder backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * Sets the background alpha.
     *
     * @param backgroundAlpha The background alpha (0-255)
     * @return This builder
     */
    public EclipseHologramBuilder backgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        return this;
    }

    /**
     * Sets the hologram scale.
     *
     * @param scale The scale
     * @return This builder
     */
    public EclipseHologramBuilder scale(double scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Sets the yaw offset.
     *
     * @param yawOffset The yaw offset
     * @return This builder
     */
    public EclipseHologramBuilder yawOffset(double yawOffset) {
        this.yawOffset = yawOffset;
        return this;
    }

    /**
     * Sets the pitch offset.
     *
     * @param pitchOffset The pitch offset
     * @return This builder
     */
    public EclipseHologramBuilder pitchOffset(double pitchOffset) {
        this.pitchOffset = pitchOffset;
        return this;
    }

    /**
     * Enables the background board.
     *
     * @param boardEnabled Whether board is enabled
     * @return This builder
     */
    public EclipseHologramBuilder boardEnabled(boolean boardEnabled) {
        this.boardEnabled = boardEnabled;
        return this;
    }

    /**
     * Sets the board material.
     *
     * @param boardMaterial The board material
     * @return This builder
     */
    public EclipseHologramBuilder boardMaterial(Material boardMaterial) {
        this.boardMaterial = boardMaterial;
        return this;
    }

    /**
     * Sets the board scale.
     *
     * @param boardScale The board scale
     * @return This builder
     */
    public EclipseHologramBuilder boardScale(double boardScale) {
        this.boardScale = boardScale;
        return this;
    }

    /**
     * Sets the board yaw offset.
     *
     * @param boardYawOffset The board yaw offset
     * @return This builder
     */
    public EclipseHologramBuilder boardYawOffset(double boardYawOffset) {
        this.boardYawOffset = boardYawOffset;
        return this;
    }

    /**
     * Sets the board pitch offset.
     *
     * @param boardPitchOffset The board pitch offset
     * @return This builder
     */
    public EclipseHologramBuilder boardPitchOffset(double boardPitchOffset) {
        this.boardPitchOffset = boardPitchOffset;
        return this;
    }

    /**
     * Sets the board thickness.
     *
     * @param boardThickness The board thickness
     * @return This builder
     */
    public EclipseHologramBuilder boardThickness(double boardThickness) {
        this.boardThickness = boardThickness;
        return this;
    }

    /**
     * Sets the icon scale.
     *
     * @param iconScale The icon scale
     * @return This builder
     */
    public EclipseHologramBuilder iconScale(double iconScale) {
        this.iconScale = iconScale;
        return this;
    }

    /**
     * Sets the icon yaw offset.
     *
     * @param iconYawOffset The icon yaw offset
     * @return This builder
     */
    public EclipseHologramBuilder iconYawOffset(double iconYawOffset) {
        this.iconYawOffset = iconYawOffset;
        return this;
    }

    /**
     * Sets the icon pitch offset.
     *
     * @param iconPitchOffset The icon pitch offset
     * @return This builder
     */
    public EclipseHologramBuilder iconPitchOffset(double iconPitchOffset) {
        this.iconPitchOffset = iconPitchOffset;
        return this;
    }

    /**
     * Sets the fade in duration in ticks.
     *
     * @param fadeInTicks The fade in duration
     * @return This builder
     */
    public EclipseHologramBuilder fadeInTicks(int fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
        return this;
    }

    /**
     * Sets the fade out duration in ticks.
     *
     * @param fadeOutTicks The fade out duration
     * @return This builder
     */
    public EclipseHologramBuilder fadeOutTicks(int fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
        return this;
    }

    /**
     * Builds the hologram with the configured settings.
     *
     * @return The configured EclipseHologram
     */
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
