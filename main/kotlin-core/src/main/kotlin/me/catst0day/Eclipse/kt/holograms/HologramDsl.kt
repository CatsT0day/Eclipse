package me.catst0day.Eclipse.kt.holograms

import me.catst0day.Eclipse.Eclipse
import me.catst0day.Eclipse.Holograms.EclipseHologram
import me.catst0day.Eclipse.Holograms.EclipseHologramBuilder
import org.bukkit.Location
import org.bukkit.Material

/**
 * Kotlin DSL for creating and configuring holograms with a clean, fluent API.
 * Provides type-safe builders and extension functions for comfortable hologram creation.
 */

/**
 * DSL marker to prevent accidental nesting of hologram builders
 */
@DslMarker
annotation class HologramDsl

/**
 * Extension function to create a hologram using the DSL
 */
fun hologram(name: String, location: Location, block: HologramBuilder.() -> Unit): EclipseHologram {
    return HologramBuilder(name, location).apply(block).build()
}

/**
 * Extension function to create and register a hologram using the DSL
 */
fun Eclipse.createHologram(name: String, location: Location, block: HologramBuilder.() -> Unit): EclipseHologram {
    val hologram = hologram(name, location, block)
    hologramManager.createHologram(name, location, hologram.lines)
    return hologramManager.getHologram(name) ?: hologram
}

/**
 * Builder class for creating holograms using Kotlin DSL
 */
@HologramDsl
class HologramBuilder(private val name: String, private val location: Location) {
    private val lines = mutableListOf<String>()
    private var clickable = false
    private var clickCommand = ""
    private var clickCost = 0.0
    private var showParticles = true
    private var enabled = true
    private var permission: String? = null
    private var viewDistance = 48
    private var alwaysVisible = true
    private var updateInterval = 20
    private var updateRange = 48
    private var lineOfSight = false
    private var followType = EclipseHologram.FollowType.FIXED
    private var doubleSided = false
    private var textAlignment = EclipseHologram.TextAlignment.LEFT
    private var textShadow = false
    private var textAlpha = 255
    private var textWidth = 200
    private var textFillerWidth = 0
    private var textSeeThrough = false
    private var lightLevel = -1
    private var backgroundColor: String? = null
    private var backgroundAlpha = 0
    private var scale = 1.0
    private var yawOffset = 0.0
    private var pitchOffset = 0.0
    private var boardEnabled = false
    private var boardMaterial: Material? = null
    private var boardScale = 1.0
    private var boardYawOffset = 0.0
    private var boardPitchOffset = 0.0
    private var boardThickness = 0.1
    private var iconScale = 1.0
    private var iconYawOffset = 0.0
    private var iconPitchOffset = 0.0
    private var fadeInTicks = 0
    private var fadeOutTicks = 0

    /**
     * Add a line to the hologram
     */
    fun line(text: String) {
        lines.add(text)
    }

    /**
     * Add multiple lines to the hologram
     */
    fun lines(vararg texts: String) {
        lines.addAll(texts)
    }

    /**
     * Add multiple lines using a list
     */
    fun lines(texts: List<String>) {
        lines.addAll(texts)
    }

    /**
     * Make the hologram clickable with a command
     */
    fun clickable(command: String, cost: Double = 0.0) {
        this.clickable = true
        this.clickCommand = command
        this.clickCost = cost
    }

    /**
     * Configure particles visibility
     */
    fun showParticles(show: Boolean) {
        this.showParticles = show
    }

    /**
     * Set whether the hologram is enabled
     */
    fun enabled(enabled: Boolean) {
        this.enabled = enabled
    }

    /**
     * Set the permission required to view the hologram
     */
    fun permission(permission: String) {
        this.permission = permission
    }

    /**
     * Set the view distance in blocks
     */
    fun viewDistance(distance: Int) {
        this.viewDistance = distance
    }

    /**
     * Set whether the hologram is always visible
     */
    fun alwaysVisible(alwaysVisible: Boolean) {
        this.alwaysVisible = alwaysVisible
    }

    /**
     * Set the update interval in ticks
     */
    fun updateInterval(interval: Int) {
        this.updateInterval = interval
    }

    /**
     * Set the update range in blocks
     */
    fun updateRange(range: Int) {
        this.updateRange = range
    }

    /**
     * Set whether line of sight is checked
     */
    fun lineOfSight(check: Boolean) {
        this.lineOfSight = check
    }

    /**
     * Set the follow type (billboard behavior)
     */
    fun followType(type: EclipseHologram.FollowType) {
        this.followType = type
    }

    /**
     * Set whether text is double-sided
     */
    fun doubleSided(doubleSided: Boolean) {
        this.doubleSided = doubleSided
    }

    /**
     * Set the text alignment
     */
    fun textAlignment(alignment: EclipseHologram.TextAlignment) {
        this.textAlignment = alignment
    }

    /**
     * Set whether text has shadow
     */
    fun textShadow(shadow: Boolean) {
        this.textShadow = shadow
    }

    /**
     * Set the text alpha (transparency 0-255)
     */
    fun textAlpha(alpha: Int) {
        this.textAlpha = alpha
    }

    /**
     * Set the text width
     */
    fun textWidth(width: Int) {
        this.textWidth = width
    }

    /**
     * Set the text filler width
     */
    fun textFillerWidth(width: Int) {
        this.textFillerWidth = width
    }

