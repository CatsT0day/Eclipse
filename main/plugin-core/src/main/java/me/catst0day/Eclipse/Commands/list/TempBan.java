package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Moderation.EclipseModerationManager;
import me.catst0day.Eclipse.Moderation.Punishment;
import me.catst0day.Eclipse.Moderation.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TempBan extends CommandTemplate {
    public TempBan(Eclipse plugin) {
        super(plugin, "tempban", List.of("tban"), EclipsePermissionManager.EclipsePerm.MODERATION_TEMPBAN, false, 0, "Temporarily ban a player");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!plugin.getModuleManager().isModuleEnabled("moderation")) {
            sender.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessage("tempBanUsage"));
            return true;
        }

        String targetName = args[0];
        String durationStr = args[1];
        boolean silent = args.length > 2 && args[args.length - 1].equals("-s");
        String reason;
        if (silent) {
            reason = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length - 1)) : "";
        } else {
            reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "";
        }

        long duration = Punishment.parseDuration(durationStr);
        if (duration <= 0) {
            sender.sendMessage(plugin.getMessage("invalidDuration"));
            return true;
        }

        UUID targetUUID = lookupTarget(targetName);
        if (targetUUID == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        UUID issuerUUID = sender instanceof Player p ? p.getUniqueId() : new UUID(0, 0);
        String issuerName = sender.getName();

        EclipseModerationManager mm = plugin.getModerationManager();
        Punishment p = mm.punish(PunishmentType.TEMP_BAN, targetUUID, targetName, issuerUUID, issuerName, reason, duration, silent);
        if (p != null) {
            mm.broadcastPunishment(p, targetName);
        }
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
        if (args.length == 2) {
            return List.of("1d", "7d", "30d", "1h", "2h", "12h", "1w", "2w");
        }
        return List.of();
    }

    private UUID lookupTarget(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p != null) return p.getUniqueId();
        return Bukkit.getOfflinePlayerIfCached(name) != null ? Bukkit.getOfflinePlayerIfCached(name).getUniqueId() : null;
    }
}
