package me.catst0day.Eclipse.Moderation;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class EclipseModerationListener implements Listener {
    private final Eclipse plugin;
    private final EclipseModerationManager manager;

    public EclipseModerationListener(Eclipse plugin) {
        this.plugin = plugin;
        this.manager = plugin.getModerationManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Punishment ban = manager.getActiveBan(event.getPlayer().getUniqueId());
        if (ban != null) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, manager.buildBanMessage(ban));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Punishment mute = manager.getActiveMute(player.getUniqueId());
        if (mute != null) {
            event.setCancelled(true);
            player.sendMessage(manager.buildMuteMessage(mute));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Punishment mute = manager.getActiveMute(event.getPlayer().getUniqueId());
        if (mute != null) {
            event.getPlayer().sendMessage(manager.buildMuteMessage(mute));
        }
    }
}
