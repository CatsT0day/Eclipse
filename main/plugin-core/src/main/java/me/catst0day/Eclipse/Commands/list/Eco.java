package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Eco extends CommandTemplate {

    public Eco(Eclipse plugin) {
        super(plugin, "eco", List.of("economyadmin"), EclipsePermissionManager.EclipsePerm.ECONOMY_ADMIN, false, 0, "Admin economy commands");
        tabCompleteArguments = List.of("give", "take", "set", "reset", "top");
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
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("ecoUsage"));
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "give" -> handleGive(sender, args);
            case "take" -> handleTake(sender, args);
            case "set" -> handleSet(sender, args);
            case "reset" -> handleReset(sender, args);
            case "top" -> handleTop(sender);
            default -> sender.sendMessage(plugin.getMessage("ecoUsage"));
        }

        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessage("ecoGiveUsage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessage("invalidAmount"));
            return;
        }

        if (plugin.getEconomyManager().addBalance(target.getUniqueId(), amount)) {
            String formatted = plugin.getEconomyManager().formatAmount(amount);
            sender.sendMessage(plugin.getMessage("ecoGiveSuccess")
                    .replace("%amount%", formatted)
                    .replace("%player%", target.getName()));
            target.sendMessage(plugin.getMessage("ecoGiveNotify").replace("%amount%", formatted));
        } else {
            sender.sendMessage(plugin.getMessage("ecoFailed"));
        }
    }

    private void handleTake(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessage("ecoTakeUsage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessage("invalidAmount"));
            return;
        }

        if (plugin.getEconomyManager().removeBalance(target.getUniqueId(), amount)) {
            String formatted = plugin.getEconomyManager().formatAmount(amount);
            sender.sendMessage(plugin.getMessage("ecoTakeSuccess")
                    .replace("%amount%", formatted)
                    .replace("%player%", target.getName()));
        } else {
            sender.sendMessage(plugin.getMessage("ecoTakeFailed"));
        }
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessage("ecoSetUsage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessage("invalidAmount"));
            return;
        }

        if (plugin.getEconomyManager().setBalance(target.getUniqueId(), amount)) {
            String formatted = plugin.getEconomyManager().formatAmount(amount);
            sender.sendMessage(plugin.getMessage("ecoSetSuccess")
                    .replace("%amount%", formatted)
                    .replace("%player%", target.getName()));
        } else {
            sender.sendMessage(plugin.getMessage("ecoFailed"));
        }
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessage("ecoResetUsage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return;
        }

        plugin.getEconomyManager().resetBalance(target.getUniqueId());
        sender.sendMessage(plugin.getMessage("ecoResetSuccess").replace("%player%", target.getName()));
    }

    private void handleTop(CommandSender sender) {
        sender.sendMessage(plugin.getMessage("ecoTopHeader"));
        
        sender.sendMessage(plugin.getMessage("ecoTopNotImplemented"));
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            return tabCompleteArguments.stream()
                    .filter(a -> a.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("reset"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        if (args.length == 3) {
            return List.of("10", "100", "1000", "10000", "100000");
        }
        return List.of();
    }
}
