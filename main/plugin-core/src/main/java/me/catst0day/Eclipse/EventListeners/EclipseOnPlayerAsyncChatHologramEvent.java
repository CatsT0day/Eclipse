package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Holograms.EclipseHoloCFGGui;
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;

public class EclipseOnPlayerAsyncChatHologramEvent implements Listener {

    private final Eclipse plugin;

    public EclipseOnPlayerAsyncChatHologramEvent(Eclipse plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        EclipseHoloCFGGui.LineEditData editData = EclipseHoloCFGGui.getEditingData(player.getUniqueId());

        if (editData == null) {
            return;
        }
        event.setCancelled(true);
        String rawLine = PlainTextComponentSerializer.plainText().serialize(event.message());
        final String newLine = rawLine.replace("_", " ");

        EclipseHologram hologram = editData.hologram;
        EclipseHoloCFGGui.removeEditingData(player.getUniqueId());
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline()) return;

            if (editData.isNewLine) {
                hologram.addLine(newLine);
                player.sendMessage(plugin.getMessage("hologramLineAdded"));
            } else {
                hologram.setLine(editData.lineIndex, newLine);
                player.sendMessage(plugin.getMessage("hologramLineSet"));
            }
            plugin.getHologramManager().updateHologram(hologram);
            new EclipseHoloCFGGui(plugin, hologram, player).openLinesEditor();
        });
    }
}
