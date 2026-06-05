package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Auction.AuctionListing;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class EclipseAuctionManager {
    private final Eclipse plugin;
    private final File dbFile;
    private final long listingDurationMillis;
    private final int maxListingsPerPlayer;
    private final double taxRate;

    public EclipseAuctionManager(Eclipse plugin) {
        this.plugin = plugin;
        this.dbFile = new File(plugin.getDataFolder(), "Eclipse.auction.db");
        this.listingDurationMillis = plugin.getConfig().getLong("auction.listingDurationDays", 7) * 24L * 60L * 60L * 1000L;
        this.maxListingsPerPlayer = plugin.getConfig().getInt("auction.maxListingsPerPlayer", 10);
        this.taxRate = plugin.getConfig().getDouble("auction.taxRate", 0.05);
        initTable();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
    }

    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS auction_listings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "seller TEXT NOT NULL, " +
                "item_data BLOB NOT NULL, " +
                "price REAL NOT NULL, " +
                "created_at BIGINT NOT NULL, " +
                "expires_at BIGINT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "sold BOOLEAN DEFAULT 0);";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize auction table", e);
        }
    }

    private byte[] serializeItem(ItemStack item) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(bos)) {
            oos.writeObject(item);
            return bos.toByteArray();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to serialize item", e);
            return null;
        }
    }

    private ItemStack deserializeItem(byte[] data) {
        if (data == null) return null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             BukkitObjectInputStream ois = new BukkitObjectInputStream(bis)) {
            return (ItemStack) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to deserialize item", e);
            return null;
        }
    }

    private AuctionListing fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        UUID seller = UUID.fromString(rs.getString("seller"));
        ItemStack item = deserializeItem(rs.getBytes("item_data"));
        double price = rs.getDouble("price");
        long createdAt = rs.getLong("created_at");
        long expiresAt = rs.getLong("expires_at");
        String category = rs.getString("category");
        boolean sold = rs.getBoolean("sold");
        return new AuctionListing(id, seller, item, price, createdAt, expiresAt, category, sold);
    }

    public int createListing(UUID seller, ItemStack item, double price, String category) {
        String sql = "INSERT INTO auction_listings(seller, item_data, price, created_at, expires_at, category, sold) VALUES(?,?,?,?,?,?,?)";
        long now = System.currentTimeMillis();
        long expiresAt = now + listingDurationMillis;

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, seller.toString());
            pstmt.setBytes(2, serializeItem(item));
            pstmt.setDouble(3, price);
            pstmt.setLong(4, now);
            pstmt.setLong(5, expiresAt);
            pstmt.setString(6, category);
            pstmt.setBoolean(7, false);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create auction listing", e);
        }
        return -1;
    }

    public AuctionListing getListing(int id) {
        String sql = "SELECT * FROM auction_listings WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get auction listing", e);
        }
        return null;
    }

    public List<AuctionListing> getAllListings() {
        List<AuctionListing> listings = new ArrayList<>();
        String sql = "SELECT * FROM auction_listings WHERE sold = 0 ORDER BY created_at DESC";
        
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                listings.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get auction listings", e);
        }
        return listings;
    }

    public List<AuctionListing> getPlayerListings(UUID seller) {
        List<AuctionListing> listings = new ArrayList<>();
        String sql = "SELECT * FROM auction_listings WHERE seller = ? ORDER BY created_at DESC";
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, seller.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listings.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player auction listings", e);
        }
        return listings;
    }

    public List<AuctionListing> getListingsByCategory(String category) {
        List<AuctionListing> listings = new ArrayList<>();
        String sql = "SELECT * FROM auction_listings WHERE category = ? AND sold = 0 ORDER BY created_at DESC";
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listings.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get auction listings by category", e);
        }
        return listings;
    }

    public boolean buyListing(int listingId, UUID buyer) {
        AuctionListing listing = getListing(listingId);
        if (listing == null || listing.isSold() || listing.isExpired()) {
            return false;
        }

        String sql = "UPDATE auction_listings SET sold = 1 WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            
            pstmt.setInt(1, listingId);
            pstmt.executeUpdate();
            
            // Transfer money
            double tax = listing.getPrice() * taxRate;
            double sellerAmount = listing.getPrice() - tax;
            
            plugin.getEconomyManager().removeBalance(buyer, listing.getPrice());
            plugin.getEconomyManager().addBalance(listing.getSeller(), sellerAmount);
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to buy auction listing", e);
            return false;
        }
    }

    public boolean cancelListing(int listingId, UUID seller) {
        AuctionListing listing = getListing(listingId);
        if (listing == null || !listing.getSeller().equals(seller) || listing.isSold()) {
            return false;
        }

        String sql = "DELETE FROM auction_listings WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, listingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to cancel auction listing", e);
            return false;
        }
    }

    public int getPlayerListingCount(UUID seller) {
        String sql = "SELECT COUNT(*) FROM auction_listings WHERE seller = ? AND sold = 0";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, seller.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player listing count", e);
        }
        return 0;
    }

    public void cleanupExpiredListings() {
        String sql = "SELECT * FROM auction_listings WHERE sold = 0 AND expires_at < ?";
        List<AuctionListing> expiredListings = new ArrayList<>();
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expiredListings.add(fromResultSet(rs));
                }
            }
            
            // Return items to sellers
            for (AuctionListing listing : expiredListings) {
                OfflinePlayer seller = Bukkit.getOfflinePlayer(listing.getSeller());
                if (seller.isOnline()) {
                    seller.getPlayer().getInventory().addItem(listing.getItem());
                } else {
                    // Could implement offline item return system
                }
            }
            
            // Delete expired listings
            String deleteSql = "DELETE FROM auction_listings WHERE sold = 0 AND expires_at < ?";
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setLong(1, System.currentTimeMillis());
                int deleted = deletePstmt.executeUpdate();
                if (deleted > 0) {
                    plugin.getLogger().info("Cleaned up " + deleted + " expired auction listings");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to cleanup expired listings", e);
        }
    }

    public String getCategoryForItem(ItemStack item) {
        Material material = item.getType();
        String name = material.name().toLowerCase();
        
        // Weapons
        if (name.contains("sword") || name.contains("axe") || name.contains("bow") || name.contains("crossbow") || name.contains("trident") || name.contains("spear")) {
            return "weapons";
        }
        
        // Armor
        if (name.contains("helmet") || name.contains("chestplate") || name.contains("leggings") || name.contains("boots") || name.contains("shield")) {
            return "armor";
        }
        
        // Tools
        if (name.contains("pickaxe") || name.contains("axe") || name.contains("shovel") || name.contains("hoe") || name.contains("shears")) {
            return "tools";
        }
        
        if (material.isBlock()) return "blocks";
        if (material.isEdible()) return "food";
        if (material.isRecord()) return "music";
        return "misc";
    }
}
