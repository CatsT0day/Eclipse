package me.catst0day.Eclipse.Holograms.Animations;


public class GradientAnimation implements AnimatableText {
    
    private final String text;
    private final int updateInterval;
    private final String startColor;
    private final String endColor;
    private final float speed;
    private boolean active;
    
    
    public GradientAnimation(String text, int updateInterval, String startColor, String endColor, float speed) {
        this.text = text;
        this.updateInterval = Math.max(1, updateInterval);
        this.startColor = startColor;
        this.endColor = endColor;
        this.speed = Math.max(0.0f, Math.min(1.0f, speed));
        this.active = true;
    }
    
    @Override
    public String getText(long tick) {
        if (!active) return text;
        
        float offset = (tick * speed) % 1.0f;
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            float position = (float) i / text.length();
            float gradientPos = (position + offset) % 1.0f;
            String color = interpolateColor(startColor, endColor, gradientPos);
            result.append(color).append(text.charAt(i));
        }
        
        return result.toString();
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
    
    
    private String interpolateColor(String color1, String color2, float t) {
        int rgb1 = hexToRgb(color1);
        int rgb2 = hexToRgb(color2);
        
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;
        
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;
        
        int r = Math.round(r1 + (r2 - r1) * t);
        int g = Math.round(g1 + (g2 - g1) * t);
        int b = Math.round(b1 + (b2 - b1) * t);
        
        return String.format("#%02X%02X%02X", r, g, b);
    }
    
    
    private int hexToRgb(String hex) {
        hex = hex.replace("#", "");
        if (hex.length() == 3) {
            hex = "" + hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2);
        }
        return Integer.parseInt(hex, 16);
    }
}
