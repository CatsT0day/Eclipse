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

public class Unban extends CommandTemplate {
    public Unban(Eclipse plugin) {
        super(plugin, "unban", List.of("pardon"), EclipsePermissionManager.EclipsePerm.MODERATION_UNBAN, false, 0, "Unban a player");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!plugin.getModuleManager().isModuleEnabled("moderation")) {
            sender.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("unbanUsage"));
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

        if (mm.getActiveBan(targetUUID) == null) {
            sender.sendMessage(plugin.getMessage("notBanned").replace("%player%", targetName));
            return true;
        }

        mm.unpunish(targetUUID, PunishmentType.BAN, issuerUUID, sender.getName());
        String msg = plugin.getMessage("unbanSuccess").replace("%player%", targetName);
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
