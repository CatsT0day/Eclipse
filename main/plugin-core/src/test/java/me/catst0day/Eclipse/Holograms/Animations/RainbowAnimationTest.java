package me.catst0day.Eclipse.Holograms.Animations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class RainbowAnimationTest {
    
    @Test
    public void testRainbowAnimationCreation() {
        RainbowAnimation animation = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 0.5f);
        
        assertNotNull(animation);
        assertEquals("Test", animation.getText(0).replaceAll("#[0-9A-Fa-f]{6}", ""));
        assertEquals(1, animation.getUpdateInterval());
        assertTrue(animation.isActive());
    }
    
    @Test
    public void testRainbowAnimationColorChange() {
        RainbowAnimation animation = new RainbowAnimation("Test", 1, 0.1f, 1.0f, 0.5f);
        
        String text1 = animation.getText(0);
        String text2 = animation.getText(10);
        String text3 = animation.getText(20);
        
        
        assertNotEquals(text1, text2);
        assertNotEquals(text2, text3);
    }
    
    @Test
    public void testRainbowAnimationSpeed() {
        RainbowAnimation slowAnimation = new RainbowAnimation("Test", 1, 0.01f, 1.0f, 0.5f);
        RainbowAnimation fastAnimation = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 0.5f);
        
        String slow1 = slowAnimation.getText(0);
        String slow2 = slowAnimation.getText(100);
        
        String fast1 = fastAnimation.getText(0);
        String fast2 = fastAnimation.getText(100);
        
        
        assertNotEquals(fast1, fast2);
        
        assertNotEquals(slow1, slow2);
    }
    
    @Test
    public void testRainbowAnimationSaturation() {
        RainbowAnimation saturated = new RainbowAnimation("Test", 1, 0.1f, 1.0f, 0.5f);
        RainbowAnimation desaturated = new RainbowAnimation("Test", 1, 0.1f, 0.0f, 0.5f);
        
        String saturatedText = saturated.getText(0);
        String desaturatedText = desaturated.getText(0);
        
        
        assertNotEquals(saturatedText, desaturatedText);
    }
    
    @Test
    public void testRainbowAnimationLightness() {
        RainbowAnimation light = new RainbowAnimation("Test", 1, 0.1f, 1.0f, 0.9f);
        RainbowAnimation dark = new RainbowAnimation("Test", 1, 0.1f, 1.0f, 0.1f);
        
        String lightText = light.getText(0);
        String darkText = dark.getText(0);
        
        
        assertNotEquals(lightText, darkText);
    }
    
    @Test
    public void testRainbowAnimationReset() {
        RainbowAnimation animation = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 0.5f);
        animation.reset();
        
        assertTrue(animation.isActive());
    }
    
    @Test
    public void testRainbowAnimationClampValues() {
        
        RainbowAnimation animation1 = new RainbowAnimation("Test", 1, 2.0f, 1.0f, 0.5f);
        RainbowAnimation animation2 = new RainbowAnimation("Test", 1, 0.5f, 2.0f, 0.5f);
        RainbowAnimation animation3 = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 2.0f);
        
        
        assertNotNull(animation1.getText(0));
        assertNotNull(animation2.getText(0));
        assertNotNull(animation3.getText(0));
    }
    
    @Test
    public void testRainbowAnimationHexFormat() {
        RainbowAnimation animation = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 0.5f);
        String text = animation.getText(0);
        
        
        assertTrue(text.matches("^#[0-9A-Fa-f]{6}.*"));
    }
    
    @Test
    public void testRainbowAnimationEmptyText() {
        RainbowAnimation animation = new RainbowAnimation("", 1, 0.5f, 1.0f, 0.5f);
        
        String text = animation.getText(0);
        
        assertTrue(text.matches("^#[0-9A-Fa-f]{6}$"));
    }
}
