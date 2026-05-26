package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Reply extends CommandTemplate {

    public Reply(Eclipse plugin) {
        super(plugin, "reply", List.of("r"), null, true, 0, "Reply to last message");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        UUID lastTargetId = Message.getLastMessaged(player.getUniqueId());
        
        if (lastTargetId == null) {
            player.sendMessage(plugin.getMessage("replyNoOne"));
            return true;
        }

        Player target = Bukkit.getPlayer(lastTargetId);
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getMessage("replyOffline"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getMessage("replyUsage"));
            return true;
        }

        if (target.hasPermission("eclipse.msg.toggle") && !target.hasPermission("eclipse.msg.receive")) {
            player.sendMessage(plugin.getMessage("msgDisabled"));
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) message.append(" ");
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
        Message.setLastMessaged(player.getUniqueId(), target.getUniqueId());
        Message.setLastMessaged(target.getUniqueId(), player.getUniqueId());
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
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}
