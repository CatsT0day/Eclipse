package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Enchant extends CommandTemplate {

    public Enchant(Eclipse plugin) {
        super(plugin, "enchant", List.of(), EclipsePermissionManager.EclipsePerm.ENCHANT, true, 2, "Enchant item in your hand");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemNoItemInHand"));
            return true;
        }

        if (args.length < 2) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemUsageEnchant"));
            return true;
        }

        String inputName = args[0].toLowerCase();
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemLevelMustBeNumber"));
            return true;
        }

        NamespacedKey key = inputName.contains(":") ? NamespacedKey.fromString(inputName) : NamespacedKey.minecraft(inputName);
        Enchantment enchantment = key != null ? Registry.ENCHANTMENT.get(key) : null;

        if (enchantment == null) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemInvalidEnchantment").replace("%s", inputName));
            return true;
        }

        item.addUnsafeEnchantment(enchantment, level);
        if (!enchantment.getKey().getNamespace().equals(NamespacedKey.MINECRAFT)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
                if (lore == null) lore = new ArrayList<>();

                String enchantKeyString = enchantment.getKey().getKey();
                String romanLevel = toRoman(level);
                lore.removeIf(line -> PlainTextComponentSerializer.plainText().serialize(line).toLowerCase().contains(enchantKeyString.toLowerCase()));
                Component enchantLine = Component.text(enchantment.getKey().toString() + " " + romanLevel, NamedTextColor.GRAY);
                lore.addFirst(enchantLine);

                meta.lore(lore);
                item.setItemMeta(meta);
            }
        }

        plugin.sendCFGmessage(player, plugin.getMessage("itemEnchantSuccess")
                .replace("%s", enchantment.getKey().getKey())
                .replace("%d", String.valueOf(level)));
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return perform(player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            // Получаем список всех доступных в реестре чар (включая кастомные)
            return Registry.ENCHANTMENT.stream()
                    .map(e -> e.getKey().toString()) // Возвращает формат "namespace:key"
                    .filter(key -> key.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return List.of("1", "2", "3", "4", "5");
        }
        return Collections.emptyList();
    }
    private String toRoman(int number) {
        if (number <= 0) return String.valueOf(number);
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(1000, "M"); map.put(900, "CM"); map.put(500, "D"); map.put(400, "CD");
        map.put(100, "C"); map.put(90, "XC"); map.put(50, "L"); map.put(40, "XL");
        map.put(10, "X"); map.put(9, "IX"); map.put(5, "V"); map.put(4, "IV"); map.put(1, "I");
        int l = map.floorKey(number);
        if (number == l) return map.get(number);
        return map.get(l) + toRoman(number - l);
    }
}
