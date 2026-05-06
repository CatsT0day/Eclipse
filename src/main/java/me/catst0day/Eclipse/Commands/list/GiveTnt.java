package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveTnt extends CommandTemplate {

    public GiveTnt(Eclipse plugin) {
        super(plugin,
                "givetnt",
                List.of("gtnt"),
                CAPIPermissions.TNTGIVE,
                false,
                0,
                "Give custom TNTGIVE to a player"
        );
    }

    @Override
    protected boolean perform(CommandSender sender, @Nullable Player player, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /eclipse givetnt <player> <type> [amount]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        String tntType = args[1];
        ConfigurationSection tntSection = plugin.getConfig().getConfigurationSection("tnts." + tntType);

        if (tntSection == null) {
            sender.sendMessage("§cTNT type '" + tntType + "' not found in configuration.");
            return true;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {}
        }

        ItemStack tntItem = createCustomTnt(tntType, tntSection, amount);
        target.getInventory().addItem(tntItem);

        sender.sendMessage("§aGiven " + amount + "x " + tntType + " to " + target.getName());
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(@Nullable Player player, String[] args) {
        if (args.length == 1) {
            return null;
        }

        if (args.length == 2) {
            ConfigurationSection tnts = plugin.getConfig().getConfigurationSection("tnts");
            if (tnts != null) {
                return tnts.getKeys(false).stream()
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3) {
            return Arrays.asList("1", "16", "32", "64");
        }

        return Collections.emptyList();
    }

    private ItemStack createCustomTnt(String type, ConfigurationSection section, int amount) {
        ItemStack item = new ItemStack(Material.TNT, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String rawName = section.getString("display-hologramm", "Custom TNTGIVE");
            meta.setDisplayName(TextUtil.translateHexAndAlternateColorCodes(rawName));

            List<String> lore = new ArrayList<>();
            lore.add("§7Type: §f" + type);
            if (section.contains("explosionPower")) {
                lore.add("§7Power: §c" + section.getDouble("explosionPower"));
            }
            meta.setLore(lore);

            item.setItemMeta(meta);
        }
        return item;
    }
}