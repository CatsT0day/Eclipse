package me.catst0day.Eclipse.Holograms.Animations;


public class BounceAnimation implements AnimatableText {
    
    private final String text;
    private final int updateInterval;
    private final float amplitude;
    private final float speed;
    private boolean active;
    
    
    public BounceAnimation(String text, int updateInterval, float amplitude, float speed) {
        this.text = text;
        this.updateInterval = Math.max(1, updateInterval);
        this.amplitude = Math.max(0.1f, amplitude);
        this.speed = Math.max(0.01f, Math.min(1.0f, speed));
        this.active = true;
    }
    
    @Override
    public String getText(long tick) {
        if (!active) return text;
        
        
        float phase = (float) (tick * speed * 0.1);
        float offset = (float) Math.sin(phase) * amplitude;
        
        
        return String.format("<translate:0.0:%.2f>%s", offset, text);
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
