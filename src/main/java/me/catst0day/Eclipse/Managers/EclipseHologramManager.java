package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EclipseHologramManager {

    private final Eclipse plugin;
    private final File file;
    private final FileConfiguration config;
    private final Map<UUID, EclipseHologram> holograms = new ConcurrentHashMap<>();

    public EclipseHologramManager(Eclipse plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "holograms.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException ignored) {}
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }

    public void create(UUID id, Location loc, List<String> lines) {
        EclipseHologram holo = new EclipseHologram(id, loc, lines);
        holograms.put(holo.getId(), holo);
        saveAll();
    }

    public void removeHologram(UUID id) {
        EclipseHologram holo = holograms.remove(id);
        if (holo != null) {
            Bukkit.getOnlinePlayers().forEach(holo::hide);
            saveAll();
        }
    }

    public void loadAll() {
        if (!config.contains("holograms")) return;
        for (String key : config.getConfigurationSection("holograms").getKeys(false)) {
            UUID id = UUID.fromString(key);
            Location loc = config.getLocation("holograms." + key + ".location");
            List<String> lines = config.getStringList("holograms." + key + ".lines");
            holograms.put(id, new EclipseHologram(id, loc, lines));
        }
    }

    public void saveAll() {
        config.set("holograms", null);
        holograms.forEach((id, holo) -> {
            config.set("holograms." + id + ".location", holo.getBaseLocation());
            config.set("holograms." + id + ".lines", holo.getRawLines());
        });
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public Map<UUID, EclipseHologram> getHolograms() { return holograms; }
}