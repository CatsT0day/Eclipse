package me.catst0day.Eclipse.Holograms.Settings;

import java.util.HashMap;
import java.util.Map;

public class EclipseHoloPageSettings {
    private boolean autoPagination = false;
    private int autoPaginationOffset = 0;
    private boolean showPageNumbers = true;
    private String nextPageText = "&a[Next Page]";
    private String prevPageText = "&c[Prev Page]";

    public boolean isAutoPagination() {
        return autoPagination;
    }

    public EclipseHoloPageSettings setAutoPagination(boolean autoPagination) {
        this.autoPagination = autoPagination;
        return this;
    }

    public int getAutoPaginationOffset() {
        return autoPaginationOffset;
    }

    public EclipseHoloPageSettings setAutoPaginationOffset(int autoPaginationOffset) {
        this.autoPaginationOffset = autoPaginationOffset;
        return this;
    }

    public boolean isShowPageNumbers() {
        return showPageNumbers;
    }

    public EclipseHoloPageSettings setShowPageNumbers(boolean showPageNumbers) {
        this.showPageNumbers = showPageNumbers;
        return this;
    }

    public String getNextPageText() {
        return nextPageText;
    }

    public EclipseHoloPageSettings setNextPageText(String nextPageText) {
        this.nextPageText = nextPageText;
        return this;
    }

    public String getPrevPageText() {
        return prevPageText;
    }

    public EclipseHoloPageSettings setPrevPageText(String prevPageText) {
        this.prevPageText = prevPageText;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("autoPagination", autoPagination);
        map.put("autoPaginationOffset", autoPaginationOffset);
        map.put("showPageNumbers", showPageNumbers);
        map.put("nextPageText", nextPageText);
        map.put("prevPageText", prevPageText);
        return map;
    }

    public static EclipseHoloPageSettings deserialize(Map<String, Object> entry) {
        EclipseHoloPageSettings settings = new EclipseHoloPageSettings();
        if (entry.containsKey("autoPagination")) {
            settings.autoPagination = (boolean) entry.get("autoPagination");
        }
        if (entry.containsKey("autoPaginationOffset")) {
            settings.autoPaginationOffset = (int) entry.get("autoPaginationOffset");
        }
        if (entry.containsKey("showPageNumbers")) {
            settings.showPageNumbers = (boolean) entry.get("showPageNumbers");
        }
        if (entry.containsKey("nextPageText")) {
            settings.nextPageText = (String) entry.get("nextPageText");
        }
        if (entry.containsKey("prevPageText")) {
            settings.prevPageText = (String) entry.get("prevPageText");
        }
        return settings;
    }
}
