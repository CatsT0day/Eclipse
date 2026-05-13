package me.catst0day.Eclipse.Particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Deprecated(since = "1.02.95", forRemoval = true)
public interface ParticleAnim {
    @Deprecated(since = "1.02.95", forRemoval = true)
    void start(Plugin pl, Location loc, Player plr, String str);
    @Deprecated(since = "1.02.95", forRemoval = true)
    void stop();
}
