package me.catst0day.Eclipse.Announcements;

public class Announcement {
    private final int id;
    private final String message;
    private final int intervalMinutes;
    private final boolean enabled;
    private final boolean randomOrder;

    public Announcement(int id, String message, int intervalMinutes, boolean enabled, boolean randomOrder) {
        this.id = id;
        this.message = message;
        this.intervalMinutes = intervalMinutes;
        this.enabled = enabled;
        this.randomOrder = randomOrder;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isRandomOrder() {
        return randomOrder;
    }
}
