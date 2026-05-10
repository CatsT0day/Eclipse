package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class EclipseOnPlayerJoinEvent implements Listener {
    public EclipseOnPlayerJoinEvent() {
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        EclipsePlr player = Eclipse.getI().getPlayer(event.getPlayer().getUniqueId());

        String title = Eclipse.getI().getConfig().getString("join.title");
        String subtitle = Eclipse.getI().getConfig().getString("join.subtitle");
        int fadeIn = Eclipse.getI().getConfig().getInt("join.fadeIn");
        int stay = Eclipse.getI().getConfig().getInt("join.stay");
        int fadeOut = Eclipse.getI().getConfig().getInt("join.fadeOut");
        player.sendTitleAsynchronously(Eclipse.getI(), title, subtitle, fadeIn, stay, fadeOut);
    }
}
