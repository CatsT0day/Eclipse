package me.catst0day.Eclipse.NMS;

import org.bukkit.Bukkit;

/**
 * Utility class for NMS version detection and handling.
 */
public final class NMSVersion {

    private NMSVersion() {}

    /**
     * Gets the NMS version string (e.g., "v1_21_R1").
     */
    public static String getNmsVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Gets the full server version (e.g., "1.21.1").
     */
    public static String getServerVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    /**
     * Checks if the server is running a modern version (1.19+).
     */
    public static boolean isModern() {
        try {
            String version = getServerVersion();
            String[] parts = version.split("\\.");
            if (parts.length >= 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                return major > 1 || (major == 1 && minor >= 19);
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    /**
     * Gets the major version (e.g., 1 for 1.21.1).
     */
    public static int getMajorVersion() {
        try {
            String version = getServerVersion();
            String[] parts = version.split("\\.");
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Gets the minor version (e.g., 21 for 1.21.1).
     */
    public static int getMinorVersion() {
        try {
            String version = getServerVersion();
            String[] parts = version.split("\\.");
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }
}
