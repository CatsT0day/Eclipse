package me.catst0day.Eclipse.Holograms.Animations;


public class PulseAnimation implements AnimatableText {
    
    private final String text;
    private final int updateInterval;
    private final float minOpacity;
    private final float maxOpacity;
    private final float speed;
    private boolean active;
    
    
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
        
        
        float phase = (float) (tick * speed * 0.1);
        float sine = (float) Math.sin(phase);
        float normalizedSine = (sine + 1.0f) / 2.0f; 
        float opacity = minOpacity + (maxOpacity - minOpacity) * normalizedSine;
        
        
        
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
