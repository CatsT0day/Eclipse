package me.catst0day.Eclipse.Managers.Database;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EclipseSQLiteManager implements EclipseSQL {
    private final File dbFile;

    public EclipseSQLiteManager(File folder, String fileName) {
        if (!folder.exists()) folder.mkdirs();
        this.dbFile = new File(folder, fileName);
        initTable();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
    }

    private void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS locations (" +
                "name TEXT PRIMARY KEY, " +
                "world TEXT, x REAL, y REAL, z REAL, yaw REAL, pitch REAL);";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new EclipseSQLException("Table init catched and exception exception", e);
        }
    }

    @Override
    public boolean saveLocation(String name, Location loc) {
        String sql = "REPLACE INTO locations(name, world, x, y, z, yaw, pitch) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, loc.getWorld().getName());
            pstmt.setDouble(3, loc.getX());
            pstmt.setDouble(4, loc.getY());
            pstmt.setDouble(5, loc.getZ());
            pstmt.setFloat(6, loc.getYaw());
            pstmt.setFloat(7, loc.getPitch());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new EclipseSQLException("Location save catched and exception: " + name, e);
        }
        return true;
    }

    @Override
    public Location loadLocation(String name) {
        String sql = "SELECT * FROM locations WHERE name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    World world = Bukkit.getWorld(rs.getString("world"));
                    if (world == null) return null;
                    return new Location(world, rs.getDouble("x"), rs.getDouble("y"),
                            rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
                }
            }
        } catch (SQLException e) {
            throw new EclipseSQLException("Location load catched and exception: " + name, e);
        }
        return null;
    }

    @Override
    public boolean delete(String name) {
        String sql = "DELETE FROM locations WHERE name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new EclipseSQLException("Location deletetion catched and exception: " + name, e);
        }
    }

    @Override
    public List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM locations";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new EclipseSQLException("Location getting catched and exception", e);
        }
        return names;
    }
}