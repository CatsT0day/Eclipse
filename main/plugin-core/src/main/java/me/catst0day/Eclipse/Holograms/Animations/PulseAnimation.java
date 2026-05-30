package me.catst0day.Eclipse.Holograms.Animations;


public class PulseAnimation implements AnimatableText {
    
    private final String text;
    private final int updateInterval;
    private final float minOpacity;
    private final float maxOpacity;
    private final float speed;
    private boolean active;
    
    /**
     * Creates a new pulse animation.
     * 
     * @param text The text to animate
     * @param updateInterval Ticks between opacity updates
     * @param minOpacity Minimum opacity (0.0 to 1.0)
     * @param maxOpacity Maximum opacity (0.0 to 1.0)
     * @param speed Speed of pulsing (0.0 to 1.0)
     */
    public PulseAnimation(String text, int updateInterval, float minOpacity, float maxOpacity, float speed) {
        this.text = text;
        this.updateInterval = Math.max(1, updateInterval);
        this.minOpacity = Math.max(0.0f, Math.min(1.0f, minOpacity));
        this.maxOpacity = Math.max(minOpacity, Math.min(1.0f, maxOpacity));
        this.speed = Math.max(0.01f, Math.min(1.0f, speed));
        this.active = true;
    }
    
    @Override
    public String getText(long tick) {
        if (!active) return text;
        
        // Use sine wave for smooth pulsing
        float phase = (float) (tick * speed * 0.1);
        float sine = (float) Math.sin(phase);
        float normalizedSine = (sine + 1.0f) / 2.0f; // Normalize to 0-1
        float opacity = minOpacity + (maxOpacity - minOpacity) * normalizedSine;
        
        // Apply opacity using Minecraft color codes (transparency not fully supported in legacy)
        // For modern versions with TextDisplay, this would use alpha
        int alpha = Math.round(opacity * 255);
        return String.format("<opacity:%d>%s", alpha, text);
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
