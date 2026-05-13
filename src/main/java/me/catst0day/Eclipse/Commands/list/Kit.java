package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Managers.EclipseKitManager;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Kit extends CommandTemplate {

    public Kit(Eclipse plugin) {
        super(plugin, "kit", Arrays.asList("kits"), EclipsePermissionManager.EclipsePerm.KIT, true, 0, "Kit system");
        tabCompleteArguments = Arrays.asList("claim", "list", "create", "delete", "edit", "give", "reset");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length == 0) {
            listKits(player);
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "claim" -> handleClaim(player, args);
            case "list" -> listKits(player);
            case "create" -> handleCreate(player, args);
            case "delete" -> handleDelete(player, args);
            case "edit" -> handleEdit(player, args);
            case "give" -> handleGive(player, args);
            case "reset" -> handleReset(player, args);
            default -> {
                // Try claim by name
                EclipseKitManager.Kit kit = plugin.getKitManager().getKit(action);
                if (kit != null) {
                    claimKit(player, kit);
                } else {
                    player.sendMessage(plugin.getMessage("kitNotFound"));
                }
            }
        }
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    private void listKits(Player player) {
        EclipsePlr ePlr = plugin.getPlayer(player.getUniqueId());
        List<EclipseKitManager.Kit> kits = plugin.getKitManager().getAvailableKitsForPlayer(ePlr);

        if (kits.isEmpty()) {
            player.sendMessage(plugin.getMessage("noKitsAvailable"));
            return;
        }

        player.sendMessage(plugin.getMessage("kitListHeader"));
        for (EclipseKitManager.Kit kit : kits) {
            player.sendMessage(plugin.getMessage("kitListItem")
                    .replace("%name%", kit.getDisplayName())
                    .replace("%items%", String.valueOf(kit.getItems().size())));
        }
    }

    private void handleClaim(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("kitClaimUsage"));
            return;
        }

        EclipseKitManager.Kit kit = plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            player.sendMessage(plugin.getMessage("kitNotFound"));
            return;
        }

        claimKit(player, kit);
    }

    private void claimKit(Player player, EclipseKitManager.Kit kit) {
        EclipsePlr ePlr = plugin.getPlayer(player.getUniqueId());

        if (!plugin.getKitManager().canClaimKit(ePlr, kit)) {
            long remaining = plugin.getKitManager().getCooldownRemaining(ePlr, kit);
            if (remaining > 0) {
                player.sendMessage(plugin.getMessage("kitCooldown")
                        .replace("%time%", plugin.getKitManager().formatCooldown(remaining)));
            } else {
                player.sendMessage(plugin.getMessage("kitCannotClaim"));
            }
            return;
        }

        if (plugin.getKitManager().claimKit(ePlr, kit)) {
            player.sendMessage(plugin.getMessage("kitClaimed")
                    .replace("%kit%", kit.getDisplayName()));
        } else {
            player.sendMessage(plugin.getMessage("kitClaimFailed"));
        }
    }

    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("eclipse.kit.admin")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("kitCreateUsage"));
            return;
        }

        String name = args[1];
        if (plugin.getKitManager().getKit(name) != null) {
            player.sendMessage(plugin.getMessage("kitAlreadyExists"));
            return;
        }

        EclipseKitManager.Kit kit = new EclipseKitManager.Kit(name);
        kit.setDisplayName(name);

        // Add items from inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                kit.addItem(item.clone());
            }
        }

        plugin.getKitManager().saveKit(kit);
        player.sendMessage(plugin.getMessage("kitCreated").replace("%kit%", name));
    }

    private void handleDelete(Player player, String[] args) {
        if (!player.hasPermission("eclipse.kit.admin")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("kitDeleteUsage"));
            return;
        }

        String name = args[1];
        if (plugin.getKitManager().deleteKit(name)) {
            player.sendMessage(plugin.getMessage("kitDeleted").replace("%kit%", name));
        } else {
            player.sendMessage(plugin.getMessage("kitNotFound"));
        }
    }

    private void handleEdit(Player player, String[] args) {
        if (!player.hasPermission("eclipse.kit.admin")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return;
        }

        if (args.length < 4) {
            player.sendMessage(plugin.getMessage("kitEditUsage"));
            return;
        }

        String name = args[1];
        String field = args[2].toLowerCase();
        String value = args[3];

        EclipseKitManager.Kit kit = plugin.getKitManager().getKit(name);
        if (kit == null) {
            player.sendMessage(plugin.getMessage("kitNotFound"));
            return;
        }

        switch (field) {
            case "name" -> kit.setDisplayName(value);
            case "cooldown" -> kit.setCooldownSeconds(Long.parseLong(value));
            case "permission" -> kit.setPermission(value);
            case "onetime" -> kit.setOneTime(Boolean.parseBoolean(value));
            case "firstjoin" -> kit.setFirstJoin(Boolean.parseBoolean(value));
            case "order" -> kit.setOrder(Integer.parseInt(value));
            default -> {
                player.sendMessage(plugin.getMessage("kitInvalidField"));
                return;
            }
        }

        plugin.getKitManager().saveKit(kit);
        player.sendMessage(plugin.getMessage("kitUpdated").replace("%kit%", name));
    }

    private void handleGive(Player player, String[] args) {
        if (!player.hasPermission("eclipse.kit.admin")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("kitGiveUsage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return;
        }

        EclipseKitManager.Kit kit = plugin.getKitManager().getKit(args[2]);
        if (kit == null) {
            player.sendMessage(plugin.getMessage("kitNotFound"));
            return;
        }

        EclipsePlr eTarget = plugin.getPlayer(target.getUniqueId());
        if (plugin.getKitManager().claimKit(eTarget, kit)) {
            player.sendMessage(plugin.getMessage("kitGiven")
                    .replace("%kit%", kit.getDisplayName())
                    .replace("%player%", target.getName()));
            target.sendMessage(plugin.getMessage("kitReceived")
                    .replace("%kit%", kit.getDisplayName()));
        } else {
            player.sendMessage(plugin.getMessage("kitGiveFailed"));
        }
    }

    private void handleReset(Player player, String[] args) {
        if (!player.hasPermission("eclipse.kit.admin")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("kitResetUsage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return;
        }

        String kitName = args[2];
        EclipsePlr eTarget = plugin.getPlayer(target.getUniqueId());
        plugin.getKitManager().resetCooldown(eTarget, kitName);
        player.sendMessage(plugin.getMessage("kitReset")
                .replace("%kit%", kitName)
                .replace("%player%", target.getName()));
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            return tabCompleteArguments.stream()
                    .filter(a -> a.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        String action = args[0].toLowerCase();

        if (args.length == 2) {
            return switch (action) {
                case "claim", "edit", "delete" -> plugin.getKitManager().getAllKits().stream()
                        .map(EclipseKitManager.Kit::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
                case "give", "reset" -> Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
                default -> List.of();
            };
        }

        if (args.length == 3 && (action.equals("give") || action.equals("reset"))) {
            return plugin.getKitManager().getAllKits().stream()
                    .map(EclipseKitManager.Kit::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        if (args.length == 3 && action.equals("edit")) {
            return Arrays.asList("name", "cooldown", "permission", "onetime", "firstjoin", "order")
                    .stream()
                    .filter(f -> f.startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}
