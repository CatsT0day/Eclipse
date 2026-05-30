package me.catst0day.Eclipse.Holograms.Animations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AnimationParser {
    
    private static final Pattern RAINBOW_PATTERN = Pattern.compile("<rainbow:([^:]+):([^:]+):([^:]+)>(.*?)</rainbow>");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:([^:]+):([^:]+):([^:]+)>(.*?)</gradient>");
    private static final Pattern FRAME_PATTERN = Pattern.compile("<frame:([^:]+):([^:]+)>(.*?)</frame>");
    private static final Pattern PULSE_PATTERN = Pattern.compile("<pulse:([^:]+):([^:]+):([^:]+):([^:]+)>(.*?)</pulse>");
    private static final Pattern SCROLL_PATTERN = Pattern.compile("<scroll:([^:]+):([^:]+):([^:]+)>(.*?)</scroll>");
    private static final Pattern TYPEWRITER_PATTERN = Pattern.compile("<typewriter:([^:]+):([^:]+):([^:]+):?([^:]*)>(.*?)</typewriter>");
    private static final Pattern BOUNCE_PATTERN = Pattern.compile("<bounce:([^:]+):([^:]+):([^:]+)>(.*?)</bounce>");
    
    
    public static AnimatableText parse(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        
        AnimatableText rainbow = parseRainbow(text);
        if (rainbow != null) return rainbow;
        
        
        AnimatableText gradient = parseGradient(text);
        if (gradient != null) return gradient;
        
        
        AnimatableText frame = parseFrame(text);
        if (frame != null) return frame;
        
        
        AnimatableText pulse = parsePulse(text);
        if (pulse != null) return pulse;
        
        
        AnimatableText scroll = parseScroll(text);
        if (scroll != null) return scroll;
        
        
        AnimatableText typewriter = parseTypewriter(text);
        if (typewriter != null) return typewriter;
        
        
        AnimatableText bounce = parseBounce(text);
        if (bounce != null) return bounce;
        
        return null;
    }
    
    
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
    
    
    private static AnimatableText parsePulse(String text) {
        Matcher matcher = PULSE_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        
        try {
            int interval = Integer.parseInt(matcher.group(1));
            float minOpacity = Float.parseFloat(matcher.group(2));
            float maxOpacity = Float.parseFloat(matcher.group(3));
            float speed = Float.parseFloat(matcher.group(4));
            String content = matcher.group(5);
            
            return new PulseAnimation(content, interval, minOpacity, maxOpacity, speed);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    
    private static AnimatableText parseScroll(String text) {
        Matcher matcher = SCROLL_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        
        try {
            int interval = Integer.parseInt(matcher.group(1));
            int speed = Integer.parseInt(matcher.group(2));
            int width = Integer.parseInt(matcher.group(3));
            String content = matcher.group(4);
            
            return new ScrollAnimation(content, interval, speed, width);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    
    private static AnimatableText parseTypewriter(String text) {
        Matcher matcher = TYPEWRITER_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        
        try {
            int interval = Integer.parseInt(matcher.group(1));
            int speed = Integer.parseInt(matcher.group(2));
            boolean loop = Boolean.parseBoolean(matcher.group(3));
            String cursor = matcher.group(4);
            String content = matcher.group(5);
            
            if (cursor != null && !cursor.isEmpty()) {
                return new TypewriterAnimation(content, interval, speed, loop, cursor);
            } else {
                return new TypewriterAnimation(content, interval, speed, loop);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    
    private static AnimatableText parseBounce(String text) {
        Matcher matcher = BOUNCE_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        
        try {
            int interval = Integer.parseInt(matcher.group(1));
            float amplitude = Float.parseFloat(matcher.group(2));
            float speed = Float.parseFloat(matcher.group(3));
            String content = matcher.group(4);
            
            return new BounceAnimation(content, interval, amplitude, speed);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    
    public static boolean containsAnimation(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        return RAINBOW_PATTERN.matcher(text).find() ||
               GRADIENT_PATTERN.matcher(text).find() ||
               FRAME_PATTERN.matcher(text).find() ||
               PULSE_PATTERN.matcher(text).find() ||
               SCROLL_PATTERN.matcher(text).find() ||
               TYPEWRITER_PATTERN.matcher(text).find() ||
               BOUNCE_PATTERN.matcher(text).find();
    }
    
    
    public static String stripTags(String text) {
        if (text == null) return null;
        
        String result = text;
        result = RAINBOW_PATTERN.matcher(result).replaceAll("$4");
        result = GRADIENT_PATTERN.matcher(result).replaceAll("$4");
        result = FRAME_PATTERN.matcher(result).replaceAll("$3");
        result = PULSE_PATTERN.matcher(result).replaceAll("$5");
        result = SCROLL_PATTERN.matcher(result).replaceAll("$4");
        result = TYPEWRITER_PATTERN.matcher(result).replaceAll("$5");
        result = BOUNCE_PATTERN.matcher(result).replaceAll("$4");
        
        return result;
    }
}
