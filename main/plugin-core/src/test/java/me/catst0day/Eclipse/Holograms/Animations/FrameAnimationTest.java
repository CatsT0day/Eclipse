package me.catst0day.Eclipse.Holograms.Animations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for FrameAnimation.
 * Tests frame cycling and interval handling.
 */
public class FrameAnimationTest {
    
    @Test
    public void testFrameAnimationCreationWithDelimiter() {
        FrameAnimation animation = new FrameAnimation("Frame1||Frame2||Frame3", 5, "||");
        
        assertNotNull(animation);
        assertEquals(5, animation.getUpdateInterval());
        assertEquals(3, animation.getFrameCount());
        assertTrue(animation.isActive());
    }
    
    @Test
    public void testFrameAnimationCreationWithList() {
        List<String> frames = List.of("Frame1", "Frame2", "Frame3");
        FrameAnimation animation = new FrameAnimation(frames, 5, "||");
        
        assertNotNull(animation);
        assertEquals(5, animation.getUpdateInterval());
        assertEquals(3, animation.getFrameCount());
    }
    
    @Test
    public void testFrameAnimationCycling() {
        FrameAnimation animation = new FrameAnimation("Frame1||Frame2||Frame3", 5, "||");
        
        assertEquals("Frame1", animation.getText(0));
        assertEquals("Frame1", animation.getText(4));
        assertEquals("Frame2", animation.getText(5));
        assertEquals("Frame2", animation.getText(9));
        assertEquals("Frame3", animation.getText(10));
        assertEquals("Frame3", animation.getText(14));
        assertEquals("Frame1", animation.getText(15)); // Should cycle back
    }
    
    @Test
    public void testFrameAnimationInterval() {
        FrameAnimation animation = new FrameAnimation("Frame1||Frame2", 10, "||");
        
        assertEquals("Frame1", animation.getText(0));
        assertEquals("Frame1", animation.getText(9));
        assertEquals("Frame2", animation.getText(10));
        assertEquals("Frame2", animation.getText(19));
        assertEquals("Frame1", animation.getText(20));
    }
    
    @Test
    public void testFrameAnimationSingleFrame() {
        FrameAnimation animation = new FrameAnimation("SingleFrame", 5, "||");
        
        assertEquals("SingleFrame", animation.getText(0));
        assertEquals("SingleFrame", animation.getText(100));
        assertEquals("SingleFrame", animation.getText(1000));
    }
    
    @Test
    public void testFrameAnimationCustomDelimiter() {
        FrameAnimation animation = new FrameAnimation("Frame1///Frame2///Frame3", 5, "///");
        
        assertEquals("Frame1", animation.getText(0));
        assertEquals("Frame2", animation.getText(5));
        assertEquals("Frame3", animation.getText(10));
    }
    
    @Test
    public void testFrameAnimationEmptyFrames() {
        FrameAnimation animation = new FrameAnimation("||||", 5, "||");
        
        assertEquals("", animation.getText(0));
    }
    
    @Test
    public void testFrameAnimationReset() {
        FrameAnimation animation = new FrameAnimation("Frame1||Frame2", 5, "||");
        animation.reset();
        
        assertTrue(animation.isActive());
        assertEquals("Frame1", animation.getText(0));
    }
    
    @Test
    public void testFrameAnimationInactive() {
        FrameAnimation animation = new FrameAnimation("Frame1||Frame2", 5, "||");
        
        // Manually deactivate by checking if it cycles correctly after reset
        animation.reset();
        assertTrue(animation.isActive());
    }
    
    @Test
    public void testFrameAnimationLongInterval() {
        FrameAnimation animation = new FrameAnimation("Frame1||Frame2", 100, "||");
        
        assertEquals("Frame1", animation.getText(0));
        assertEquals("Frame1", animation.getText(99));
        assertEquals("Frame2", animation.getText(100));
    }
    
    @Test
    public void testFrameAnimationSpecialCharacters() {
        FrameAnimation animation = new FrameAnimation("Frame!@#$%^&*()||Frame2<>?", 5, "||");
        
        assertEquals("Frame!@#$%^&*()", animation.getText(0));
        assertEquals("Frame2<>?", animation.getText(5));
    }
    
    @Test
    public void testFrameAnimationUnicode() {
        FrameAnimation animation = new FrameAnimation("Привет||Hello||مرحبا", 5, "||");
        
        assertEquals("Привет", animation.getText(0));
        assertEquals("Hello", animation.getText(5));
        assertEquals("مرحبا", animation.getText(10));
    }
    
    @Test
    public void testFrameAnimationEmptyString() {
        FrameAnimation animation = new FrameAnimation("", 5, "||");
        
        assertEquals("", animation.getText(0));
    }
    
    @Test
    public void testFrameAnimationNullDelimiter() {
        FrameAnimation animation = new FrameAnimation("Frame1||Frame2", 5, null);
        
        // Should use default delimiter "||"
        assertEquals("Frame1", animation.getText(0));
        assertEquals("Frame2", animation.getText(5));
    }
}
