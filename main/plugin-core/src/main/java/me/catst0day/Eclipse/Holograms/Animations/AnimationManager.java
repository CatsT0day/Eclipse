package me.catst0day.Eclipse.Holograms.Animations;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages hologram animations with efficient update scheduling.
 * Only updates holograms when players are within viewing distance to optimize performance.
 */
public class AnimationManager {
    
    private final Eclipse plugin;
    private final Map<String, Map<Integer, AnimatableText>> hologramAnimations;
    private final Map<String, Long> hologramLastUpdate;
    private BukkitTask animationTask;
    private long globalTick;
    private static final int VIEW_DISTANCE_SQUARED = 64 * 64; // 64 blocks view distance
    
    /**
     * Creates a new AnimationManager.
     * 
     * @param plugin The plugin instance (can be replaced with eclipse.getI() btw)
     */
    public AnimationManager(Eclipse plugin) {
        this.plugin = plugin;
        this.hologramAnimations = new ConcurrentHashMap<>();
        this.hologramLastUpdate = new ConcurrentHashMap<>();
        this.globalTick = 0;
        startAnimationTask();
    }
    
    /**
     * Starts the animation update task.
     * Runs every tick to update animations efficiently.
     */
    private void startAnimationTask() {
        animationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            globalTick++;
            updateAnimations();
        }, 1L, 1L);
    }
    
    /**
     * Updates all active animations for holograms with nearby players.
     * Optimized to only update when players are in range.
     */
    private void updateAnimations() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Map.Entry<String, Map<Integer, AnimatableText>> entry : hologramAnimations.entrySet()) {
                String holoName = entry.getKey();
                Map<Integer, AnimatableText> animations = entry.getValue();
                
                EclipseHologram hologram = plugin.getHologramManager().getHologram(holoName);
                if (hologram == null || !hologram.isEnabled()) {
                    continue;
                }
                
                // Check if player is within viewing distance
                if (!isPlayerInRange(player, hologram)) {
                    continue;
                }
                
                // Update animations based on their intervals
                Long lastUpdate = hologramLastUpdate.get(holoName);
                if (lastUpdate == null) {
                    lastUpdate = 0L;
                }
                
                boolean needsUpdate = false;
                for (Map.Entry<Integer, AnimatableText> animEntry : animations.entrySet()) {
                    AnimatableText anim = animEntry.getValue();
                    if (!anim.isActive()) continue;
                    
                    long ticksSinceUpdate = globalTick - lastUpdate;
                    if (ticksSinceUpdate >= anim.getUpdateInterval()) {
                        needsUpdate = true;
                        break;
                    }
                }
                
                if (needsUpdate) {
                    updateHologramText(hologram, animations);
                    hologramLastUpdate.put(holoName, globalTick);
                }
            }
        }
    }
    
    /**
     * Checks if a player is within viewing distance of a hologram.
     * Uses distance squared for performance optimization.
     * 
     * @param player The player to check
     * @param hologram The hologram to check
     * @return true if player is in range, false otherwise
     */
    private boolean isPlayerInRange(Player player, EclipseHologram hologram) {
        if (!player.getWorld().equals(hologram.getLocation().getWorld())) {
            return false;
        }
        
        double distanceSquared = player.getLocation().distanceSquared(hologram.getLocation());
        return distanceSquared <= VIEW_DISTANCE_SQUARED;
    }
    
    /**
     * Updates the hologram text with animated content.
     * 
     * @param hologram The hologram to update
     * @param animations The animations to apply
     */
    private void updateHologramText(EclipseHologram hologram, Map<Integer, AnimatableText> animations) {
        List<String> lines = new ArrayList<>(hologram.getLines());
        
        for (Map.Entry<Integer, AnimatableText> entry : animations.entrySet()) {
            int lineIndex = entry.getKey();
            AnimatableText animation = entry.getValue();
            
            if (lineIndex >= 0 && lineIndex < lines.size()) {
                String animatedText = animation.getText(globalTick);
                lines.set(lineIndex, animatedText);
            }
        }
        
        hologram.setLines(lines);
        plugin.getHologramManager().updateHologram(hologram);
    }
    
    /**
     * Registers an animation for a specific hologram line.
     * 
     * @param hologramName The name of the hologram
     * @param lineIndex The line index to animate
     * @param animation The animation to apply
     */
    public void registerAnimation(String hologramName, int lineIndex, AnimatableText animation) {
        hologramAnimations.computeIfAbsent(hologramName.toLowerCase(), k -> new ConcurrentHashMap<>())
            .put(lineIndex, animation);
    }
    
    /**
     * Removes an animation from a hologram line.
     * 
     * @param hologramName The name of the hologram
     * @param lineIndex The line index to remove animation from
     */
    public void removeAnimation(String hologramName, int lineIndex) {
        Map<Integer, AnimatableText> animations = hologramAnimations.get(hologramName.toLowerCase());
        if (animations != null) {
            animations.remove(lineIndex);
            if (animations.isEmpty()) {
                hologramAnimations.remove(hologramName.toLowerCase());
                hologramLastUpdate.remove(hologramName.toLowerCase());
            }
        }
    }
    
    /**
     * Removes all animations for a hologram.
     * 
     * @param hologramName The name of the hologram
     */
    public void removeHologramAnimations(String hologramName) {
        hologramAnimations.remove(hologramName.toLowerCase());
        hologramLastUpdate.remove(hologramName.toLowerCase());
    }
    
    /**
     * Gets all animations for a hologram.
     * 
     * @param hologramName The name of the hologram
     * @return Map of line indices to animations
     */
    public Map<Integer, AnimatableText> getHologramAnimations(String hologramName) {
        return hologramAnimations.getOrDefault(hologramName.toLowerCase(), Collections.emptyMap());
    }
    
    /**
     * Checks if a hologram line has an animation.
     * 
     * @param hologramName The name of the hologram
     * @param lineIndex The line index to check
     * @return true if the line has an animation, false otherwise
     */
    public boolean hasAnimation(String hologramName, int lineIndex) {
        Map<Integer, AnimatableText> animations = hologramAnimations.get(hologramName.toLowerCase());
        return animations != null && animations.containsKey(lineIndex);
    }
    
    /**
     * Shuts down the animation manager and cancels the update task.
     */
    public void shutdown() {
        if (animationTask != null) {
            animationTask.cancel();
        }
        hologramAnimations.clear();
        hologramLastUpdate.clear();
    }
}
