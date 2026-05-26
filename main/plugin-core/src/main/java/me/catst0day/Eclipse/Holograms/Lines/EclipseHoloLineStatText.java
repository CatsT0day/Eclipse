package me.catst0day.Eclipse.Holograms.Lines;

import org.bukkit.entity.Player;

public class EclipseHoloLineStatText extends EclipseHoloLine {
    private final String text;
    
    public EclipseHoloLineStatText(String text) {
        super(EclipseHoloLineType.STATIC_TEXT);
        this.text = text;
    }
    
    @Override
    public String getText(Player player) {
        return text;
    }
    
    public String getRawText() {
        return text;
    }
}
