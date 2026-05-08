package me.catst0day.Eclipse.Holograms.Processor;

import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHoloPktHandler;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EclipseHoloProcessorArmorStand extends EclipseHoloProcessor {
    public EclipseHoloProcessorArmorStand(EclipseHologram hologram) {
        super(hologram);
    }

    @Override
    public CompletableFuture<Void> show(Player player) {
        return CompletableFuture.runAsync(() -> {
            EclipseHoloPktHandler.showHologram(player, hologram);
        });
    }

    @Override
    public CompletableFuture<Void> hide(Player player) {
        return CompletableFuture.runAsync(() -> {
            EclipseHoloPktHandler.hideHologram(player, hologram);
        });
    }

    @Override
    public CompletableFuture<Void> update(Player player) {
        return CompletableFuture.runAsync(() -> {
            EclipseHoloPktHandler.updateHologram(player, hologram);
        });
    }

    @Override
    protected void onPageChange(UUID uuid) {
        // Handle page-specific logic for ArmorStand
    }
}
