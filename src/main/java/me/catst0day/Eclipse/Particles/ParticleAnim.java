package me.catst0day.Eclipse.Particles;

import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface ParticleAnim {
    void start(Plugin pl, Location loc, Player plr, String str);

    void stop();
}
