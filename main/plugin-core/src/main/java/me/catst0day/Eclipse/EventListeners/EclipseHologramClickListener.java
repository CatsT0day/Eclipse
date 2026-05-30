package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHologramPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EclipseHologramClickListener implements Listener {
    private final Eclipse plugin;
    private final Map<UUID, Long> clickCooldowns = new HashMap<>();
    private static final long CLICK_COOLDOWN_MS = 500;

    public EclipseHologramClickListener(Eclipse plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        
        long lastClick = clickCooldowns.getOrDefault(player.getUniqueId(), 0L);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClick < CLICK_COOLDOWN_MS) {
            return;
        }
        
        
        UUID hologramUuid = EclipseHologramPacket.getHologramFromEntity(player, event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null);
        if (hologramUuid == null) {
            return;
        }
        
        EclipseHologram hologram = plugin.getHologramManager().getHologramByUUID(hologramUuid);
        if (hologram == null) {
            return;
        }
        
        if (!hologram.isClickable()) {
            return;
        }
        
        event.setCancelled(true);
        clickCooldowns.put(player.getUniqueId(), currentTime);
        
        handleHologramClick(player, hologram);
    }

    private void handleHologramClick(Player player, EclipseHologram hologram) {
        String command = hologram.getClickCommand();
        double cost = hologram.getClickCost();
        
        if (command == null || command.isEmpty()) {
            return;
        }
        
        
        if (cost > 0) {
            if (!plugin.getEconomyManager().hasBalance(player.getUniqueId(), cost)) {
                player.sendMessage(plugin.getMessage("hologramClickCannotAfford")
                        .replace("%cost%", plugin.getEconomyManager().formatAmount(cost))
                        .replace("%balance%", plugin.getEconomyManager().formatAmount(plugin.getEconomyManager().getBalance(player.getUniqueId()))));
                return;
            }
            
            
            if (!plugin.getEconomyManager().removeBalance(player.getUniqueId(), cost)) {
                player.sendMessage(plugin.getMessage("hologramClickPaymentFailed"));
                return;
            }
            
            player.sendMessage(plugin.getMessage("hologramClickPaid")
                    .replace("%cost%", plugin.getEconomyManager().formatAmount(cost))
                    .replace("%command%", command));
        }
        
        
        executeCommand(player, command);
    }

    private void executeCommand(Player player, String command) {
        
        String parsedCommand = command
                .replace("{player}", player.getName())
                .replace("{uuid}", player.getUniqueId().toString());
        
        
        if (parsedCommand.startsWith("[player] ")) {
            parsedCommand = parsedCommand.substring("[player] ".length());
            plugin.getServer().dispatchCommand(player, parsedCommand);
        } else if (parsedCommand.startsWith("[console] ")) {
            parsedCommand = parsedCommand.substring("[console] ".length());
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), parsedCommand);
        } else {
            
            plugin.getServer().dispatchCommand(player, parsedCommand);
        }
    }
}
