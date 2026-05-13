package me.catst0day.Eclipse.Holograms.Hover;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EclipseHoloHoverModule {
    private final EclipseHologram hologram;
    private final Map<UUID, EclipseHoloHoverData> hoveringPlayers;
    private boolean enabled;
    private long hoverDelayMs = 500;
    
    public EclipseHoloHoverModule(EclipseHologram hologram) {
        this.hologram = hologram;
        this.hoveringPlayers = new HashMap<>();
        this.enabled = false;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public long getHoverDelayMs() {
        return hoverDelayMs;
    }
    
    public void setHoverDelayMs(long hoverDelayMs) {
        this.hoverDelayMs = hoverDelayMs;
    }
    
    public void onHoverStart(Player player, int lineIndex, String hoverText, String clickCommand) {
        if (!enabled) return;

        EclipsePlr ePlr = Eclipse.getI().getPlayer(player.getUniqueId());
        EclipseHoloHoverData data = new EclipseHoloHoverData(player, lineIndex, hoverText, clickCommand);
        hoveringPlayers.put(player.getUniqueId(), data);
    }

    public void onHoverEnd(Player player) {
        EclipseHoloHoverData data = hoveringPlayers.remove(player.getUniqueId());
        if (data != null && data.getHoverDuration() >= hoverDelayMs) {
            if (data.getClickCommand() != null && !data.getClickCommand().isEmpty()) {
                player.performCommand(data.getClickCommand());
            }
        }
    }

    public EclipseHoloHoverData getHoverData(Player player) {
        return hoveringPlayers.get(player.getUniqueId());
    }

    public EclipseHoloHoverData getHoverData(EclipsePlr player) {
        return hoveringPlayers.get(player.getUniqueId());
    }

    public boolean isHovering(Player player) {
        return hoveringPlayers.containsKey(player.getUniqueId());
    }

    public boolean isHovering(EclipsePlr player) {
        return hoveringPlayers.containsKey(player.getUniqueId());
    }
    
    public void clear() {
        hoveringPlayers.clear();
    }
}
