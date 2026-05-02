package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class EclipseOnItemPickupEvent implements Listener {
    protected final Eclipse plugin;

    public EclipseOnItemPickupEvent(Eclipse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.isGodMode(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessage("godModeItemPickupDenied"));
        }
    }
}
