package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipseChatManager;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Chat extends CommandTemplate {

    public Chat(Eclipse plugin) {
        super(plugin, "chat", List.of("ch", "channel"), EclipsePermissionManager.EclipsePerm.MAIN, true, 0, "Change chat mode");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (plugin.getModuleManager().isModuleEnabled("chat")) {
            player.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        return execute(player, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (plugin.getModuleManager().isModuleEnabled("chat")) {
            sender.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        return execute(sender, args);
    }

    private boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
            return true;
        }

        if (args.length == 0) {
            EclipseChatManager.ChatMode currentMode = plugin.getChatManager().getPlayerChatMode(player.getUniqueId());
            sender.sendMessage(plugin.getMessage("chatCurrentMode")
                    .replace("%mode%", currentMode.name().toLowerCase()));
            return true;
        }

        String modeArg = args[0].toLowerCase();
        EclipseChatManager.ChatMode newMode;

        switch (modeArg) {
            case "global":
            case "g":
                newMode = EclipseChatManager.ChatMode.GLOBAL;
                break;
            case "local":
            case "l":
                newMode = EclipseChatManager.ChatMode.LOCAL;
                break;
            case "staff":
            case "s":
                if (!player.hasPermission("eclipse.chat.staff")) {
                    sender.sendMessage(plugin.getMessage("noPermission"));
                    return true;
                }
                newMode = EclipseChatManager.ChatMode.STAFF;
                break;
            default:
                sender.sendMessage(plugin.getMessage("chatInvalidMode")
                        .replace("%mode%", args[0]));
                return true;
        }

        plugin.getChatManager().setPlayerChatMode(player.getUniqueId(), newMode);
        sender.sendMessage(plugin.getMessage("chatModeChanged")
                .replace("%mode%", newMode.name().toLowerCase()));

        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> modes = List.of("global", "local");
            if (player.hasPermission("eclipse.chat.staff")) {
                modes = List.of("global", "local", "staff");
            }
            return modes.stream()
                    .filter(mode -> mode.startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
