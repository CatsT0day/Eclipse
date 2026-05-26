package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Managers.EclipseChatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class EclipseChatListener implements Listener {

    private final Eclipse plugin;
    private final EclipseChatManager chatManager;

    public EclipseChatListener(Eclipse plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getModuleManager().isModuleEnabled("chat")) return;
        if (!chatManager.isEnabled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        String language = plugin.getConfig().getString("language", "EN");
        if (chatManager.isPlayerMuted(player.getUniqueId())) {
            event.setCancelled(true);
            long remainingTime = chatManager.getRemainingMuteTime(player.getUniqueId());
            player.sendMessage(plugin.getMessage("chatMuted")
                    .replace("%time%", String.valueOf(remainingTime)));
            return;
        }
        if (chatManager.isMessageBlocked(message, language)) {
            if (chatManager.shouldWarnPlayer()) {
                player.sendMessage(chatManager.getWarnMessage());
            }

            if (chatManager.shouldAutoMute()) {
                chatManager.mutePlayer(player.getUniqueId(), chatManager.getMuteDuration());
                player.sendMessage(plugin.getMessage("chatAutoMuted")
                        .replace("%duration%", String.valueOf(chatManager.getMuteDuration())));
            }

            if (chatManager.shouldLogBlockedMessages()) {
                plugin.getLogger().warning("[CHAT] " + player.getName() + " tried to send blocked message: " + message);
            }

            event.setCancelled(true);
            return;
        }
        String filteredMessage = chatManager.filterMessage(message, language);
        event.setMessage(filteredMessage);
        EclipseChatManager.ChatMode chatMode = chatManager.getPlayerChatMode(player.getUniqueId());
        String format = chatManager.getChatFormat(chatMode);
        String displayName = chatManager.getDisplayName(player.getUniqueId(), player.getName());
        format = format.replace("%player%", displayName)
                       .replace("%message%", filteredMessage);
        event.setFormat(format);
        if (chatMode == EclipseChatManager.ChatMode.LOCAL) {
            event.getRecipients().clear();
            event.getRecipients().addAll(getNearbyPlayers(player));
        } else if (chatMode == EclipseChatManager.ChatMode.STAFF) {
            event.getRecipients().clear();
            event.getRecipients().addAll(getStaffPlayers());
        }
    }

    private List<Player> getNearbyPlayers(Player player) {
        return player.getWorld().getNearbyEntities(player.getLocation(), 
                chatManager.getLocalChatRadius(), chatManager.getLocalChatRadius(), chatManager.getLocalChatRadius())
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .toList();
    }

    private List<Player> getStaffPlayers() {
        return (List<Player>) plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("eclipse.chat.staff"))
                .toList();
    }
}
