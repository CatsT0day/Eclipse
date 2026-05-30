package me.catst0day.Eclipse.EventListeners;


import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import static me.catst0day.Eclipse.Utils.Util.log;

public class EclipseHideAchievements implements Listener {

    private void hideAdvancementsFor(World world) {
        if (world == null) return;
        GameRule<Boolean> showAdvancementsRule = GameRule.ANNOUNCE_ADVANCEMENTS;

        world.setGameRule(showAdvancementsRule, false);
        log("Achievements are hidden for world '" + world.getName() + "'.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        hideAdvancementsFor(event.getWorld());
    }
}