package me.catst0day.Eclipse.Holograms.Animations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnimationParser.
 * Tests parsing of different animation tag formats.
 */
public class AnimationParserTest {
    
    @Test
    public void testParseRainbowAnimation() {
        String text = "<rainbow:0.5:1.0:0.5>Hello World</rainbow>";
        AnimatableText animation = AnimationParser.parse(text);
        
        assertNotNull(animation);
        assertInstanceOf(RainbowAnimation.class, animation);
        assertEquals("Hello World", AnimationParser.stripTags(text));
    }
    
    @Test
    public void testParseGradientAnimation() {
        String text = "<gradient:#FF0000:#0000FF:0.5>Test Text</gradient>";
        AnimatableText animation = AnimationParser.parse(text);
        
        assertNotNull(animation);
        assertTrue(animation instanceof GradientAnimation);
        assertEquals("Test Text", AnimationParser.stripTags(text));
    }
    
    @Test
    public void testParseFrameAnimation() {
        String text = "<frame:20:||>Frame1||Frame2||Frame3</frame>";
        AnimatableText animation = AnimationParser.parse(text);
        
        assertNotNull(animation);
        assertTrue(animation instanceof FrameAnimation);
        assertEquals("Frame1||Frame2||Frame3", AnimationParser.stripTags(text));
    }
    
    @Test
    public void testParseInvalidAnimation() {
        String text = "Plain text without animation";
        AnimatableText animation = AnimationParser.parse(text);
        
        assertNull(animation);
    }
    
    @Test
    public void testContainsAnimation() {
        assertTrue(AnimationParser.containsAnimation("<rainbow:0.5:1.0:0.5>Text</rainbow>"));
        assertTrue(AnimationParser.containsAnimation("<gradient:#FF0000:#0000FF:0.5>Text</gradient>"));
        assertTrue(AnimationParser.containsAnimation("<frame:20:||>Frame1||Frame2</frame>"));
        assertFalse(AnimationParser.containsAnimation("Plain text"));
        assertFalse(AnimationParser.containsAnimation(null));
        assertFalse(AnimationParser.containsAnimation(""));
    }
    
    @Test
    public void testStripTags() {
        assertEquals("Hello", AnimationParser.stripTags("<rainbow:0.5:1.0:0.5>Hello</rainbow>"));
        assertEquals("World", AnimationParser.stripTags("<gradient:#FF0000:#0000FF:0.5>World</gradient>"));
        assertEquals("Test", AnimationParser.stripTags("<frame:20:||>Test</frame>"));
        assertEquals("Plain text", AnimationParser.stripTags("Plain text"));
        assertNull(AnimationParser.stripTags(null));
    }
    
    @Test
    public void testRainbowAnimationGetText() {
        String text = "<rainbow:0.1:1.0:0.5>Test</rainbow>";
        AnimatableText animation = AnimationParser.parse(text);
        
        assertNotNull(animation);
        String result1 = animation.getText(0);
        String result2 = animation.getText(10);
        
        // Results should be different due to color change
        assertNotEquals(result1, result2);
        // Both should contain the text
        assertTrue(result1.contains("Test"));
        assertTrue(result2.contains("Test"));
    }
    
    @Test
    public void testGradientAnimationGetText() {
        String text = "<gradient:#FF0000:#0000FF:0.1>Test</gradient>";
        AnimatableText animation = AnimationParser.parse(text);
        
        assertNotNull(animation);
        String result1 = animation.getText(0);
        String result2 = animation.getText(10);
        
        // Results should be different due to gradient shift
        assertNotEquals(result1, result2);
        // Both should contain the text
        assertTrue(result1.contains("Test"));
        assertTrue(result2.contains("Test"));
    }
    
    @Test
    public void testFrameAnimationGetText() {
        String text = "<frame:5:||>Frame1||Frame2||Frame3</frame>";
        AnimatableText animation = AnimationParser.parse(text);
        
        assertNotNull(animation);
        assertEquals("Frame1", animation.getText(0));
        assertEquals("Frame1", animation.getText(4));
        assertEquals("Frame2", animation.getText(5));
        assertEquals("Frame2", animation.getText(9));
        assertEquals("Frame3", animation.getText(10));
    }
    
    @Test
    public void testMalformedRainbowTag() {
        AnimatableText animation = AnimationParser.parse("<rainbow:invalid:1.0:0.5>Text</rainbow>");
        assertNull(animation);
    }
    
    @Test
    public void testMalformedGradientTag() {
        AnimatableText animation = AnimationParser.parse("<gradient:invalid:#0000FF:0.5>Text</gradient>");
        assertNull(animation);
    }
    
    @Test
    public void testMalformedFrameTag() {
        AnimatableText animation = AnimationParser.parse("<frame:invalid:||>Frame1||Frame2</frame>");
        assertNull(animation);
    }
}
