package me.catst0day.Eclipse.Holograms;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Entity.Player.Gui;
import me.catst0day.Eclipse.Entity.Player.GuiButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EclipseHoloCFGGui {
    private static final Map<UUID, LineEditData> editingPlayers = new HashMap<>();
    
    private final Eclipse plugin;
    private final EclipseHologram hologram;
    private final Player player;
    private Gui gui;
    
    public static class LineEditData {
        public final EclipseHologram hologram;
        public final int lineIndex;
        public final boolean isNewLine;
        
        LineEditData(EclipseHologram hologram, int lineIndex, boolean isNewLine) {
            this.hologram = hologram;
            this.lineIndex = lineIndex;
            this.isNewLine = isNewLine;
        }
    }
    
    public static LineEditData getEditingData(UUID uuid) {
        return editingPlayers.get(uuid);
    }
    
    public static void removeEditingData(UUID uuid) {
        editingPlayers.remove(uuid);
    }
    
    public EclipseHoloCFGGui(Eclipse plugin, EclipseHologram hologram, Player player) {
        this.plugin = plugin;
        this.hologram = hologram;
        this.player = player;
    }
    
    public void open() {
        gui = new Gui(player, plugin.getMessage("hologramConfigTitle").replace("%name%", hologram.getName()), 4);
        setupButtons();
        gui.open();
    }
    
    private void setupButtons() {
        GuiButton infoButton = new GuiButton(Material.BOOK)
                .setName(plugin.getMessage("hologramInfoButton"))
                .addLore(plugin.getMessage("hologramName").replace("%name%", hologram.getName()))
                .addLore(plugin.getMessage("hologramWorld").replace("%world%", hologram.getLocation().getWorld().getName()))
                .addLore(plugin.getMessage("hologramLocation").replace("%x%", String.format("%.2f", hologram.getLocation().getX()))
                        .replace("%y%", String.format("%.2f", hologram.getLocation().getY()))
                        .replace("%z%", String.format("%.2f", hologram.getLocation().getZ())))
                .addLore(plugin.getMessage("hologramLinesCount").replace("%count%", String.valueOf(hologram.getLines().size())))
                .addLore(plugin.getMessage("hologramViewDistance").replace("%distance%", String.valueOf(hologram.getViewDistance())));
        gui.addButton(4, infoButton);
        GuiButton editLinesButton = new GuiButton(Material.WRITABLE_BOOK)
                .setName(plugin.getMessage("hologramEditLinesButton"))
                .addLore(plugin.getMessage("hologramEditLinesLore"))
                .onLeftClick(p -> openLinesEditor());
        gui.addButton(10, editLinesButton);
        GuiButton viewDistanceButton = new GuiButton(Material.COMPASS)
                .setName(plugin.getMessage("hologramViewDistanceButton"))
                .addLore(plugin.getMessage("hologramCurrentDistance").replace("%distance%", String.valueOf(hologram.getViewDistance())))
                .addLore(plugin.getMessage("hologramClickToChange"))
                .onLeftClick(p -> cycleViewDistance());
        gui.addButton(11, viewDistanceButton);
        GuiButton alwaysVisibleButton = new GuiButton(Material.ENDER_EYE)
                .setName(plugin.getMessage("hologramAlwaysVisibleButton"))
                .addLore(plugin.getMessage("hologramAlwaysVisibleLore").replace("%status%", hologram.isAlwaysVisible() ? 
                        plugin.getMessage("enabled") : plugin.getMessage("disabled")))
                .onLeftClick(p -> toggleAlwaysVisible());
        gui.addButton(12, alwaysVisibleButton);
        GuiButton updateIntervalButton = new GuiButton(Material.CLOCK)
                .setName(plugin.getMessage("hologramUpdateIntervalButton"))
                .addLore(plugin.getMessage("hologramCurrentInterval").replace("%interval%", String.valueOf(hologram.getUpdateInterval())))
                .addLore(plugin.getMessage("hologramClickToChange"))
                .onLeftClick(p -> cycleUpdateInterval());
        gui.addButton(13, updateIntervalButton);
        GuiButton teleportButton = new GuiButton(Material.ENDER_PEARL)
                .setName(plugin.getMessage("hologramTeleportButton"))
                .addLore(plugin.getMessage("hologramTeleportLore"))
                .onLeftClick(p -> {
                    p.teleport(hologram.getLocation());
                    p.sendMessage(plugin.getMessage("hologramTeleported"));
                });
        gui.addButton(14, teleportButton);
        GuiButton moveButton = new GuiButton(Material.BEACON)
                .setName(plugin.getMessage("hologramMoveButton"))
                .addLore(plugin.getMessage("hologramMoveLore"))
                .onLeftClick(p -> {
                    hologram.setLocation(p.getLocation());
                    plugin.getHologramManager().updateHologram(hologram);
                    p.sendMessage(plugin.getMessage("hologramMoved"));
                    gui.update();
                });
        gui.addButton(15, moveButton);
        GuiButton deleteButton = new GuiButton(Material.BARRIER)
                .setName(plugin.getMessage("hologramDeleteButton"))
                .addLore(plugin.getMessage("hologramDeleteLore"))
                .onLeftClick(p -> {
                    plugin.getHologramManager().deleteHologram(hologram.getName());
                    p.sendMessage(plugin.getMessage("hologramDeleted"));
                    p.closeInventory();
                });
        gui.addButton(30, deleteButton);
        GuiButton closeButton = new GuiButton(Material.ARROW)
                .setName(plugin.getMessage("closeButton"))
                .closeOnClick();
        gui.addButton(31, closeButton);
    }
    
    public void openLinesEditor() {
        Gui linesGui = new Gui(player, plugin.getMessage("hologramLinesEditorTitle"), 6);
        
        List<String> lines = hologram.getLines();
        for (int i = 0; i < lines.size() && i < 45; i++) {
            final int lineIndex = i;
            GuiButton lineButton = new GuiButton(Material.PAPER)
                    .setName(plugin.getMessage("hologramLineButton").replace("%number%", String.valueOf(i + 1)))
                    .addLore(lines.get(i))
                    .addLore(plugin.getMessage("hologramLineEditLore"))
                    .onLeftClick(p -> {
                        p.closeInventory();
                        editingPlayers.put(p.getUniqueId(), new LineEditData(hologram, lineIndex, false));
                        p.sendMessage(plugin.getMessage("hologramEnterNewLine").replace("%line%", String.valueOf(lineIndex + 1)));
                    })
                    .onRightClick(p -> {
                        hologram.removeLine(lineIndex);
                        plugin.getHologramManager().updateHologram(hologram);
                        p.sendMessage(plugin.getMessage("hologramLineRemoved"));
                        openLinesEditor();
                    });
            linesGui.addButton(i, lineButton);
        }
        GuiButton addButton = new GuiButton(Material.WRITABLE_BOOK)
                .setName(plugin.getMessage("hologramAddLineButton"))
                .addLore(plugin.getMessage("hologramAddLineLore"))
                .onLeftClick(p -> {
                    p.closeInventory();
                    editingPlayers.put(p.getUniqueId(), new LineEditData(hologram, lines.size(), true));
                    p.sendMessage(plugin.getMessage("hologramEnterNewLine"));
                });
        linesGui.addButton(45, addButton);
        GuiButton backButton = new GuiButton(Material.ARROW)
                .setName(plugin.getMessage("backButton"))
                .onLeftClick(p -> open());
        linesGui.addButton(53, backButton);
        
        linesGui.open();
    }
    
    private void cycleViewDistance() {
        int current = hologram.getViewDistance();
        int newDistance;
        if (current < 16) newDistance = 16;
        else if (current < 32) newDistance = 32;
        else if (current < 48) newDistance = 48;
        else if (current < 64) newDistance = 64;
        else if (current < 96) newDistance = 96;
        else newDistance = 128;
        
        hologram.setViewDistance(newDistance);
        plugin.getHologramManager().updateHologram(hologram);
        player.sendMessage(plugin.getMessage("hologramViewDistanceSet").replace("%distance%", String.valueOf(newDistance)));
        gui.update();
    }
    
    private void toggleAlwaysVisible() {
        hologram.setAlwaysVisible(!hologram.isAlwaysVisible());
        plugin.getHologramManager().updateHologram(hologram);
        player.sendMessage(plugin.getMessage("hologramAlwaysVisibleToggled").replace("%status%", 
                hologram.isAlwaysVisible() ? plugin.getMessage("enabled") : plugin.getMessage("disabled")));
        gui.update();
    }
    
    private void cycleUpdateInterval() {
        int current = hologram.getUpdateInterval();
        int newInterval;
        if (current < 20) newInterval = 20;
        else if (current < 40) newInterval = 40;
        else if (current < 60) newInterval = 60;
        else if (current < 120) newInterval = 120;
        else newInterval = 300;
        
        hologram.setUpdateInterval(newInterval);
        plugin.getHologramManager().updateHologram(hologram);
        player.sendMessage(plugin.getMessage("hologramUpdateIntervalSet").replace("%interval%", String.valueOf(newInterval)));
        gui.update();
    }
}
