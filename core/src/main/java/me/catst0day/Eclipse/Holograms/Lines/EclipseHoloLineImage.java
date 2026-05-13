package me.catst0day.Eclipse.Holograms.Lines;

import org.bukkit.entity.Player;

public class EclipseHoloLineImage extends EclipseHoloLine {
    private final String url;
    private final int width;
    private final int height;
    
    public EclipseHoloLineImage(String url, int width, int height) {
        super(EclipseHoloLineType.IMAGE);
        this.url = url;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public String getText(Player player) {
        return "[IMAGE]";
    }
    
    public String getUrl() {
        return url;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
