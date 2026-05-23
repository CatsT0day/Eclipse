package me.catst0day.Eclipse.EventListeners;

import io.papermc.paper.registry.keys.GameRuleKeys;
import org.bukkit.GameRule;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.Contract;

import static me.catst0day.Eclipse.Utils.Util.log;

public class EclipseHideAchievements implements Listener {

    @SuppressWarnings("unchecked")
    private void hideAdvancementsFor(World world) {
        if (world == null) return;
        GameRule<Boolean> showAdvancementsRule = (GameRule<Boolean>) Registry.GAME_RULE.get(GameRuleKeys.SHOW_ADVANCEMENT_MESSAGES);

        if (showAdvancementsRule != null) {
            world.setGameRule(showAdvancementsRule, false);
            log("Achievements are hidden for world '" + world.getName() + "'.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        hideAdvancementsFor(event.getWorld());
    }
}
