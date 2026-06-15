package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Moderation.EclipseModerationManager;
import me.catst0day.Eclipse.Moderation.Punishment;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class History extends CommandTemplate {
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public History(Eclipse plugin) {
        super(plugin, "history", List.of("h", "punishments"), EclipsePermissionManager.EclipsePerm.MODERATION_HISTORY, false, 0, "View punishment history of a player");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!plugin.getModuleManager().isModuleEnabled("moderation")) {
            sender.sendMessage(plugin.getMessage("moduleDisabled"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("historyUsage"));
            return true;
        }

        String targetName = args[0];
        UUID targetUUID = lookupTarget(targetName);
        if (targetUUID == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        EclipseModerationManager mm = plugin.getModerationManager();
        List<Punishment> history = mm.getHistory(targetUUID);

        sender.sendMessage(TextUtil.translateHexAndAlternateColorCodes(
                "&6=== &ePunishment History: " + targetName + " &6==="));
        sender.sendMessage(plugin.getMessage("historyTotal")
                .replace("%count%", String.valueOf(history.size())));

        if (history.isEmpty()) {
            sender.sendMessage(plugin.getMessage("historyEmpty"));
            return true;
        }

        for (Punishment p : history) {
            String type = p.getType().name();
            String date = DATE_FMT.format(new Date(p.getDate()));
            String reason = p.getReason().isEmpty() ? plugin.getMessage("noReason") : p.getReason();
            String status = p.isActive() ? "&aActive" : "&7Inactive";
            String expires = p.getExpiry() > 0 ? " (" + p.getDurationString() + ")" : "";

            String line = " &7#" + p.getId() + " &f" + type + " &7by " + p.getIssuerName()
                    + " &7on " + date + expires + " &f- " + status
                    + " &7- " + reason;
            if (p.isSilent()) line = " &8[silent]" + line;
            sender.sendMessage(TextUtil.translateHexAndAlternateColorCodes(line));
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