    /**
     * Set whether text is see-through
     */
    fun textSeeThrough(seeThrough: Boolean) {
        this.textSeeThrough = seeThrough
    }

    /**
     * Set the light level (-1 to 15)
     */
    fun lightLevel(level: Int) {
        this.lightLevel = level
    }

    /**
     * Set the background color (hex format)
     */
    fun backgroundColor(color: String) {
        this.backgroundColor = color
    }

    /**
     * Set the background alpha (0-255)
     */
    fun backgroundAlpha(alpha: Int) {
        this.backgroundAlpha = alpha
    }

    /**
     * Set the hologram scale
     */
    fun scale(scale: Double) {
        this.scale = scale
    }

    /**
     * Set the yaw offset
     */
    fun yawOffset(offset: Double) {
        this.yawOffset = offset
    }

    /**
     * Set the pitch offset
     */
    fun pitchOffset(offset: Double) {
        this.pitchOffset = offset
    }

    /**
     * Enable the background board
     */
    fun boardEnabled(enabled: Boolean) {
        this.boardEnabled = enabled
    }

    /**
     * Set the board material
     */
    fun boardMaterial(material: Material) {
        this.boardMaterial = material
    }

    /**
     * Set the board scale
     */
    fun boardScale(scale: Double) {
        this.boardScale = scale
    }

    /**
     * Set the board yaw offset
     */
    fun boardYawOffset(offset: Double) {
        this.boardYawOffset = offset
    }

    /**
     * Set the board pitch offset
     */
    fun boardPitchOffset(offset: Double) {
        this.boardPitchOffset = offset
    }

    /**
     * Set the board thickness
     */
    fun boardThickness(thickness: Double) {
        this.boardThickness = thickness
    }

    /**
     * Set the icon scale
     */
    fun iconScale(scale: Double) {
        this.iconScale = scale
    }

    /**
     * Set the icon yaw offset
     */
    fun iconYawOffset(offset: Double) {
        this.iconYawOffset = offset
    }

    /**
     * Set the icon pitch offset
     */
    fun iconPitchOffset(offset: Double) {
        this.iconPitchOffset = offset
    }

    /**
     * Set the fade in duration in ticks
     */
    fun fadeInTicks(ticks: Int) {
        this.fadeInTicks = ticks
    }

    /**
     * Set the fade out duration in ticks
     */
    fun fadeOutTicks(ticks: Int) {
        this.fadeOutTicks = ticks
    }

    /**
     * Build the hologram with the configured settings
     */
    fun build(): EclipseHologram {
        return EclipseHologramBuilder.create(name, location)
            .lines(lines)
            .also {
                if (clickable) it.clickable(clickCommand, clickCost)
                it.showParticles(showParticles)
                it.enabled(enabled)
                permission?.let { perm -> it.permission(perm) }
                it.viewDistance(viewDistance)
                it.alwaysVisible(alwaysVisible)
                it.updateInterval(updateInterval)
                it.updateRange(updateRange)
                it.lineOfSight(lineOfSight)
                it.followType(followType)
                it.doubleSided(doubleSided)
                it.textAlignment(textAlignment)
                it.textShadow(textShadow)
                it.textAlpha(textAlpha)
                it.textWidth(textWidth)
                it.textFillerWidth(textFillerWidth)
                it.textSeeThrough(textSeeThrough)
                it.lightLevel(lightLevel)
                backgroundColor?.let { color -> it.backgroundColor(color) }
                it.backgroundAlpha(backgroundAlpha)
                it.scale(scale)
                it.yawOffset(yawOffset)
                it.pitchOffset(pitchOffset)
                it.boardEnabled(boardEnabled)
                boardMaterial?.let { mat -> it.boardMaterial(mat) }
                it.boardScale(boardScale)
                it.boardYawOffset(boardYawOffset)
                it.boardPitchOffset(boardPitchOffset)
                it.boardThickness(boardThickness)
                it.iconScale(iconScale)
                it.iconYawOffset(iconYawOffset)
                it.iconPitchOffset(iconPitchOffset)
                it.fadeInTicks(fadeInTicks)
                it.fadeOutTicks(fadeOutTicks)
            }
            .build()
    }
}

/**
 * Convenience functions for common hologram configurations
 */

/**
 * Create a simple text hologram with minimal configuration
 */
fun simpleHologram(name: String, location: Location, vararg lines: String): EclipseHologram {
    return hologram(name, location) {
        lines(*lines)
    }
}

/**
 * Create a clickable hologram that executes a command
 */
fun clickableHologram(
    name: String,
    location: Location,
    command: String,
    cost: Double = 0.0,
    vararg lines: String
): EclipseHologram {
    return hologram(name, location) {
        lines(*lines)
        clickable(command, cost)
    }
}

/**
 * Create a temporary hologram that fades in and out
 */
fun temporaryHologram(
    name: String,
    location: Location,
    fadeInTicks: Int = 20,
    fadeOutTicks: Int = 20,
    vararg lines: String
): EclipseHologram {
    return hologram(name, location) {
        lines(*lines)
        this.fadeInTicks(fadeInTicks)
        this.fadeOutTicks(fadeOutTicks)
    }
}

/**
 * Create a hologram with animated text using animation tags
 */
fun animatedHologram(
    name: String,
    location: Location,
    vararg animatedLines: String
): EclipseHologram {
    return hologram(name, location) {
        lines(*animatedLines)
    }
}
