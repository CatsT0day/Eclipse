package me.catst0day.Eclipse.Holograms.Settings;

import java.util.HashMap;
import java.util.Map;

public class EclipseHoloAnimSettings {
    private boolean fadeInEnabled = false;
    private int fadeInTicks = 20;
    private boolean fadeOutEnabled = false;
    private int fadeOutTicks = 20;
    private boolean autoPage = false;
    private int autoPageIntervalTicks = 100;

    public boolean isFadeInEnabled() {
        return fadeInEnabled;
    }

    public EclipseHoloAnimSettings setFadeInEnabled(boolean fadeInEnabled) {
        this.fadeInEnabled = fadeInEnabled;
        return this;
    }

    public int getFadeInTicks() {
        return fadeInTicks;
    }

    public EclipseHoloAnimSettings setFadeInTicks(int fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
        return this;
    }

    public boolean isFadeOutEnabled() {
        return fadeOutEnabled;
    }

    public EclipseHoloAnimSettings setFadeOutEnabled(boolean fadeOutEnabled) {
        this.fadeOutEnabled = fadeOutEnabled;
        return this;
    }

    public int getFadeOutTicks() {
        return fadeOutTicks;
    }

    public EclipseHoloAnimSettings setFadeOutTicks(int fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
        return this;
    }

    public boolean isAutoPage() {
        return autoPage;
    }

    public EclipseHoloAnimSettings setAutoPage(boolean autoPage) {
        this.autoPage = autoPage;
        return this;
    }

    public int getAutoPageIntervalTicks() {
        return autoPageIntervalTicks;
    }

    public EclipseHoloAnimSettings setAutoPageIntervalTicks(int autoPageIntervalTicks) {
        this.autoPageIntervalTicks = autoPageIntervalTicks;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("fadeInEnabled", fadeInEnabled);
        map.put("fadeInTicks", fadeInTicks);
        map.put("fadeOutEnabled", fadeOutEnabled);
        map.put("fadeOutTicks", fadeOutTicks);
        map.put("autoPage", autoPage);
        map.put("autoPageIntervalTicks", autoPageIntervalTicks);
        return map;
    }

    public static EclipseHoloAnimSettings deserialize(Map<String, Object> entry) {
        EclipseHoloAnimSettings settings = new EclipseHoloAnimSettings();
        if (entry.containsKey("fadeInEnabled")) {
            settings.fadeInEnabled = (boolean) entry.get("fadeInEnabled");
        }
        if (entry.containsKey("fadeInTicks")) {
            settings.fadeInTicks = (int) entry.get("fadeInTicks");
        }
        if (entry.containsKey("fadeOutEnabled")) {
            settings.fadeOutEnabled = (boolean) entry.get("fadeOutEnabled");
        }
        if (entry.containsKey("fadeOutTicks")) {
            settings.fadeOutTicks = (int) entry.get("fadeOutTicks");
        }
        if (entry.containsKey("autoPage")) {
            settings.autoPage = (boolean) entry.get("autoPage");
        }
        if (entry.containsKey("autoPageIntervalTicks")) {
            settings.autoPageIntervalTicks = (int) entry.get("autoPageIntervalTicks");
        }
        return settings;
    }
}
