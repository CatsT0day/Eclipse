package me.catst0day.Eclipse.Holograms.Lines;

import org.bukkit.entity.Player;

public abstract class EclipseHoloLine {
    protected final EclipseHoloLineType type;
    
    public EclipseHoloLine(EclipseHoloLineType type) {
        this.type = type;
    }
    
    public EclipseHoloLineType getType() {
        return type;
    }
    
    public abstract String getText(Player player);
}
