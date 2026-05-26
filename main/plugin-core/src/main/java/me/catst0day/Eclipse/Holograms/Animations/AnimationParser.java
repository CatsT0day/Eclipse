package me.catst0day.Eclipse.Holograms.Animations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for hologram animation tags.
 * Supports rainbow, gradient, and frame-by-frame animations.
 * 
 * Tag formats:
 * - <rainbow:speed:saturation:lightness>text</rainbow>
 * - <gradient:startColor:endColor:speed>text</gradient>
 * - <frame:interval:delimiter>frame1||frame2||frame3</frame>
 */
public class AnimationParser {
    
    private static final Pattern RAINBOW_PATTERN = Pattern.compile("<rainbow:([^:]+):([^:]+):([^:]+)>(.*?)</rainbow>");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:([^:]+):([^:]+):([^:]+)>(.*?)</gradient>");
    private static final Pattern FRAME_PATTERN = Pattern.compile("<frame:([^:]+):([^:]+)>(.*?)</frame>");
    
    /**
     * Parses a text string and creates an appropriate AnimatableText if an animation tag is found.
     * 
     * @param text The text to parse
     * @return AnimatableText if tag found, null otherwise
     */
    public static AnimatableText parse(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        // Try rainbow animation
        AnimatableText rainbow = parseRainbow(text);
        if (rainbow != null) return rainbow;
        
        // Try gradient animation
        AnimatableText gradient = parseGradient(text);
        if (gradient != null) return gradient;
        
        // Try frame animation
        AnimatableText frame = parseFrame(text);
        if (frame != null) return frame;
        
        return null;
    }
    
    /**
     * Parses a rainbow animation tag.
     * Format: <rainbow:speed:saturation:lightness>text</rainbow>
     * 
     * @param text The text to parse
     * @return RainbowAnimation if tag found, null otherwise
     */
    private static AnimatableText parseRainbow(String text) {
        Matcher matcher = RAINBOW_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        
        try {
            float speed = Float.parseFloat(matcher.group(1));
            float saturation = Float.parseFloat(matcher.group(2));
            float lightness = Float.parseFloat(matcher.group(3));
            String content = matcher.group(4);
            
            return new RainbowAnimation(content, 1, speed, saturation, lightness);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Parses a gradient animation tag.
     * Format: <gradient:startColor:endColor:speed>text</gradient>
     * 
     * @param text The text to parse
     * @return GradientAnimation if tag found, null otherwise
     */
    private static AnimatableText parseGradient(String text) {
        Matcher matcher = GRADIENT_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        
        try {
            String startColor = matcher.group(1);
            String endColor = matcher.group(2);
            float speed = Float.parseFloat(matcher.group(3));
            String content = matcher.group(4);
            
            return new GradientAnimation(content, 1, startColor, endColor, speed);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Parses a frame animation tag.
     * Format: <frame:interval:delimiter>frame1||frame2||frame3</frame>
     * 
     * @param text The text to parse
     * @return FrameAnimation if tag found, null otherwise
     */
    private static AnimatableText parseFrame(String text) {
        Matcher matcher = FRAME_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        
        try {
            int interval = Integer.parseInt(matcher.group(1));
            String delimiter = matcher.group(2);
            String content = matcher.group(3);
            
            return new FrameAnimation(content, interval, delimiter);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Checks if a string contains an animation tag.
     * 
     * @param text The text to check
     * @return true if animation tag found, false otherwise
     */
    public static boolean containsAnimation(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        return RAINBOW_PATTERN.matcher(text).find() ||
               GRADIENT_PATTERN.matcher(text).find() ||
               FRAME_PATTERN.matcher(text).find();
    }
    
    /**
     * Strips animation tags from text, returning the raw content.
     * 
     * @param text The text to strip
     * @return Text without animation tags
     */
    public static String stripTags(String text) {
        if (text == null) return null;
        
        String result = text;
        result = RAINBOW_PATTERN.matcher(result).replaceAll("$4");
        result = GRADIENT_PATTERN.matcher(result).replaceAll("$4");
        result = FRAME_PATTERN.matcher(result).replaceAll("$3");
        
        return result;
    }
}
