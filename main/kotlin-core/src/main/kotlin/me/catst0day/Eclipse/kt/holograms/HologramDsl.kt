package me.catst0day.Eclipse.kt.holograms

import me.catst0day.Eclipse.Eclipse
import me.catst0day.Eclipse.Holograms.EclipseHologram
import me.catst0day.Eclipse.Holograms.EclipseHologramBuilder
import org.bukkit.Location
import org.bukkit.Material




@DslMarker
annotation class HologramDsl


fun hologram(name: String, location: Location, block: HologramBuilder.() -> Unit): EclipseHologram {
    return HologramBuilder(name, location).apply(block).build()
}


fun Eclipse.createHologram(name: String, location: Location, block: HologramBuilder.() -> Unit): EclipseHologram {
    val hologram = hologram(name, location, block)
    hologramManager.createHologram(name, location, hologram.lines)
    return hologramManager.getHologram(name) ?: hologram
}


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

    
    fun line(text: String) {
        lines.add(text)
    }

    
    fun lines(vararg texts: String) {
        lines.addAll(texts)
    }

    
    fun lines(texts: List<String>) {
        lines.addAll(texts)
    }

    
    fun clickable(command: String, cost: Double = 0.0) {
        this.clickable = true
        this.clickCommand = command
        this.clickCost = cost
    }

    
    fun showParticles(show: Boolean) {
        this.showParticles = show
    }

    
    fun enabled(enabled: Boolean) {
        this.enabled = enabled
    }

    
    fun permission(permission: String) {
        this.permission = permission
    }

    
    fun viewDistance(distance: Int) {
        this.viewDistance = distance
    }

    
    fun alwaysVisible(alwaysVisible: Boolean) {
        this.alwaysVisible = alwaysVisible
    }

    
    fun updateInterval(interval: Int) {
        this.updateInterval = interval
    }

    
    fun updateRange(range: Int) {
        this.updateRange = range
    }

    
    fun lineOfSight(check: Boolean) {
        this.lineOfSight = check
    }

    
    fun followType(type: EclipseHologram.FollowType) {
        this.followType = type
    }

    
    fun doubleSided(doubleSided: Boolean) {
        this.doubleSided = doubleSided
    }

    
    fun textAlignment(alignment: EclipseHologram.TextAlignment) {
        this.textAlignment = alignment
    }

    
    fun textShadow(shadow: Boolean) {
        this.textShadow = shadow
    }

    
    fun textAlpha(alpha: Int) {
        this.textAlpha = alpha
    }

    
    fun textWidth(width: Int) {
        this.textWidth = width
    }

    
    fun textFillerWidth(width: Int) {
        this.textFillerWidth = width
    }

    
    fun textSeeThrough(seeThrough: Boolean) {
        this.textSeeThrough = seeThrough
    }

    
    fun lightLevel(level: Int) {
        this.lightLevel = level
    }

    
    fun backgroundColor(color: String) {
        this.backgroundColor = color
    }

    
    fun backgroundAlpha(alpha: Int) {
        this.backgroundAlpha = alpha
    }

    
    fun scale(scale: Double) {
        this.scale = scale
    }

    
    fun yawOffset(offset: Double) {
        this.yawOffset = offset
    }

    
    fun pitchOffset(offset: Double) {
        this.pitchOffset = offset
    }

    
    fun boardEnabled(enabled: Boolean) {
        this.boardEnabled = enabled
    }

    
    fun boardMaterial(material: Material) {
        this.boardMaterial = material
    }

    
    fun boardScale(scale: Double) {
        this.boardScale = scale
    }

    
    fun boardYawOffset(offset: Double) {
        this.boardYawOffset = offset
    }

    
    fun boardPitchOffset(offset: Double) {
        this.boardPitchOffset = offset
    }

    
    fun boardThickness(thickness: Double) {
        this.boardThickness = thickness
    }

    
    fun iconScale(scale: Double) {
        this.iconScale = scale
    }

    
    fun iconYawOffset(offset: Double) {
        this.iconYawOffset = offset
    }

    
    fun iconPitchOffset(offset: Double) {
        this.iconPitchOffset = offset
    }

    
    fun fadeInTicks(ticks: Int) {
        this.fadeInTicks = ticks
    }

    
    fun fadeOutTicks(ticks: Int) {
        this.fadeOutTicks = ticks
    }

    
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




fun simpleHologram(name: String, location: Location, vararg lines: String): EclipseHologram {
    return hologram(name, location) {
        lines(*lines)
    }
}


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


fun animatedHologram(
    name: String,
    location: Location,
    vararg animatedLines: String
): EclipseHologram {
    return hologram(name, location) {
        lines(*animatedLines)
    }
}
