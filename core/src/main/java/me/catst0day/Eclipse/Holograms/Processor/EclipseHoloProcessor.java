package me.catst0day.Eclipse.Holograms.Processor;

import me.catst0day.Eclipse.Holograms.EclipseHologram;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class EclipseHoloProcessor {
    protected final EclipseHologram hologram;
    
    public EclipseHoloProcessor(EclipseHologram hologram) {
        this.hologram = hologram;
    }
    
    public abstract CompletableFuture<Void> show(Player player);
    public abstract CompletableFuture<Void> hide(Player player);
    public abstract CompletableFuture<Void> update(Player player);
    
    protected void onPageChange(UUID uuid) {
    }
}
