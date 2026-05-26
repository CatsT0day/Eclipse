package me.catst0day.Eclipse.Utils.Particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface ParticleAnim {
    void start(Plugin pl, Location loc, Player plr, String str);

    void stop();
}
