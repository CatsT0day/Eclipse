package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Moderation.EclipseModerationManager;
import me.catst0day.Eclipse.Moderation.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Unmute extends CommandTemplate {
    public Unmute(Eclipse plugin) {
        super(plugin, "unmute", List.of(), EclipsePermissionManager.EclipsePerm.MODERATION_UNMUTE, false, 0, "Unmute a player");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!plugin.getModuleManager().isModuleEnabled("moderation")) {
            sender.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("unmuteUsage"));
            return true;
        }

        String targetName = args[0];
        UUID targetUUID = lookupTarget(targetName);
        if (targetUUID == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        UUID issuerUUID = sender instanceof Player p ? p.getUniqueId() : new UUID(0, 0);
        EclipseModerationManager mm = plugin.getModerationManager();

        if (mm.getActiveMute(targetUUID) == null) {
            sender.sendMessage(plugin.getMessage("notMuted").replace("%player%", targetName));
            return true;
        }

        mm.unpunish(targetUUID, PunishmentType.MUTE, issuerUUID, sender.getName());
        String msg = plugin.getMessage("unmuteSuccess").replace("%player%", targetName);
        sender.sendMessage(msg);
        Bukkit.getConsoleSender().sendMessage(msg);
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .toList();
        }
        return List.of();
    }

    private UUID lookupTarget(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p != null) return p.getUniqueId();
        return Bukkit.getOfflinePlayerIfCached(name) != null ? Bukkit.getOfflinePlayerIfCached(name).getUniqueId() : null;
    }
}
