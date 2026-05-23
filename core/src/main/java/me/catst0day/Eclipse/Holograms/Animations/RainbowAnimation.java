package me.catst0day.Eclipse.Holograms.Animations;

/**
 * Rainbow animation that cycles through colors using HSL color space.
 * The text color changes smoothly through the rainbow spectrum.
 */
public class RainbowAnimation implements AnimatableText {
    
    private final String text;
    private final int updateInterval;
    private final float speed;
    private final float saturation;
    private final float lightness;
    private boolean active;
    
    /**
     * Creates a new rainbow animation.
     * 
     * @param text The text to animate
     * @param updateInterval Ticks between color updates
     * @param speed Speed of color cycling (0.0 to 1.0)
     * @param saturation Color saturation (0.0 to 1.0)
     * @param lightness Color lightness (0.0 to 1.0)
     */
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
    
    /**
     * Converts HSL color to hex string.
     * 
     * @param h Hue (0.0 to 1.0)
     * @param s Saturation (0.0 to 1.0)
     * @param l Lightness (0.0 to 1.0)
     * @return Hex color string (e.g., "#FF0000")
     */
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
