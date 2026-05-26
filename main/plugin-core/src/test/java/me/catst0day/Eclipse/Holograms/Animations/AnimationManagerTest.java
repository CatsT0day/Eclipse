package me.catst0day.Eclipse.Holograms.Animations;

import me.catst0day.Eclipse.Eclipse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnimationManager.
 * Tests animation registration, removal, and management.
 */
@ExtendWith(MockitoExtension.class)
public class AnimationManagerTest {
    
    @Mock
    private Eclipse plugin;
    
    private AnimationManager animationManager;
    
    @BeforeEach
    public void setUp() {
        animationManager = new AnimationManager(plugin);
    }
    
    @Test
    public void testRegisterAnimation() {
        AnimatableText animation = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 0.5f);
        
        animationManager.registerAnimation("testHolo", 0, animation);
        
        assertTrue(animationManager.hasAnimation("testHolo", 0));
        assertFalse(animationManager.hasAnimation("testHolo", 1));
    }
    
    @Test
    public void testRemoveAnimation() {
        AnimatableText animation = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 0.5f);
        
        animationManager.registerAnimation("testHolo", 0, animation);
        assertTrue(animationManager.hasAnimation("testHolo", 0));
        
        animationManager.removeAnimation("testHolo", 0);
        assertFalse(animationManager.hasAnimation("testHolo", 0));
    }
    
    @Test
    public void testRemoveHologramAnimations() {
        AnimatableText animation1 = new RainbowAnimation("Test1", 1, 0.5f, 1.0f, 0.5f);
        AnimatableText animation2 = new GradientAnimation("Test2", 1, "#FF0000", "#0000FF", 0.5f);
        
        animationManager.registerAnimation("testHolo", 0, animation1);
        animationManager.registerAnimation("testHolo", 1, animation2);
        
        assertTrue(animationManager.hasAnimation("testHolo", 0));
        assertTrue(animationManager.hasAnimation("testHolo", 1));
        
        animationManager.removeHologramAnimations("testHolo");
        
        assertFalse(animationManager.hasAnimation("testHolo", 0));
        assertFalse(animationManager.hasAnimation("testHolo", 1));
    }
    
    @Test
    public void testGetHologramAnimations() {
        AnimatableText animation1 = new RainbowAnimation("Test1", 1, 0.5f, 1.0f, 0.5f);
        AnimatableText animation2 = new GradientAnimation("Test2", 1, "#FF0000", "#0000FF", 0.5f);
        
        animationManager.registerAnimation("testHolo", 0, animation1);
        animationManager.registerAnimation("testHolo", 1, animation2);
        
        var animations = animationManager.getHologramAnimations("testHolo");
        
        assertEquals(2, animations.size());
        assertTrue(animations.containsKey(0));
        assertTrue(animations.containsKey(1));
    }
    
    @Test
    public void testGetHologramAnimationsNonExistent() {
        var animations = animationManager.getHologramAnimations("nonExistent");
        
        assertTrue(animations.isEmpty());
    }
    
    @Test
    public void testHasAnimationNonExistent() {
        assertFalse(animationManager.hasAnimation("nonExistent", 0));
    }
    
    @Test
    public void testCaseInsensitiveHologramNames() {
        AnimatableText animation = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 0.5f);
        
        animationManager.registerAnimation("TestHolo", 0, animation);
        
        assertTrue(animationManager.hasAnimation("testholo", 0));
        assertTrue(animationManager.hasAnimation("TESTHOLO", 0));
        assertTrue(animationManager.hasAnimation("TestHolo", 0));
    }
    
    @Test
    public void testMultipleAnimationsSameLine() {
        AnimatableText animation1 = new RainbowAnimation("Test1", 1, 0.5f, 1.0f, 0.5f);
        AnimatableText animation2 = new GradientAnimation("Test2", 1, "#FF0000", "#0000FF", 0.5f);
        
        animationManager.registerAnimation("testHolo", 0, animation1);
        animationManager.registerAnimation("testHolo", 0, animation2);
        
        // Second animation should replace the first
        var animations = animationManager.getHologramAnimations("testHolo");
        assertEquals(1, animations.size());
        assertEquals(animation2, animations.get(0));
    }
    
    @Test
    public void testShutdown() {
        AnimatableText animation = new RainbowAnimation("Test", 1, 0.5f, 1.0f, 0.5f);
        
        animationManager.registerAnimation("testHolo", 0, animation);
        assertTrue(animationManager.hasAnimation("testHolo", 0));
        
        animationManager.shutdown();
        
        assertFalse(animationManager.hasAnimation("testHolo", 0));
    }
}
