package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Mail.MailTemplate;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EclipseMailManager {
    private final Eclipse plugin;
    private final File dbFile;
    private final long mailExpirationDays;

    public EclipseMailManager(Eclipse plugin) {
        this.plugin = plugin;
        this.dbFile = new File(plugin.getDataFolder(), "Eclipse.mail.db");
        this.mailExpirationDays = plugin.getConfig().getLong("mail.expirationDays", 30);
        initTable();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
    }

    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS mail (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender TEXT NOT NULL, " +
                "recipient TEXT NOT NULL, " +
                "message TEXT NOT NULL, " +
                "timestamp BIGINT NOT NULL, " +
                "read BOOLEAN DEFAULT 0);";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize mail table: " + e.getMessage());
        }
    }

    public int sendMail(UUID sender, UUID recipient, String message) {
        String sql = "INSERT INTO mail(sender, recipient, message, timestamp, read) VALUES(?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, sender.toString());
            pstmt.setString(2, recipient.toString());
            pstmt.setString(3, message);
            pstmt.setLong(4, System.currentTimeMillis());
            pstmt.setBoolean(5, false);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to send mail: " + e.getMessage());
        }
        return -1;
    }

    public List<MailTemplate> getPlayerMail(UUID recipient) {
        List<MailTemplate> mailList = new ArrayList<>();
        String sql = "SELECT * FROM mail WHERE recipient = ? ORDER BY timestamp DESC";
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipient.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    UUID sender = UUID.fromString(rs.getString("sender"));
                    String message = rs.getString("message");
                    long timestamp = rs.getLong("timestamp");
                    boolean read = rs.getBoolean("read");
                    mailList.add(new MailTemplate(id, sender, recipient, message, timestamp, read));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load player mail: " + e.getMessage());
        }
        return mailList;
    }

    public List<MailTemplate> getUnreadMail(UUID recipient) {
        List<MailTemplate> mailList = new ArrayList<>();
        String sql = "SELECT * FROM mail WHERE recipient = ? AND read = 0 ORDER BY timestamp DESC";
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipient.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    UUID sender = UUID.fromString(rs.getString("sender"));
                    String message = rs.getString("message");
                    long timestamp = rs.getLong("timestamp");
                    boolean read = rs.getBoolean("read");
                    mailList.add(new MailTemplate(id, sender, recipient, message, timestamp, read));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load unread mail: " + e.getMessage());
        }
        return mailList;
    }

    public boolean markAsRead(int mailId) {
        String sql = "UPDATE mail SET read = 1 WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mailId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to mark mail as read: " + e.getMessage());
        }
        return false;
    }

    public boolean markAllAsRead(UUID recipient) {
        String sql = "UPDATE mail SET read = 1 WHERE recipient = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipient.toString());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to mark all mail as read: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteMail(int mailId) {
        String sql = "DELETE FROM mail WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mailId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to delete mail: " + e.getMessage());
        }
        return false;
    }

    public boolean clearAllMail(UUID recipient) {
        String sql = "DELETE FROM mail WHERE recipient = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipient.toString());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to clear mail: " + e.getMessage());
        }
        return false;
    }

    public int getUnreadCount(UUID recipient) {
        String sql = "SELECT COUNT(*) FROM mail WHERE recipient = ? AND read = 0";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipient.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get unread count: " + e.getMessage());
        }
        return 0;
    }

    public void cleanupExpiredMail() {
        long expirationTime = System.currentTimeMillis() - (mailExpirationDays * 24L * 60L * 60L * 1000L);
        String sql = "DELETE FROM mail WHERE timestamp < ?";
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, expirationTime);
            int deleted = pstmt.executeUpdate();
            if (deleted > 0) {
                plugin.getLogger().info("Cleaned up " + deleted + " expired mail messages");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to cleanup expired mail: " + e.getMessage());
        }
    }
}
