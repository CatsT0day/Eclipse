package me.catst0day.Eclipse.Moderation;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EclipseModerationManager {
    private final Eclipse plugin;
    private final File dbFile;
    private final Map<UUID, List<Punishment>> punishmentCache = new ConcurrentHashMap<>();
    private final Map<String, UUID> ipCache = new ConcurrentHashMap<>();
    private int nextId = 1;

    public EclipseModerationManager(Eclipse plugin) {
        this.plugin = plugin;
        this.dbFile = new File(plugin.getDataFolder(), "moderation.db");
        initDatabase();
        loadAllPunishments();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
    }

    private void initDatabase() {
        if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();
        String sql = "CREATE TABLE IF NOT EXISTS punishments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT NOT NULL, " +
                "target_uuid TEXT NOT NULL, " +
                "target_name TEXT NOT NULL, " +
                "issuer_uuid TEXT NOT NULL, " +
                "issuer_name TEXT NOT NULL, " +
                "reason TEXT DEFAULT '', " +
                "date LONG NOT NULL, " +
                "expiry LONG DEFAULT 0, " +
                "active INTEGER DEFAULT 1, " +
                "silent INTEGER DEFAULT 0)";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to init moderation database: " + e.getMessage());
        }
    }

    private void loadAllPunishments() {
        punishmentCache.clear();
        String sql = "SELECT * FROM punishments ORDER BY id ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Punishment p = readPunishment(rs);
                punishmentCache.computeIfAbsent(p.getTargetUUID(), k -> new CopyOnWriteArrayList<>()).add(p);
                if (p.getId() >= nextId) nextId = p.getId() + 1;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load punishments: " + e.getMessage());
        }
    }

    private Punishment readPunishment(ResultSet rs) throws SQLException {
        return new Punishment(
                rs.getInt("id"),
                PunishmentType.valueOf(rs.getString("type")),
                UUID.fromString(rs.getString("target_uuid")),
                rs.getString("target_name"),
                UUID.fromString(rs.getString("issuer_uuid")),
                rs.getString("issuer_name"),
                rs.getString("reason"),
                rs.getLong("date"),
                rs.getLong("expiry"),
                rs.getBoolean("active"),
                rs.getBoolean("silent")
        );
    }

    public Punishment punish(PunishmentType type, UUID targetUUID, String targetName,
                             UUID issuerUUID, String issuerName, String reason,
                             long duration, boolean silent) {
        long now = System.currentTimeMillis();
        long expiry = duration > 0 ? now + duration : 0;

        if (type.isBan()) {
            deactivateActivePunishments(targetUUID, PunishmentType.BAN);
            deactivateActivePunishments(targetUUID, PunishmentType.TEMP_BAN);
            deactivateActivePunishments(targetUUID, PunishmentType.IP_BAN);
        }
        if (type.isMute()) {
            deactivateActivePunishments(targetUUID, PunishmentType.MUTE);
            deactivateActivePunishments(targetUUID, PunishmentType.TEMP_MUTE);
        }

        String sql = "INSERT INTO punishments(type, target_uuid, target_name, issuer_uuid, issuer_name, reason, date, expiry, active, silent) " +
                "VALUES(?,?,?,?,?,?,?,?,1,?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, type.name());
            stmt.setString(2, targetUUID.toString());
            stmt.setString(3, targetName);
            stmt.setString(4, issuerUUID.toString());
            stmt.setString(5, issuerName);
            stmt.setString(6, reason == null ? "" : reason);
            stmt.setLong(7, now);
            stmt.setLong(8, expiry);
            stmt.setBoolean(9, silent);
            stmt.executeUpdate();

            int id = nextId++;
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) id = keys.getInt(1);
            }

            Punishment p = new Punishment(id, type, targetUUID, targetName, issuerUUID, issuerName, reason, now, expiry, true, silent);
            punishmentCache.computeIfAbsent(targetUUID, k -> new CopyOnWriteArrayList<>()).add(p);

            if (type.isBan()) {
                Player target = Bukkit.getPlayer(targetUUID);
                if (target != null && target.isOnline()) {
                    String kickMsg = buildBanMessage(p);
                    Bukkit.getScheduler().runTask(plugin, () -> target.kickPlayer(kickMsg));
                }
            }

            return p;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save punishment: " + e.getMessage());
            return null;
        }
    }

    public void unpunish(UUID targetUUID, PunishmentType type, UUID issuerUUID, String issuerName) {
        List<Punishment> list = punishmentCache.get(targetUUID);
        if (list == null) return;

        for (Punishment p : list) {
            if (p.getType() == type || (type == PunishmentType.BAN && p.getType().isBan())
                    || (type == PunishmentType.MUTE && p.getType().isMute())) {
                if (p.isActive()) {
                    p.setActive(false);
                    String sql = "UPDATE punishments SET active = 0 WHERE id = ?";
                    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, p.getId());
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        plugin.getLogger().severe("Failed to deactivate punishment: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void deactivateActivePunishments(UUID targetUUID, PunishmentType type) {
        List<Punishment> list = punishmentCache.get(targetUUID);
        if (list == null) return;
        for (Punishment p : list) {
            if (p.getType() == type && p.isActive()) {
                p.setActive(false);
            }
        }
    }

    public Punishment getActiveBan(UUID uuid) {
        List<Punishment> list = punishmentCache.get(uuid);
        if (list == null) return null;
        for (Punishment p : list) {
            if (p.getType().isBan() && p.isActive()) return p;
        }
        return null;
    }

    public Punishment getActiveMute(UUID uuid) {
        List<Punishment> list = punishmentCache.get(uuid);
        if (list == null) return null;
        for (Punishment p : list) {
            if (p.getType().isMute() && p.isActive()) return p;
        }
        return null;
    }

    public boolean isBanned(UUID uuid) {
        return getActiveBan(uuid) != null;
    }

    public boolean isMuted(UUID uuid) {
        return getActiveMute(uuid) != null;
    }

    public List<Punishment> getHistory(UUID uuid) {
        return punishmentCache.getOrDefault(uuid, Collections.emptyList());
    }

    public List<Punishment> getWarnings(UUID uuid) {
        List<Punishment> all = punishmentCache.get(uuid);
        if (all == null) return Collections.emptyList();
        return all.stream().filter(p -> p.getType() == PunishmentType.WARN).toList();
    }

    public void clearWarnings(UUID targetUUID, UUID issuerUUID, String issuerName) {
        List<Punishment> warnings = getWarnings(targetUUID);
        for (Punishment w : warnings) {
            if (w.isActive()) {
                w.setActive(false);
                String sql = "UPDATE punishments SET active = 0 WHERE id = ?";
                try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, w.getId());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to clear warning: " + e.getMessage());
                }
            }
        }
    }

    public int getWarningCount(UUID uuid) {
        return (int) getWarnings(uuid).stream().filter(Punishment::isActive).count();
    }

    public String buildBanMessage(Punishment p) {
        String msg = plugin.getMessage("banKickMessage")
                .replace("%reason%", p.getReason().isEmpty() ? "No reason specified" : p.getReason())
                .replace("%staff%", p.getIssuerName());
        if (p.getExpiry() > 0) {
            msg += "\n" + plugin.getMessage("banExpiresIn").replace("%time%", p.getDurationString());
        }
        msg += "\n" + plugin.getMessage("banAppealInfo");
        return TextUtil.translateHexAndAlternateColorCodes(msg);
    }

    public String buildMuteMessage(Punishment p) {
        String msg = plugin.getMessage("muteMessage")
                .replace("%reason%", p.getReason().isEmpty() ? "No reason specified" : p.getReason())
                .replace("%staff%", p.getIssuerName());
        if (p.getExpiry() > 0) {
            msg += " " + plugin.getMessage("muteExpiresIn").replace("%time%", p.getDurationString());
        }
        return TextUtil.translateHexAndAlternateColorCodes(msg);
    }

    public void broadcastPunishment(Punishment p, String targetName) {
        String key = switch (p.getType()) {
            case BAN -> "banBroadcast";
            case TEMP_BAN -> "tempBanBroadcast";
            case IP_BAN -> "ipBanBroadcast";
            case MUTE -> "muteBroadcast";
            case TEMP_MUTE -> "tempMuteBroadcast";
            case WARN -> "warnBroadcast";
            case KICK -> "kickBroadcast";
        };
        String msg = plugin.getMessage(key)
                .replace("%player%", targetName)
                .replace("%staff%", p.getIssuerName())
                .replace("%reason%", p.getReason().isEmpty() ? plugin.getMessage("noReason") : p.getReason());
        if (p.getExpiry() > 0) {
            msg = msg.replace("%time%", p.getDurationString());
        }
        if (p.isSilent()) {
            msg = plugin.getMessage("silentPrefix") + msg;
        }
        String finalMsg = TextUtil.translateHexAndAlternateColorCodes(msg);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (p.isSilent() && !online.hasPermission("eclipse.moderation.notify")) continue;
            online.sendMessage(finalMsg);
        }
        Bukkit.getConsoleSender().sendMessage(finalMsg);
    }

    public void shutdown() {
        punishmentCache.clear();
    }
}
