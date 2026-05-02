package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Managers.Database.EclipseSQLiteManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Location;

import java.util.*;

public class EclipseWarpManager {
    private final EclipseSQLiteManager db;

    public EclipseWarpManager(Eclipse plugin) {
        this.db = new EclipseSQLiteManager(plugin.getDataFolder(), "Eclipse.warps.db");
    }

    public boolean saveWarp(String name, Location loc) {
        db.saveLocation(name.toLowerCase(), loc);
        return true;
    }

    public Location getWarp(String name) {
        return db.loadLocation(name.toLowerCase());
    }

    public void deleteWarp(String name) {
        db.delete(name.toLowerCase());
    }

    public List<String> getWarpList() {
        return db.getAllNames();
    }
    public boolean warpExists(String name) {
        return db.loadLocation(name.toLowerCase()) != null;
    }
}