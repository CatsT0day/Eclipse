package me.catst0day.Eclipse.Holograms.Lines;

import org.bukkit.entity.Player;

public class EclipseHoloLineDynText extends EclipseHoloLine {
    private final String placeholder;
    
    public EclipseHoloLineDynText(String placeholder) {
        super(EclipseHoloLineType.DYNAMIC_TEXT);
        this.placeholder = placeholder;
    }
    
    @Override
    public String getText(Player player) {
        return placeholder;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
}
