package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Entity.Player.Gui;
import me.catst0day.Eclipse.Entity.Player.GuiButton;
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Managers.EclipseHologramManager;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Hologram extends CommandTemplate {

    public Hologram(Eclipse plugin) {
        super(plugin, "holo", List.of("hologram", "hd"), CAPIPermissions.HOLOGRAMS, false, 0, "Hologram management");
    }

    @Override
    protected boolean perform(CommandSender sender, @Nullable Player player, String[] args) {
        EclipsePlr ePlayer = (player != null) ? plugin.getPlayer(player) : null;

        if (args.length == 0) {
            if (ePlayer != null) showHologramList(ePlayer);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("list") && ePlayer != null) {
            showHologramList(ePlayer);
            return true;
        }

        if (sub.equals("reload")) {
            plugin.getHologramManager().saveAll();
            sender.sendMessage(plugin.getMessage("configReloaded"));
            return true;
        }

        return false;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        EclipsePlr ePlayer = plugin.getPlayer(player);
        EclipseHologramManager manager = plugin.getHologramManager();
        String sub = args[0].toLowerCase();

        // СОЗДАНИЕ: Генерируем UUID здесь
        if (sub.equals("create")) {
            if (args.length < 2) return false;
            String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).replace("&", "§");

            UUID newId = UUID.randomUUID(); // Генерация UUID при создании
            manager.create(newId, player.getLocation(), new ArrayList<>(List.of(text)));

            ePlayer.sendMsg(plugin.getMessage("hologramCreated"));
            return true;
        }

        // РЕДАКТИРОВАНИЕ: Используем старый UUID
        if (sub.equals("addline")) {
            if (args.length < 3) return false;
            UUID id = findUUID(args[1]);
            EclipseHologram holo = manager.getHolograms().get(id);
            if (holo != null) {
                String newLine = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).replace("&", "§");
                List<String> lines = new ArrayList<>(holo.getRawLines());
                lines.add(newLine);

                manager.removeHologram(id);
                manager.create(id, holo.getBaseLocation(), lines); // Тот же UUID

                ePlayer.sendMsg(plugin.getMessage("hologramLineAdded"));
            }
            return true;
        }

        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (String s : Arrays.asList("create", "list", "addline", "remove", "reload")) {
                if (s.startsWith(args[0].toLowerCase())) completions.add(s);
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("addline"))) {
            for (UUID uuid : plugin.getHologramManager().getHolograms().keySet()) {
                String sid = uuid.toString().substring(0, 8);
                if (sid.startsWith(args[1].toLowerCase())) completions.add(sid);
            }
        }
        return completions;
    }

    private void showHologramList(EclipsePlr ePlayer) {
        Gui gui = new Gui(ePlayer.getPlayer(), "§7Holograms", 6);
        fillBorders(gui);
        int slot = 10;
        for (Map.Entry<UUID, EclipseHologram> entry : plugin.getHologramManager().getHolograms().entrySet()) {
            if (slot > 43) break;
            UUID id = entry.getKey();
            EclipseHologram holo = entry.getValue();

            gui.addButton(slot++, new GuiButton(Material.ARMOR_STAND)
                    .setName("§eID: §f" + id.toString().substring(0, 8))
                    .addLore("§7Lines: " + holo.getRawLines().size())
                    .onLeftClick(p -> ePlayer.teleportAsynchronously(holo.getBaseLocation()))
                    .onRightClick(p -> {
                        if (p.isSneaking()) {
                            plugin.getHologramManager().removeHologram(id);
                            showHologramList(ePlayer);
                        } else openEditor(ePlayer, id, holo);
                    }));
            if (slot % 9 == 8) slot += 2;
        }
        gui.open();
    }

    private void openEditor(EclipsePlr ePlayer, UUID id, EclipseHologram holo) {
        Gui gui = new Gui(ePlayer.getPlayer(), "§8Edit " + id.toString().substring(0, 8), 3);
        gui.addButton(13, new GuiButton(Material.ENDER_PEARL)
                .setName("§eMove to Me")
                .onLeftClick(p -> {
                    List<String> lines = new ArrayList<>(holo.getRawLines());
                    plugin.getHologramManager().removeHologram(id);
                    plugin.getHologramManager().create(id, p.getLocation(), lines); // Используем тот же UUID
                    p.closeInventory();
                    ePlayer.sendMsg(plugin.getMessage("hologramMoved"));
                }));
        gui.open();
    }

    private void fillBorders(Gui gui) {
        GuiButton glass = new GuiButton(Material.GRAY_STAINED_GLASS_PANE).setName(" ");
        for (int i = 0; i < 9; i++) {
            gui.addButton(i, glass);
            gui.addButton(45 + i, glass);
        }
        for (int row = 1; row < 5; row++) {
            gui.addButton(row * 9, glass);
            gui.addButton(row * 9 + 8, glass);
        }
    }

    private UUID findUUID(String input) {
        for (UUID uuid : plugin.getHologramManager().getHolograms().keySet()) {
            if (uuid.toString().startsWith(input.toLowerCase())) return uuid;
        }
        return null;
    }
}