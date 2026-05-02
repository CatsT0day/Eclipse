package me.catst0day.Eclipse.EventListeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class EclipseOnEntityDamageByEntityEvent implements Listener {
    public EclipseOnEntityDamageByEntityEvent() {
    }

    @EventHandler
    public void onPlayerHit(@NotNull EntityDamageByEntityEvent event) {
        Entity var4 = event.getDamager();
        if (var4 instanceof Player damager) {
            var4 = event.getEntity();
            if (var4 instanceof Player damaged) {
                if (damager.isFlying() && !damager.hasPermission("eclipse.damage.fly.bypass")) {
                    damager.setAllowFlight(false);
                }

                if (damaged.isFlying() && !damaged.hasPermission("eclipse.damage.fly.bypass")) {
                    damaged.setAllowFlight(false);
                }

                event.setDamage(event.getDamage() * 0.85);
            }
        }

    }
}
