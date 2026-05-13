package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Pay extends CommandTemplate {

    public Pay(Eclipse plugin) {
        super(plugin, "pay", List.of(), EclipsePermissionManager.EclipsePerm.ECONOMY, true, 2, "Send money to another player");
    }

    @Override
    protected boolean perform(CommandSender sender, @Nullable Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("payUsage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(plugin.getMessage("paySelfError"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalidAmount"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(plugin.getMessage("amountMustBePositive"));
            return true;
        }

        if (!plugin.getEconomyManager().hasBalance(player.getUniqueId(), amount)) {
            player.sendMessage(plugin.getMessage("notEnoughMoney"));
            return true;
        }

        if (plugin.getEconomyManager().transferBalance(player.getUniqueId(), target.getUniqueId(), amount)) {
            String formattedAmount = plugin.getEconomyManager().formatAmount(amount);
            player.sendMessage(plugin.getMessage("paySuccess")
                    .replace("%amount%", formattedAmount)
                    .replace("%player%", target.getName()));
            target.sendMessage(plugin.getMessage("payReceived")
                    .replace("%amount%", formattedAmount)
                    .replace("%player%", player.getName()));
        } else {
            player.sendMessage(plugin.getMessage("payFailed"));
        }

        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> !name.equals(player.getName()))
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2) {
            return List.of("10", "100", "1000", "10000");
        }
        return List.of();
    }
}
