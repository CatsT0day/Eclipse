package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Managers.Database.EclipseSQLiteManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EclipseKitManager {
    private final Eclipse plugin;
    private final EclipseSQLiteManager database;
    private final Map<String, Kit> kits = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> playerCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> claimedOneTimeKits = new ConcurrentHashMap<>();

    public EclipseKitManager(Eclipse plugin) {
        this.plugin = plugin;
        this.database = new EclipseSQLiteManager(plugin.getDataFolder(), "kits.db");
        initDatabase();
        loadKits();
        loadPlayerData();
    }

    private void initDatabase() {
        String kitsTable = "CREATE TABLE IF NOT EXISTS kits (" +
                "name TEXT PRIMARY KEY, " +
                "display_name TEXT NOT NULL, " +
                "cooldown_seconds INTEGER DEFAULT 86400, " +
                "permission TEXT DEFAULT '', " +
                "one_time INTEGER DEFAULT 0, " +
                "first_join INTEGER DEFAULT 0, " +
                "display_order INTEGER DEFAULT 0, " +
                "icon_data BLOB)";
        String itemsTable = "CREATE TABLE IF NOT EXISTS kit_items (" +
                "kit_name TEXT NOT NULL, " +
                "slot INTEGER NOT NULL, " +
                "item_data BLOB NOT NULL, " +
                "PRIMARY KEY (kit_name, slot), " +
                "FOREIGN KEY (kit_name) REFERENCES kits(name) ON DELETE CASCADE)";
        String cooldownsTable = "CREATE TABLE IF NOT EXISTS kit_cooldowns (" +
                "player_uuid TEXT NOT NULL, " +
                "kit_name TEXT NOT NULL, " +
                "last_claim INTEGER NOT NULL, " +
                "PRIMARY KEY (player_uuid, kit_name))";
        String oneTimeTable = "CREATE TABLE IF NOT EXISTS kit_claimed_one_time (" +
                "player_uuid TEXT NOT NULL, " +
                "kit_name TEXT NOT NULL, " +
                "PRIMARY KEY (player_uuid, kit_name))";

        try (Connection conn = database.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(kitsTable);
            stmt.execute(itemsTable);
            stmt.execute(cooldownsTable);
            stmt.execute(oneTimeTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadKits() {
        kits.clear();
        String sql = "SELECT * FROM kits ORDER BY display_order";

        try (Connection conn = database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                Kit kit = new Kit(name);
                kit.setDisplayName(rs.getString("display_name"));
                kit.setCooldownSeconds(rs.getLong("cooldown_seconds"));
                kit.setPermission(rs.getString("permission"));
                kit.setOneTime(rs.getInt("one_time") == 1);
                kit.setFirstJoin(rs.getInt("first_join") == 1);
                kit.setOrder(rs.getInt("display_order"));
                byte[] iconData = rs.getBytes("icon_data");
                if (iconData != null) {
                    kit.setIcon(deserializeItem(iconData));
                }
                loadKitItems(kit);

                kits.put(name.toLowerCase(), kit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadKitItems(Kit kit) {
        String sql = "SELECT item_data FROM kit_items WHERE kit_name = ? ORDER BY slot";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kit.getName());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                byte[] itemData = rs.getBytes("item_data");
                if (itemData != null) {
                    ItemStack item = deserializeItem(itemData);
                    if (item != null) {
                        kit.addItem(item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerData() {
        String cooldownSql = "SELECT * FROM kit_cooldowns";
        try (Connection conn = database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(cooldownSql)) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                String kitName = rs.getString("kit_name");
                long lastClaim = rs.getLong("last_claim");
                playerCooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                        .put(kitName.toLowerCase(), lastClaim);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String oneTimeSql = "SELECT * FROM kit_claimed_one_time";
        try (Connection conn = database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(oneTimeSql)) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                String kitName = rs.getString("kit_name");
                claimedOneTimeKits.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet())
                        .add(kitName.toLowerCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveKit(Kit kit) {
        String sql = "REPLACE INTO kits(name, display_name, cooldown_seconds, permission, one_time, first_join, display_order, icon_data) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kit.getName().toLowerCase());
            pstmt.setString(2, kit.getDisplayName());
            pstmt.setLong(3, kit.getCooldownSeconds());
            pstmt.setString(4, kit.getPermission());
            pstmt.setInt(5, kit.isOneTime() ? 1 : 0);
            pstmt.setInt(6, kit.isFirstJoin() ? 1 : 0);
            pstmt.setInt(7, kit.getOrder());
            pstmt.setBytes(8, kit.getIcon() != null ? serializeItem(kit.getIcon()) : null);
            pstmt.executeUpdate();
            saveKitItems(kit);

            kits.put(kit.getName().toLowerCase(), kit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveKitItems(Kit kit) {
        String deleteSql = "DELETE FROM kit_items WHERE kit_name = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, kit.getName().toLowerCase());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String insertSql = "INSERT INTO kit_items(kit_name, slot, item_data) VALUES(?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            List<ItemStack> items = kit.getItems();
            for (int i = 0; i < items.size(); i++) {
                pstmt.setString(1, kit.getName().toLowerCase());
                pstmt.setInt(2, i);
                pstmt.setBytes(3, serializeItem(items.get(i)));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private byte[] serializeItem(ItemStack item) {
        if (item == null) return null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
            boos.writeObject(item);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ItemStack deserializeItem(byte[] data) {
        if (data == null) return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
            return (ItemStack) bois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteKit(String name) {
        Kit kit = kits.remove(name.toLowerCase());
        if (kit == null) return false;

        String sql = "DELETE FROM kits WHERE name = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.toLowerCase());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    public Collection<Kit> getAllKits() {
        return kits.values();
    }

    public List<Kit> getAvailableKitsForPlayer(EclipsePlr player) {
        List<Kit> available = new ArrayList<>();
        for (Kit kit : kits.values()) {
            if (canClaimKit(player, kit)) {
                available.add(kit);
            }
        }
        available.sort(Comparator.comparingInt(Kit::getOrder));
        return available;
    }

    public boolean canClaimKit(EclipsePlr player, Kit kit) {
        if (!kit.getPermission().isEmpty()) {
            Player p = player.getPlayer();
            if (p == null || !p.hasPermission(kit.getPermission())) {
                return false;
            }
        }
        if (kit.isOneTime()) {
            Set<String> claimed = claimedOneTimeKits.get(player.getUniqueId());
            if (claimed != null && claimed.contains(kit.getName().toLowerCase())) {
                return false;
            }
        }
        return !isOnCooldown(player, kit);
    }

    public boolean isOnCooldown(EclipsePlr player, Kit kit) {
        Map<String, Long> cooldowns = playerCooldowns.get(player.getUniqueId());
        if (cooldowns == null) return false;

        Long lastClaim = cooldowns.get(kit.getName().toLowerCase());
        if (lastClaim == null) return false;

        long timePassed = (System.currentTimeMillis() - lastClaim) / 1000;
        return timePassed < kit.getCooldownSeconds();
    }

    public long getCooldownRemaining(EclipsePlr player, Kit kit) {
        Map<String, Long> cooldowns = playerCooldowns.get(player.getUniqueId());
        if (cooldowns == null) return 0;

        Long lastClaim = cooldowns.get(kit.getName().toLowerCase());
        if (lastClaim == null) return 0;

        long timePassed = (System.currentTimeMillis() - lastClaim) / 1000;
        long remaining = kit.getCooldownSeconds() - timePassed;
        return Math.max(0, remaining);
    }

    public String formatCooldown(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }

    public boolean claimKit(EclipsePlr player, Kit kit) {
        Player p = player.getPlayer();
        if (p == null) return false;

        if (!canClaimKit(player, kit)) {
            return false;
        }
        for (ItemStack item : kit.getItems()) {
            if (item != null) {
                p.getInventory().addItem(item.clone());
            }
        }
        setCooldown(player, kit);
        if (kit.isOneTime()) {
            claimedOneTimeKits.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet())
                    .add(kit.getName().toLowerCase());
            saveOneTimeClaimed(player, kit);
        }

        return true;
    }

    private void setCooldown(EclipsePlr player, Kit kit) {
        Map<String, Long> cooldowns = playerCooldowns.computeIfAbsent(
                player.getUniqueId(), k -> new ConcurrentHashMap<>());
        cooldowns.put(kit.getName().toLowerCase(), System.currentTimeMillis());

        String sql = "REPLACE INTO kit_cooldowns(player_uuid, kit_name, last_claim) VALUES(?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player.getUniqueId().toString());
            pstmt.setString(2, kit.getName().toLowerCase());
            pstmt.setLong(3, System.currentTimeMillis());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveOneTimeClaimed(EclipsePlr player, Kit kit) {
        String sql = "INSERT OR IGNORE INTO kit_claimed_one_time(player_uuid, kit_name) VALUES(?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player.getUniqueId().toString());
            pstmt.setString(2, kit.getName().toLowerCase());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetCooldown(EclipsePlr player, String kitName) {
        Map<String, Long> cooldowns = playerCooldowns.get(player.getUniqueId());
        if (cooldowns != null) {
            cooldowns.remove(kitName.toLowerCase());
        }

        String sql = "DELETE FROM kit_cooldowns WHERE player_uuid = ? AND kit_name = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player.getUniqueId().toString());
            pstmt.setString(2, kitName.toLowerCase());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetAllCooldowns(String kitName) {
        playerCooldowns.forEach((uuid, cooldowns) -> cooldowns.remove(kitName.toLowerCase()));

        String sql = "DELETE FROM kit_cooldowns WHERE kit_name = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kitName.toLowerCase());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onPlayerJoin(EclipsePlr player) {
        for (Kit kit : kits.values()) {
            if (kit.isFirstJoin()) {
                claimKit(player, kit);
            }
        }
    }

    public void shutdown() {
        database.close();
    }
    public static class Kit {
        private final String name;
        private String displayName;
        private long cooldownSeconds = 86400;
        private String permission = "";
        private boolean oneTime = false;
        private boolean firstJoin = false;
        private int order = 0;
        private ItemStack icon;
        private final List<ItemStack> items = new ArrayList<>();

        public Kit(String name) {
            this.name = name;
            this.displayName = name;
        }

        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public long getCooldownSeconds() { return cooldownSeconds; }
        public void setCooldownSeconds(long cooldownSeconds) { this.cooldownSeconds = cooldownSeconds; }
        public String getPermission() { return permission; }
        public void setPermission(String permission) { this.permission = permission; }
        public boolean isOneTime() { return oneTime; }
        public void setOneTime(boolean oneTime) { this.oneTime = oneTime; }
        public boolean isFirstJoin() { return firstJoin; }
        public void setFirstJoin(boolean firstJoin) { this.firstJoin = firstJoin; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
        public ItemStack getIcon() { return icon; }
        public void setIcon(ItemStack icon) { this.icon = icon; }
        public List<ItemStack> getItems() { return items; }
        public void addItem(ItemStack item) { items.add(item); }
    }
}
