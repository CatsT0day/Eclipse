package me.catst0day.Eclipse.Holograms.Animations;

import java.util.List;


public class FrameAnimation implements AnimatableText {
    
    private final List<String> frames;
    private final int updateInterval;
    private final String delimiter;
    private boolean active;
    private int currentFrame;
    
    /**
     * Creates a new frame animation.
     *
     * @param frames List of text frames to cycle through
     * @param updateInterval Ticks between frame changes
     * @param delimiter The delimiter used to split frames in the original string
     */
    public FrameAnimation(List<String> frames, int updateInterval, String delimiter) {
        this.frames = frames;
        this.updateInterval = Math.max(1, updateInterval);
        this.delimiter = delimiter;
        this.active = true;
        this.currentFrame = 0;
    }

    /**
     * Creates a new frame animation from a delimited string.
     *
     * @param text Text with frames separated by delimiter
     * @param updateInterval Ticks between frame changes
     * @param delimiter The delimiter to split frames (default: "||")
     */
    public FrameAnimation(String text, int updateInterval, String delimiter) {
        this.delimiter = delimiter != null ? delimiter : "||";
        this.frames = List.of(text.split(this.delimiter));
        this.updateInterval = Math.max(1, updateInterval);
        this.active = true;
        this.currentFrame = 0;
    }
    
    @Override
    public String getText(long tick) {
        if (!active || frames.isEmpty()) return "";
        
        long frameIndex = (tick / updateInterval) % frames.size();
        return frames.get((int) frameIndex);
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
        this.currentFrame = 0;
    }

    /**
     * Gets the number of frames in this animation.
     *
     * @return Frame count
     */
    public int getFrameCount() {
        return frames.size();
    }
}
