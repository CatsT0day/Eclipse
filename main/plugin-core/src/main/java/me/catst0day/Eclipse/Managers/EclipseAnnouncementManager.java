package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Announcements.Announcement;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EclipseAnnouncementManager {
    private final Eclipse plugin;
    private final File configFile;
    private YamlConfiguration config;
    private final List<Announcement> announcements;
    private int currentAnnouncementIndex = 0;
    private int taskId = -1;

    public EclipseAnnouncementManager(Eclipse plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "announcements.yml");
        this.announcements = new ArrayList<>();
        loadConfig();
    }

    private void loadConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("announcements.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        announcements.clear();
        if (config.contains("announcements")) {
            for (String key : config.getConfigurationSection("announcements").getKeys(false)) {
                String path = "announcements." + key;
                String message = config.getString(path + ".message", "");
                int interval = config.getInt(path + ".interval", 5);
                boolean enabled = config.getBoolean(path + ".enabled", true);
                boolean randomOrder = config.getBoolean(path + ".randomOrder", false);
                
                if (!message.isEmpty()) {
                    int id = Integer.parseInt(key);
                    announcements.add(new Announcement(id, message, interval, enabled, randomOrder));
                }
            }
        }
    }

    public void reloadConfig() {
        loadConfig();
        restartAnnouncementTask();
    }

    public void startAnnouncementTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        if (announcements.isEmpty()) {
            return;
        }

        // Get the minimum interval from all enabled announcements
        int minInterval = announcements.stream()
                .filter(Announcement::isEnabled)
                .mapToInt(Announcement::getIntervalMinutes)
                .min()
                .orElse(5);

        long intervalTicks = minInterval * 60L * 20L; // Convert minutes to ticks

        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::broadcastNextAnnouncement, 
                intervalTicks, intervalTicks).getTaskId();
    }

    public void stopAnnouncementTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public void restartAnnouncementTask() {
        stopAnnouncementTask();
        startAnnouncementTask();
    }

    private void broadcastNextAnnouncement() {
        List<Announcement> enabledAnnouncements = new ArrayList<>();
        for (Announcement announcement : announcements) {
            if (announcement.isEnabled()) {
                enabledAnnouncements.add(announcement);
            }
        }

        if (enabledAnnouncements.isEmpty()) {
            return;
        }

        Announcement announcement;
        if (enabledAnnouncements.get(0).isRandomOrder()) {
            Collections.shuffle(enabledAnnouncements);
            announcement = enabledAnnouncements.get(0);
        } else {
            announcement = enabledAnnouncements.get(currentAnnouncementIndex % enabledAnnouncements.size());
            currentAnnouncementIndex++;
        }

        String message = plugin.getMessage("announcementPrefix") + announcement.getMessage();
        Bukkit.broadcastMessage(message);
    }

    public boolean addAnnouncement(String message, int intervalMinutes, boolean randomOrder) {
        int newId = announcements.size() + 1;
        String path = "announcements." + newId;
        config.set(path + ".message", message);
        config.set(path + ".interval", intervalMinutes);
        config.set(path + ".enabled", true);
        config.set(path + ".randomOrder", randomOrder);
        
        try {
            config.save(configFile);
            loadAnnouncements();
            restartAnnouncementTask();
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save announcement: " + e.getMessage());
            return false;
        }
    }

    public boolean removeAnnouncement(int id) {
        String path = "announcements." + id;
        if (!config.contains(path)) {
            return false;
        }
        
        config.set(path, null);
        
        try {
            config.save(configFile);
            loadAnnouncements();
            restartAnnouncementTask();
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to remove announcement: " + e.getMessage());
            return false;
        }
    }

    public boolean toggleAnnouncement(int id) {
        String path = "announcements." + id;
        if (!config.contains(path)) {
            return false;
        }
        
        boolean currentState = config.getBoolean(path + ".enabled", true);
        config.set(path + ".enabled", !currentState);
        
        try {
            config.save(configFile);
            loadAnnouncements();
            restartAnnouncementTask();
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to toggle announcement: " + e.getMessage());
            return false;
        }
    }

    public List<Announcement> getAllAnnouncements() {
        return new ArrayList<>(announcements);
    }

    public Announcement getAnnouncement(int id) {
        return announcements.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void shutdown() {
        stopAnnouncementTask();
    }
}
