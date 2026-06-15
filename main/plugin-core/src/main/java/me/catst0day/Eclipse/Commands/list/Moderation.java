package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ModerationCommand extends CommandTemplate {
    public ModerationCommand(Eclipse plugin) {
        super(plugin, "moderation", List.of("mod", "mods"),
                EclipsePermissionManager.EclipsePerm.MODERATION_ADMIN, false, 0, "Moderation system commands");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!plugin.getModuleManager().isModuleEnabled("moderation")) {
            sender.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info", "status" -> sendStatus(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(TextUtil.translateHexAndAlternateColorCodes(
                "&6=== &eModeration Commands &6==="));
        sender.sendMessage(" &6/ban &f<player> [reason] &7- Permanently ban");
        sender.sendMessage(" &6/tempban &f<player> <duration> [reason] &7- Temporary ban");
        sender.sendMessage(" &6/unban &f<player> &7- Unban a player");
        sender.sendMessage(" &6/mute &f<player> [reason] &7- Permanently mute");
        sender.sendMessage(" &6/tempmute &f<player> <duration> [reason] &7- Temporary mute");
        sender.sendMessage(" &6/unmute &f<player> &7- Unmute a player");
        sender.sendMessage(" &6/kick &f<player> [reason] &7- Kick a player");
        sender.sendMessage(" &6/warn &f<player> [reason] &7- Warn a player");
        sender.sendMessage(" &6/history &f<player> &7- View punishment history");
        sender.sendMessage(" &6/clearwarnings &f<player> &7- Clear warnings");
        sender.sendMessage(" &7Append &o-s&7 for silent punishments");
    }

    private void sendStatus(CommandSender sender) {
        sender.sendMessage(plugin.getMessage("moderationStatus"));
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("info", "status");
        }
        return List.of();
    }
}
