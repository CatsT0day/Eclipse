package me.catst0day.Eclipse.Holograms.Lines;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EclipseHoloLineIcon extends EclipseHoloLine {
    private final Material material;
    private final int data;
    private final boolean isHead;
    private final String headValue; // player name or base64
    
    public EclipseHoloLineIcon(Material material, int data) {
        super(EclipseHoloLineType.ICON);
        this.material = material;
        this.data = data;
        this.isHead = false;
        this.headValue = null;
    }
    
    public EclipseHoloLineIcon(String headValue) {
        super(EclipseHoloLineType.ICON);
        this.material = Material.PLAYER_HEAD;
        this.data = 0;
        this.isHead = true;
        this.headValue = headValue;
    }
    
    @Override
    public String getText(Player player) {
        return "[ICON]";
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public int getData() {
        return data;
    }
    
    public boolean isHead() {
        return isHead;
    }
    
    public String getHeadValue() {
        return headValue;
    }
}
