package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Moderation.EclipseModerationManager;
import me.catst0day.Eclipse.Moderation.Punishment;
import me.catst0day.Eclipse.Moderation.PunishmentType;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Kick extends CommandTemplate {
    public Kick(Eclipse plugin) {
        super(plugin, "kick", List.of(), EclipsePermissionManager.EclipsePerm.MODERATION_KICK, false, 0, "Kick a player from the server");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!plugin.getModuleManager().isModuleEnabled("moderation")) {
            sender.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("kickUsage"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        boolean silent = args.length > 1 && args[args.length - 1].equals("-s");
        String reason;
        if (silent) {
            reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1)) : "";
        } else {
            reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "";
        }

        UUID issuerUUID = sender instanceof Player p ? p.getUniqueId() : new UUID(0, 0);
        String issuerName = sender.getName();

        EclipseModerationManager mm = plugin.getModerationManager();
        Punishment p = mm.punish(PunishmentType.KICK, target.getUniqueId(), targetName,
                issuerUUID, issuerName, reason, 0, silent);

        String kickMsg = plugin.getMessage("kickMessage")
                .replace("%reason%", reason.isEmpty() ? plugin.getMessage("noReason") : reason)
                .replace("%staff%", issuerName);
        target.kickPlayer(TextUtil.translateHexAndAlternateColorCodes(kickMsg));

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
        return List.of();
    }
}
