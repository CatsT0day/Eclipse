package me.catst0day.Eclipse.Holograms.Animations;


public class ScrollAnimation implements AnimatableText {
    
    private final String text;
    private final int updateInterval;
    private final int speed;
    private final int width;
    private boolean active;
    private final String padding;
    
    
    public ScrollAnimation(String text, int updateInterval, int speed, int width) {
        this.text = text;
        this.updateInterval = Math.max(1, updateInterval);
        this.speed = Math.max(1, speed);
        this.width = Math.max(text.length(), width);
        this.active = true;
        this.padding = " ".repeat(width);
    }
    
    @Override
    public String getText(long tick) {
        if (!active) return text;
        
        
        String padded = padding + text + padding;
        int totalLength = padded.length();
        
        
        long position = (tick * speed) % totalLength;
        
        
        int start = (int) position;
        int end = Math.min(start + width, totalLength);
        String visible = padded.substring(start, end);
        
        
        if (visible.length() < width) {
            visible += padded.substring(0, width - visible.length());
        }
        
        return visible;
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
