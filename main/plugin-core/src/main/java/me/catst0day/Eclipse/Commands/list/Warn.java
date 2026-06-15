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

public class Warn extends CommandTemplate {
    public Warn(Eclipse plugin) {
        super(plugin, "warn", List.of("warning"), EclipsePermissionManager.EclipsePerm.MODERATION_WARN, false, 0, "Warn a player");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!plugin.getModuleManager().isModuleEnabled("moderation")) {
            sender.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("warnUsage"));
            return true;
        }

        String targetName = args[0];
        boolean silent = args.length > 1 && args[args.length - 1].equals("-s");
        String reason;
        if (silent) {
            reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1)) : "";
        } else {
            reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "";
        }

        UUID targetUUID = lookupTarget(targetName);
        if (targetUUID == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        UUID issuerUUID = sender instanceof Player p ? p.getUniqueId() : new UUID(0, 0);
        String issuerName = sender.getName();

        EclipseModerationManager mm = plugin.getModerationManager();
        Punishment p = mm.punish(PunishmentType.WARN, targetUUID, targetName, issuerUUID, issuerName, reason, 0, silent);
        if (p != null) {
            mm.broadcastPunishment(p, targetName);
            Player target = Bukkit.getPlayer(targetUUID);
            if (target != null && target.isOnline()) {
                String warnMsg = plugin.getMessage("warnReceived")
                        .replace("%reason%", reason.isEmpty() ? plugin.getMessage("noReason") : reason)
                        .replace("%staff%", issuerName);
                target.sendMessage(warnMsg);
            }
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
        return List.of();
    }

    private UUID lookupTarget(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p != null) return p.getUniqueId();
        return Bukkit.getOfflinePlayerIfCached(name) != null ? Bukkit.getOfflinePlayerIfCached(name).getUniqueId() : null;
    }
}
