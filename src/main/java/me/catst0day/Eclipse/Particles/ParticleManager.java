package me.catst0day.Eclipse.Particles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ParticleManager {
    private final Plugin plugin;
    private final Map<String, ParticleAnim> activeAnimations = new HashMap();

    public ParticleManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void startAnimation(String id, ParticleAnim animation, Location center, Player owner, String structureName) {
        if (this.activeAnimations.containsKey(id)) {
            ((ParticleAnim)this.activeAnimations.get(id)).stop();
        }

        animation.start(this.plugin, center, owner, structureName);
        this.activeAnimations.put(id, animation);
    }

    public void stopAnimation(String id) {
        if (this.activeAnimations.containsKey(id)) {
            ((ParticleAnim)this.activeAnimations.get(id)).stop();
            this.activeAnimations.remove(id);
        }

    }

    public void stopAllAnimations() {
        if (!this.activeAnimations.isEmpty()) {
            Set<String> keys = new HashSet(this.activeAnimations.keySet());

            for(String id : keys) {
                this.stopAnimation(id);
            }

            this.plugin.getLogger().info("All particle animations have stopped. (" + keys.size() + ").");
        }
    }
}
