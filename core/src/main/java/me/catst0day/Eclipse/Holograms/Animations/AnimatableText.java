package me.catst0day.Eclipse.Holograms.Animations;

/**
 * Interface for animatable hologram text.
 * Implementations of this interface provide dynamic text content
 * that changes over time based on animation type.
 */
public interface AnimatableText {
    
    /**
     * Gets the current text content for this animation at the given tick.
     * 
     * @param tick The current animation tick
     * @return The animated text string
     */
    String getText(long tick);
    
    /**
     * Gets the update interval in ticks for this animation.
     * 
     * @return The number of ticks between updates
     */
    int getUpdateInterval();
    
    /**
     * Checks if this animation is still active.
     * 
     * @return true if the animation should continue, false otherwise
     */
    boolean isActive();
    
    /**
     * Resets the animation to its initial state.
     */
    void reset();
}
