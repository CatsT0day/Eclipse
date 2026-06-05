package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Managers.EclipseModuleManager;
import org.bukkit.event.Listener;

public class EclipseAuctionListener implements Listener {
    private final Eclipse plugin;

    public EclipseAuctionListener(Eclipse plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    private void startCleanupTask() {
        // Run cleanup every hour
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (plugin.getModuleManager().isModuleEnabled("auction")) {
                plugin.getAuctionManager().cleanupExpiredListings();
            }
        }, 20L * 60 * 60, 20L * 60 * 60); // 1 hour delay, then every hour
    }
}
