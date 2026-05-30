package me.catst0day.Eclipse.Holograms.Animations;

/**
 * Typewriter animation that reveals text character by character.
 * Creates a typing effect as if the text is being typed in real-time.
 */
public class TypewriterAnimation implements AnimatableText {
    
    private final String text;
    private final int updateInterval;
    private final int speed;
    private final boolean loop;
    private boolean active;
    private final String cursor;
    
    /**
     * Creates a new typewriter animation.
     * 
     * @param text The text to type out
     * @param updateInterval Ticks between character reveals
     * @param speed Characters to reveal per update
     * @param loop Whether to loop the animation
     */
    public TypewriterAnimation(String text, int updateInterval, int speed, boolean loop) {
        this.text = text;
        this.updateInterval = Math.max(1, updateInterval);
        this.speed = Math.max(1, speed);
        this.loop = loop;
        this.active = true;
        this.cursor = "_";
    }
    
    /**
     * Creates a new typewriter animation with custom cursor.
     * 
     * @param text The text to type out
     * @param updateInterval Ticks between character reveals
     * @param speed Characters to reveal per update
     * @param loop Whether to loop the animation
     * @param cursor The cursor character to show at the end
     */
    public TypewriterAnimation(String text, int updateInterval, int speed, boolean loop, String cursor) {
        this.text = text;
        this.updateInterval = Math.max(1, updateInterval);
        this.speed = Math.max(1, speed);
        this.loop = loop;
        this.active = true;
        this.cursor = cursor != null ? cursor : "_";
    }
    
    @Override
    public String getText(long tick) {
        if (!active) return text;
        
        int totalChars = text.length();
        long totalRevealed = (tick / updateInterval) * speed;
        
        if (totalRevealed >= totalChars) {
            if (loop) {
                // Reset animation
                long cycleLength = (totalChars / speed + 1) * updateInterval;
                long effectiveTick = tick % cycleLength;
                totalRevealed = (effectiveTick / updateInterval) * speed;
            } else {
                return text; // Animation complete
            }
        }
        
        int revealCount = (int) Math.min(totalRevealed, totalChars);
        String revealed = text.substring(0, revealCount);
        
        // Add cursor if not complete
        if (revealCount < totalChars) {
            revealed += cursor;
        }
        
        return revealed;
    }
    
    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void reset() {
        this.active = true;
    }
}
