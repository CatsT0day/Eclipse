package me.catst0day.Eclipse.Holograms.Settings;

import java.util.HashMap;
import java.util.Map;

public class EclipseHoloTextSettings {
    private TextAlignment textAlignment = TextAlignment.CENTER;
    private String backgroundColor = "#000000";
    private int backgroundAlpha = 0;
    private int textAlpha = 255;
    private boolean doubleSided = true;
    private boolean shadowed = false;
    private boolean seeThrough = false;
    private int lineWidth = 200;
    private int fillerAmount = 0;

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public EclipseHoloTextSettings setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public EclipseHoloTextSettings setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public EclipseHoloTextSettings setBackgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        return this;
    }

    public int getTextAlpha() {
        return textAlpha;
    }

    public EclipseHoloTextSettings setTextAlpha(int textAlpha) {
        this.textAlpha = textAlpha;
        return this;
    }

    public boolean isDoubleSided() {
        return doubleSided;
    }

    public EclipseHoloTextSettings setDoubleSided(boolean doubleSided) {
        this.doubleSided = doubleSided;
        return this;
    }

    public boolean isShadowed() {
        return shadowed;
    }

    public EclipseHoloTextSettings setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
        return this;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }

    public EclipseHoloTextSettings setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public EclipseHoloTextSettings setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    public int getFillerAmount() {
        return fillerAmount;
    }

    public EclipseHoloTextSettings setFillerAmount(int fillerAmount) {
        this.fillerAmount = fillerAmount;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("textAlignment", textAlignment.name());
        map.put("backgroundColor", backgroundColor);
        map.put("backgroundAlpha", backgroundAlpha);
        map.put("textAlpha", textAlpha);
        map.put("doubleSided", doubleSided);
        map.put("shadowed", shadowed);
        map.put("seeThrough", seeThrough);
        map.put("lineWidth", lineWidth);
        map.put("fillerAmount", fillerAmount);
        return map;
    }

    public static EclipseHoloTextSettings deserialize(Map<String, Object> entry) {
        EclipseHoloTextSettings settings = new EclipseHoloTextSettings();
        if (entry.containsKey("textAlignment")) {
            try {
                settings.textAlignment = TextAlignment.valueOf((String) entry.get("textAlignment"));
            } catch (Exception ignored) {
            }
        }
        if (entry.containsKey("backgroundColor")) {
            settings.backgroundColor = (String) entry.get("backgroundColor");
        }
        if (entry.containsKey("backgroundAlpha")) {
            settings.backgroundAlpha = (int) entry.get("backgroundAlpha");
        }
        if (entry.containsKey("textAlpha")) {
            settings.textAlpha = (int) entry.get("textAlpha");
        }
        if (entry.containsKey("doubleSided")) {
            settings.doubleSided = (boolean) entry.get("doubleSided");
        }
        if (entry.containsKey("shadowed")) {
            settings.shadowed = (boolean) entry.get("shadowed");
        }
        if (entry.containsKey("seeThrough")) {
            settings.seeThrough = (boolean) entry.get("seeThrough");
        }
        if (entry.containsKey("lineWidth")) {
            settings.lineWidth = (int) entry.get("lineWidth");
        }
        if (entry.containsKey("fillerAmount")) {
            settings.fillerAmount = (int) entry.get("fillerAmount");
        }
        return settings;
    }
}
