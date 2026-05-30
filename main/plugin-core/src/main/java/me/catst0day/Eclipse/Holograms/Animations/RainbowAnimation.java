package me.catst0day.Eclipse.Holograms.Animations;


public class RainbowAnimation implements AnimatableText {
    
    private final String text;
    private final int updateInterval;
    private final float speed;
    private final float saturation;
    private final float lightness;
    private boolean active;
    
    
    public RainbowAnimation(String text, int updateInterval, float speed, float saturation, float lightness) {
        this.text = text;
        this.updateInterval = Math.max(1, updateInterval);
        this.speed = Math.max(0.0f, Math.min(1.0f, speed));
        this.saturation = Math.max(0.0f, Math.min(1.0f, saturation));
        this.lightness = Math.max(0.0f, Math.min(1.0f, lightness));
        this.active = true;
    }
    
    @Override
    public String getText(long tick) {
        if (!active) return text;
        
        float hue = (tick * speed) % 1.0f;
        String color = hslToHex(hue, saturation, lightness);
        return color + text;
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
    
    
    private String hslToHex(float h, float s, float l) {
        float r, g, b;
        
        if (s == 0.0f) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1.0f + s) : l + s - l * s;
            float p = 2.0f * l - q;
            r = hueToRgb(p, q, h + 1.0f / 3.0f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f / 3.0f);
        }
        
        return String.format("#%02X%02X%02X",
            Math.round(r * 255),
            Math.round(g * 255),
            Math.round(b * 255));
    }
    
    private float hueToRgb(float p, float q, float t) {
        if (t < 0.0f) t += 1.0f;
        if (t > 1.0f) t -= 1.0f;
        if (t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
        if (t < 0.5f) return q;
        if (t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
        return p;
    }
}
