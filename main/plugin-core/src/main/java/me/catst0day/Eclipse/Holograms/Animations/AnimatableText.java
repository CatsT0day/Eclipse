package me.catst0day.Eclipse.Holograms.Animations;


public interface AnimatableText {
    
    
    String getText(long tick);
    
    
    int getUpdateInterval();
    
    
    boolean isActive();
    
    
    void reset();
}
