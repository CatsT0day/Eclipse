package me.catst0day.Eclipse.Holograms.Hover;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import org.bukkit.entity.Player;

public class EclipseHoloHoverData {
    private final Player player;
    private final EclipsePlr eclipsePlayer;
    private final int lineIndex;
    private final String hoverText;
    private final String clickCommand;
    private long hoverStartTime;

    public EclipseHoloHoverData(Player player, int lineIndex, String hoverText, String clickCommand) {
        this.player = player;
        this.eclipsePlayer = Eclipse.getI().getPlayer(player.getUniqueId());
        this.lineIndex = lineIndex;
        this.hoverText = hoverText;
        this.clickCommand = clickCommand;
        this.hoverStartTime = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public EclipsePlr getEclipsePlayer() {
        return eclipsePlayer;
    }
    
    public int getLineIndex() {
        return lineIndex;
    }
    
    public String getHoverText() {
        return hoverText;
    }
    
    public String getClickCommand() {
        return clickCommand;
    }
    
    public long getHoverStartTime() {
        return hoverStartTime;
    }
    
    public void setHoverStartTime(long hoverStartTime) {
        this.hoverStartTime = hoverStartTime;
    }
    
    public long getHoverDuration() {
        return System.currentTimeMillis() - hoverStartTime;
    }
}
