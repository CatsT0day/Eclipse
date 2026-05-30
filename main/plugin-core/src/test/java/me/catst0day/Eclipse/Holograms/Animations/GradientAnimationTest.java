package me.catst0day.Eclipse.Holograms.Animations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class GradientAnimationTest {
    
    @Test
    public void testGradientAnimationCreation() {
        GradientAnimation animation = new GradientAnimation("Test", 1, "#FF0000", "#0000FF", 0.5f);
        
        assertNotNull(animation);
        assertTrue(animation.getText(0).contains("Test"));
        assertEquals(1, animation.getUpdateInterval());
        assertTrue(animation.isActive());
    }
    
    @Test
    public void testGradientAnimationInterpolation() {
        GradientAnimation animation = new GradientAnimation("Test", 1, "#FF0000", "#0000FF", 0.5f);
        
        String text = animation.getText(0);
        
        
        assertTrue(text.matches("(#[0-9A-Fa-f]{6}T)(#[0-9A-Fa-f]{6}e)(#[0-9A-Fa-f]{6}s)(#[0-9A-Fa-f]{6}t)"));
    }
    
    @Test
    public void testGradientAnimationShift() {
        GradientAnimation animation = new GradientAnimation("Test", 1, "#FF0000", "#0000FF", 0.1f);
        
        String text1 = animation.getText(0);
        String text2 = animation.getText(10);
        
        
        assertNotEquals(text1, text2);
    }
    
    @Test
    public void testGradientAnimationSpeed() {
        GradientAnimation slow = new GradientAnimation("Test", 1, "#FF0000", "#0000FF", 0.01f);
        GradientAnimation fast = new GradientAnimation("Test", 1, "#FF0000", "#0000FF", 0.5f);
        
        String slow1 = slow.getText(0);
        String slow2 = slow.getText(100);
        
        String fast1 = fast.getText(0);
        String fast2 = fast.getText(100);
        
        
        assertNotEquals(fast1, fast2);
    }
    
    @Test
    public void testGradientAnimationColorRange() {
        GradientAnimation animation = new GradientAnimation("Test", 1, "#FF0000", "#0000FF", 0.0f);
        
        String text = animation.getText(0);
        
        
        assertTrue(text.contains("#FF0000") || text.contains("#FF"));
        assertTrue(text.contains("#0000FF") || text.contains("#00"));
    }
    
    @Test
    public void testGradientAnimationHexColorParsing() {
        GradientAnimation animation1 = new GradientAnimation("Test", 1, "#F00", "#00F", 0.5f);
        GradientAnimation animation2 = new GradientAnimation("Test", 1, "#FF0000", "#0000FF", 0.5f);
        
        
        assertNotNull(animation1.getText(0));
        assertNotNull(animation2.getText(0));
    }
    
    @Test
    public void testGradientAnimationReset() {
        GradientAnimation animation = new GradientAnimation("Test", 1, "#FF0000", "#0000FF", 0.5f);
        animation.reset();
        
        assertTrue(animation.isActive());
    }
    
    @Test
    public void testGradientAnimationSameColors() {
        GradientAnimation animation = new GradientAnimation("Test", 1, "#FF0000", "#FF0000", 0.5f);
        
        String text = animation.getText(0);
        
        
        assertNotNull(text);
        assertTrue(text.contains("Test"));
    }
    
    @Test
    public void testGradientAnimationEmptyText() {
        GradientAnimation animation = new GradientAnimation("", 1, "#FF0000", "#0000FF", 0.5f);
        
        String text = animation.getText(0);
        
        
        assertTrue(text.isEmpty() || text.matches("^#[0-9A-Fa-f]{6}$"));
    }
    
    @Test
    public void testGradientAnimationLongText() {
        String longText = "This is a very long text to test gradient animation";
        GradientAnimation animation = new GradientAnimation(longText, 1, "#FF0000", "#0000FF", 0.5f);
        
        String result = animation.getText(0);
        
        
        assertTrue(result.contains(longText));
    }
}
