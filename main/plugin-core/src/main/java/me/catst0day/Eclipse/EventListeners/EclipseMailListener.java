package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Managers.EclipseModuleManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EclipseMailListener implements Listener {
    private final Eclipse plugin;

    public EclipseMailListener(Eclipse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getModuleManager().isModuleEnabled("mail")) {
            return;
        }

        if (!plugin.getConfig().getBoolean("mail.notifyOnJoin", true)) {
            return;
        }

        Player player = event.getPlayer();
        int unreadCount = plugin.getMailManager().getUnreadCount(player.getUniqueId());

        if (unreadCount > 0) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.sendMessage(plugin.getMessage("mailJoinNotification")
                            .replace("%count%", String.valueOf(unreadCount)));
                }
            }, 20L * 3); // 3 seconds delay
        }
    }
}
