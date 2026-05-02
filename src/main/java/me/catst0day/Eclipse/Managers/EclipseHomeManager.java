package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Managers.Database.EclipseSQLiteManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Location;
import java.io.File;
import java.util.List;
import java.util.UUID;

public class EclipseHomeManager {
    private final File storageFolder;

    public EclipseHomeManager(Eclipse plugin) {
        this.storageFolder = new File(plugin.getDataFolder(), "playerdata");
    }

    private EclipseSQLiteManager getPlayerDB(UUID uuid) {
        String fileName = "Eclipse.player." + uuid.toString() + ".db";
        return new EclipseSQLiteManager(storageFolder, fileName);
    }

    public boolean setHome(UUID uuid, String name, Location loc) {
        return getPlayerDB(uuid).saveLocation(name, loc);
    }

    public Location getHome(UUID uuid, String name) {
        return getPlayerDB(uuid).loadLocation(name);
    }

    public boolean deleteHome(UUID uuid, String name) {
        return getPlayerDB(uuid).delete(name);
    }

    public List<String> getPlayerHomes(UUID uuid) {
        return getPlayerDB(uuid).getAllNames();
    }

    public boolean homeExists(UUID uuid, String name) {
        return getHome(uuid, name) != null;
    }
}