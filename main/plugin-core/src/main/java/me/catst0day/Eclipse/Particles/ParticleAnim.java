package me.catst0day.Eclipse.Particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @deprecated Instead of using this interface, use
 * {@link me.catst0day.Eclipse.Utils.Particles.ParticleAnim}
 * This will no longer update, and will be removed in 1.04.00
 */
@Deprecated(since = "1.02.95", forRemoval = true)
public interface ParticleAnim {
    /**
     * @deprecated Instead of using this interface, use
     * {@link me.catst0day.Eclipse.Utils.Particles.ParticleAnim}
     * This will no longer update, and will be removed in 1.04.00
     */
    @Deprecated(since = "1.02.95", forRemoval = true)
    void start(Plugin pl, Location loc, Player plr, String str);
    /**
     * @deprecated Instead of using this interface, use
     * {@link me.catst0day.Eclipse.Utils.Particles.ParticleAnim}
     * This will no longer update, and will be removed in 1.04.00
     */
    @Deprecated(since = "1.02.95", forRemoval = true)
    void stop();
}
