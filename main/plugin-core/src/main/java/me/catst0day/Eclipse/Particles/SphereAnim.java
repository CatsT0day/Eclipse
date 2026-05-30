package me.catst0day.Eclipse.Particles;

import me.catst0day.Eclipse.Utils.Schedulers.EclipseScheduler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;



@Deprecated(since = "1.02.95", forRemoval = true)
public class SphereAnim implements ParticleAnim {
    private BukkitTask task;
    private final double radius;
    private double angle = 0.0;

    public SphereAnim(double radius) {
        this.radius = radius;
    }

    @Override
    
    @Deprecated(since = "1.02.95", forRemoval = true)
    public void start(Plugin plugin, final Location center, Player owner, String structureName) {
        this.task = EclipseScheduler.scheduleSyncRepeatingTask(plugin, () -> {
            if (center.getWorld() == null) {
                this.stop();
                return;
            }

            Particle.DustOptions purple = new Particle.DustOptions(Color.fromRGB(160, 0, 220), 1.5F);
            int points = 60;

            for (int i = 0; i < points; ++i) {
                double theta = Math.acos(2.0 * i / points - 1.0);
                double phi = this.angle + Math.sqrt(points * Math.PI) * i;

                double x = this.radius * Math.sin(theta) * Math.cos(phi);
                double y = this.radius * Math.cos(theta);
                double z = this.radius * Math.sin(theta) * Math.sin(phi);

                Location particleLoc = center.clone().add(x, y, z);
                center.getWorld().spawnParticle(
                        Particle.DUST,
                        particleLoc,
                        1,
                        0.0, 0.0, 0.0,
                        0.0,
                        purple
                );
            }

            this.angle += 0.06544984694978735;

        }, 0L, 3L);
    }

    @Override
    
    @Deprecated(since = "1.02.95", forRemoval = true)
    public void stop() {

        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}