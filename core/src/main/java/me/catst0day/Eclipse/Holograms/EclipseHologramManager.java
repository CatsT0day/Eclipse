package me.catst0day.Eclipse.Holograms;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Holograms.Animations.AnimationParser;
import me.catst0day.Eclipse.Holograms.Animations.AnimatableText;
import me.catst0day.Eclipse.Managers.Database.EclipseSQLiteManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EclipseHologramManager {
    private final Eclipse plugin;
    private final EclipseSQLiteManager dbManager;
    private final Map<String, EclipseHologram> holograms = new ConcurrentHashMap<>();
    private final Map<UUID, Set<EclipseHologram>> visibleHolograms = new ConcurrentHashMap<>();
    private BukkitTask updateTask;
    
    public EclipseHologramManager(Eclipse plugin) {
        this.plugin = plugin;
        File dbFolder = new File(plugin.getDataFolder(), "holograms");
        this.dbManager = new EclipseSQLiteManager(dbFolder, "holograms.db");
        initDatabase();
        loadHolograms();
        startUpdateTask();
    }
    
    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS holograms (" +
                "name TEXT PRIMARY KEY, " +
                "uuid TEXT NOT NULL, " +
                "world TEXT NOT NULL, " +
                "x REAL NOT NULL, " +
                "y REAL NOT NULL, " +
                "z REAL NOT NULL, " +
                "yaw REAL NOT NULL, " +
                "pitch REAL NOT NULL, " +
                "view_distance INTEGER DEFAULT 48, " +
                "always_visible INTEGER DEFAULT 0, " +
                "update_interval INTEGER DEFAULT 20, " +
                "clickable INTEGER DEFAULT 0, " +
                "click_command TEXT DEFAULT '', " +
                "click_cost REAL DEFAULT 0.0, " +
                "lines TEXT NOT NULL);";
        
        try (Connection conn = dbManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadHolograms() {
        String sql = "SELECT * FROM holograms";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String worldName = rs.getString("world");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                float yaw = rs.getFloat("yaw");
                float pitch = rs.getFloat("pitch");
                int viewDistance = rs.getInt("view_distance");
                boolean alwaysVisible = rs.getBoolean("always_visible");
                int updateInterval = rs.getInt("update_interval");
                boolean clickable = rs.getBoolean("clickable");
                String clickCommand = rs.getString("click_command");
                double clickCost = rs.getDouble("click_cost");
                String linesData = rs.getString("lines");
                
                if (Bukkit.getWorld(worldName) != null) {
                    Location loc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
                    List<String> lines = Arrays.asList(linesData.split("\n"));
                    
                    EclipseHologram hologram = new EclipseHologram(name, loc, new ArrayList<>(lines));
                    hologram.setViewDistance(viewDistance);
                    hologram.setAlwaysVisible(alwaysVisible);
                    hologram.setUpdateInterval(updateInterval);
                    hologram.setClickable(clickable);
                    hologram.setClickCommand(clickCommand);
                    hologram.setClickCost(clickCost);
                    holograms.put(name.toLowerCase(), hologram);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean createHologram(String name, Location location, List<String> lines) {
        if (holograms.containsKey(name.toLowerCase())) {
            return false;
        }
        
        EclipseHologram hologram = new EclipseHologram(name, location, new ArrayList<>(lines));
        holograms.put(name.toLowerCase(), hologram);
        saveHologram(hologram);
        
        // Auto-detect and register animations
        registerAnimations(hologram);
        
        return true;
    }
    
    public boolean deleteHologram(String name) {
        EclipseHologram hologram = holograms.remove(name.toLowerCase());
        if (hologram == null) {
            return false;
        }
        
        // Remove animations
        plugin.getAnimationManager().removeHologramAnimations(name);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            EclipseHologramPacket.hideHologram(player, hologram);
        }

        deleteHologramFromDB(name);
        return true;
    }
    
    public EclipseHologram getHologram(String name) {
        return holograms.get(name.toLowerCase());
    }
    
    public EclipseHologram getHologramByUUID(UUID uuid) {
        for (EclipseHologram hologram : holograms.values()) {
            if (hologram.getUniqueId().equals(uuid)) {
                return hologram;
            }
        }
        return null;
    }
    
    public Collection<EclipseHologram> getAllHolograms() {
        return holograms.values();
    }
    
    public List<String> getHologramNames() {
        return new ArrayList<>(holograms.keySet());
    }
    
    public boolean hologramExists(String name) {
        return holograms.containsKey(name.toLowerCase());
    }
    
    private void saveHologram(EclipseHologram hologram) {
        String sql = "REPLACE INTO holograms(name, uuid, world, x, y, z, yaw, pitch, view_distance, always_visible, update_interval, clickable, click_command, click_cost, lines) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hologram.getName());
            pstmt.setString(2, hologram.getUniqueId().toString());
            pstmt.setString(3, hologram.getLocation().getWorld().getName());
            pstmt.setDouble(4, hologram.getLocation().getX());
            pstmt.setDouble(5, hologram.getLocation().getY());
            pstmt.setDouble(6, hologram.getLocation().getZ());
            pstmt.setFloat(7, hologram.getLocation().getYaw());
            pstmt.setFloat(8, hologram.getLocation().getPitch());
            pstmt.setInt(9, hologram.getViewDistance());
            pstmt.setBoolean(10, hologram.isAlwaysVisible());
            pstmt.setInt(11, hologram.getUpdateInterval());
            pstmt.setBoolean(12, hologram.isClickable());
            pstmt.setString(13, hologram.getClickCommand());
            pstmt.setDouble(14, hologram.getClickCost());
            pstmt.setString(15, String.join("\n", hologram.getLines()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void deleteHologramFromDB(String name) {
        String sql = "DELETE FROM holograms WHERE name = ?";
        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateHologram(EclipseHologram hologram) {
        saveHologram(hologram);
        
        // Re-register animations if lines changed
        registerAnimations(hologram);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hologram.isVisibleTo(player)) {
                EclipseHologramPacket.updateHologram(player, hologram);
            }
        }
    }
    
    private void startUpdateTask() {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updatePlayerHolograms(player);
            }
        }, 20L, 20L);
    }
    
    private void updatePlayerHolograms(Player player) {
        Set<EclipseHologram> currentlyVisible = new HashSet<>();
        
        for (EclipseHologram hologram : holograms.values()) {
            if (hologram.isVisibleTo(player)) {
                currentlyVisible.add(hologram);
                Set<EclipseHologram> playerVisible = visibleHolograms.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
                
                if (!playerVisible.contains(hologram)) {
                    EclipseHologramPacket.showHologram(player, hologram);
                    playerVisible.add(hologram);
                }
            }
        }
        Set<EclipseHologram> playerVisible = visibleHolograms.get(player.getUniqueId());
        if (playerVisible != null) {
            for (EclipseHologram hologram : new HashSet<>(playerVisible)) {
                if (!currentlyVisible.contains(hologram)) {
                    EclipseHologramPacket.hideHologram(player, hologram);
                    playerVisible.remove(hologram);
                }
            }
            if (playerVisible.isEmpty()) {
                visibleHolograms.remove(player.getUniqueId());
            }
        }
    }
    
    public void showAllHologramsToPlayer(Player player) {
        for (EclipseHologram hologram : holograms.values()) {
            if (hologram.isVisibleTo(player)) {
                EclipseHologramPacket.showHologram(player, hologram);
                visibleHolograms.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(hologram);
            }
        }
    }
    
    public void hideAllHologramsFromPlayer(Player player) {
        Set<EclipseHologram> playerVisible = visibleHolograms.remove(player.getUniqueId());
        if (playerVisible != null) {
            for (EclipseHologram hologram : playerVisible) {
                EclipseHologramPacket.hideHologram(player, hologram);
            }
        }
    }
    
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            hideAllHologramsFromPlayer(player);
        }
        
        // Clear all animations
        for (String holoName : holograms.keySet()) {
            plugin.getAnimationManager().removeHologramAnimations(holoName);
        }
        
        holograms.clear();
        visibleHolograms.clear();
    }
    
    /**
     * Automatically detects and registers animations for a hologram.
     * 
     * @param hologram The hologram to check for animations
     */
    private void registerAnimations(EclipseHologram hologram) {
        List<String> lines = hologram.getLines();
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            
            if (AnimationParser.containsAnimation(line)) {
                AnimatableText animation = AnimationParser.parse(line);
                if (animation != null) {
                    plugin.getAnimationManager().registerAnimation(hologram.getName(), i, animation);
                }
            }
        }
    }
}
