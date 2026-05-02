package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class EclipseOnPlayerRespawnEvent implements Listener {
    public EclipseOnPlayerRespawnEvent() {
    }

    @EventHandler
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        EclipsePlr player = (EclipsePlr) event.getPlayer();
        String title = Eclipse.getI().getConfig().getString("respawn.title");
        String subtitle =Eclipse.getI().getConfig().getString("respawn.subtitle");
        int fadeIn = Eclipse.getI().getConfig().getInt("respawn.fadeIn");
        int stay = Eclipse.getI().getConfig().getInt("respawn.stay");
        int fadeOut = Eclipse.getI().getConfig().getInt("respawn.fadeOut");
        player.sendTitleAsynchronously(Eclipse.getI(), title, subtitle, fadeIn, stay, fadeOut);
    }
}

