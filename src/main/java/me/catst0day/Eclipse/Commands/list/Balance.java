package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Balance extends CommandTemplate {

    public Balance(Eclipse plugin) {
        super(plugin, "balance", List.of("bal", "money", "economy"), EclipsePermissionManager.EclipsePerm.FLY, false, 0, "Check player balance");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return execute(player, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return execute(sender, args);
    }

    private boolean execute(CommandSender sender, String[] args) {
        Player target;
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("Usage").replace("%s", "/balance [player]"));
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.getMessage("playerNotFound"));
                return true;
            }
            
            if (!target.equals(sender) && !sender.hasPermission("eclipse.balance.others")) {
                sender.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
        }

        double balance = plugin.getEconomyManager().getBalance(target.getUniqueId());
        
        if (target.equals(sender)) {
            sender.sendMessage(plugin.getMessage("balanceSelf")
                    .replace("%amount%", String.format("%.2f", balance)));
        } else {
            sender.sendMessage(plugin.getMessage("balanceOther")
                    .replace("%player%", target.getName())
                    .replace("%amount%", String.format("%.2f", balance)));
        }

        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1 && player.hasPermission("eclipse.balance.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
