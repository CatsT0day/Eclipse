package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Message extends CommandTemplate {

    private static final Map<UUID, UUID> lastMessaged = new HashMap<>();

    public Message(Eclipse plugin) {
        super(plugin, "msg", List.of("message", "tell", "whisper", "w"), null, true, 0, "Send private message");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("msgUsage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        if (target == player) {
            player.sendMessage(plugin.getMessage("msgSelf"));
            return true;
        }
        if (target.hasPermission("eclipse.msg.toggle") && !target.hasPermission("eclipse.msg.receive")) {
            player.sendMessage(plugin.getMessage("msgDisabled"));
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) message.append(" ");
            message.append(args[i]);
        }

        String senderName = plugin.getChatManager().getDisplayName(player.getUniqueId(), player.getName());
        String targetName = plugin.getChatManager().getDisplayName(target.getUniqueId(), target.getName());
        player.sendMessage(plugin.getMessage("msgSent")
                .replace("%to%", targetName)
                .replace("%message%", message.toString()));
        target.sendMessage(plugin.getMessage("msgReceived")
                .replace("%from%", senderName)
                .replace("%message%", message.toString()));
        lastMessaged.put(player.getUniqueId(), target.getUniqueId());
        lastMessaged.put(target.getUniqueId(), player.getUniqueId());
        for (Player spy : Bukkit.getOnlinePlayers()) {
            if (spy.hasPermission("eclipse.msg.spy") && spy != player && spy != target) {
                spy.sendMessage(plugin.getMessage("msgSpy")
                        .replace("%from%", senderName)
                        .replace("%to%", targetName)
                        .replace("%message%", message.toString()));
            }
        }

        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player unused, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessage("msgUsage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) message.append(" ");
            message.append(args[i]);
        }

        target.sendMessage(plugin.getMessage("msgReceivedConsole")
                .replace("%from%", "Console")
                .replace("%message%", message.toString()));

        sender.sendMessage(plugin.getMessage("msgSent")
                .replace("%to%", target.getName())
                .replace("%message%", message.toString()));

        lastMessaged.put(target.getUniqueId(), null); // Console doesn't have UUID

        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .toList();
        }
        return List.of();
    }

    public static UUID getLastMessaged(UUID playerId) {
        return lastMessaged.get(playerId);
    }

    public static void setLastMessaged(UUID playerId, UUID targetId) {
        lastMessaged.put(playerId, targetId);
    }
}
