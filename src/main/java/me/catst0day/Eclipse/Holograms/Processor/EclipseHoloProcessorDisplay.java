package me.catst0day.Eclipse.Holograms.Processor;

import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHologramPacket;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class EclipseHoloProcessorDisplay extends EclipseHoloProcessor {
    public EclipseHoloProcessorDisplay(EclipseHologram hologram) {
        super(hologram);
    }

    @Override
    public CompletableFuture<Void> show(Player player) {
        return CompletableFuture.runAsync(() -> {
            EclipseHologramPacket.showHologram(player, hologram);
        });
    }

    @Override
    public CompletableFuture<Void> hide(Player player) {
        return CompletableFuture.runAsync(() -> {
            EclipseHologramPacket.hideHologram(player, hologram);
        });
    }

    @Override
    public CompletableFuture<Void> update(Player player) {
        return CompletableFuture.runAsync(() -> {
            EclipseHologramPacket.updateHologram(player, hologram);
        });
    }
}
