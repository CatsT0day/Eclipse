package me.catst0day.Eclipse.Holograms.Animations;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class AnimationManager {
    
    private final Eclipse plugin;
    private final Map<String, Map<Integer, AnimatableText>> hologramAnimations;
    private final Map<String, Long> hologramLastUpdate;
    private BukkitTask animationTask;
    private long globalTick;
    private static final int VIEW_DISTANCE_SQUARED = 64 * 64; 
    
    
    public AnimationManager(Eclipse plugin) {
        this.plugin = plugin;
        this.hologramAnimations = new ConcurrentHashMap<>();
        this.hologramLastUpdate = new ConcurrentHashMap<>();
        this.globalTick = 0;
        startAnimationTask();
    }
    
    
    private void startAnimationTask() {
        animationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            globalTick++;
            updateAnimations();
        }, 1L, 1L);
    }
    
    
    private void updateAnimations() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Map.Entry<String, Map<Integer, AnimatableText>> entry : hologramAnimations.entrySet()) {
                String holoName = entry.getKey();
                Map<Integer, AnimatableText> animations = entry.getValue();
                
                EclipseHologram hologram = plugin.getHologramManager().getHologram(holoName);
                if (hologram == null || !hologram.isEnabled()) {
                    continue;
                }
                
                
                if (!isPlayerInRange(player, hologram)) {
                    continue;
                }
                
                
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
    
    
    private boolean isPlayerInRange(Player player, EclipseHologram hologram) {
        if (!player.getWorld().equals(hologram.getLocation().getWorld())) {
            return false;
        }
        
        double distanceSquared = player.getLocation().distanceSquared(hologram.getLocation());
        return distanceSquared <= VIEW_DISTANCE_SQUARED;
    }
    
    
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
    
    
    public void registerAnimation(String hologramName, int lineIndex, AnimatableText animation) {
        hologramAnimations.computeIfAbsent(hologramName.toLowerCase(), k -> new ConcurrentHashMap<>())
            .put(lineIndex, animation);
    }
    
    
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
    
    
    public void removeHologramAnimations(String hologramName) {
        hologramAnimations.remove(hologramName.toLowerCase());
        hologramLastUpdate.remove(hologramName.toLowerCase());
    }
    
    
    public Map<Integer, AnimatableText> getHologramAnimations(String hologramName) {
        return hologramAnimations.getOrDefault(hologramName.toLowerCase(), Collections.emptyMap());
    }
    
    
    public boolean hasAnimation(String hologramName, int lineIndex) {
        Map<Integer, AnimatableText> animations = hologramAnimations.get(hologramName.toLowerCase());
        return animations != null && animations.containsKey(lineIndex);
    }
    
    
    public void shutdown() {
        if (animationTask != null) {
            animationTask.cancel();
        }
        hologramAnimations.clear();
        hologramLastUpdate.clear();
    }
}
