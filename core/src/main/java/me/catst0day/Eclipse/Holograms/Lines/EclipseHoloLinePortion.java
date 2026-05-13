package me.catst0day.Eclipse.Holograms.Lines;

import org.bukkit.entity.Player;

public class EclipseHoloLinePortion extends EclipseHoloLine {
    private final String text;
    private final String clickCommand;
    private final String hoverText;
    
    public EclipseHoloLinePortion(String text, String clickCommand, String hoverText) {
        super(EclipseHoloLineType.PORTION);
        this.text = text;
        this.clickCommand = clickCommand;
        this.hoverText = hoverText;
    }
    
    @Override
    public String getText(Player player) {
        return text;
    }
    
    public String getClickCommand() {
        return clickCommand;
    }
    
    public String getHoverText() {
        return hoverText;
    }
    
    public boolean hasClickCommand() {
        return clickCommand != null && !clickCommand.isEmpty();
    }
    
    public boolean hasHoverText() {
        return hoverText != null && !hoverText.isEmpty();
    }
}
