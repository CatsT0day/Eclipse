package me.catst0day.Eclipse.NMS;

import org.bukkit.Bukkit;


public final class NMSVersion {

    private NMSVersion() {}

    
    public static String getNmsVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    
    public static String getServerVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    
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

    
    public static int getMajorVersion() {
        try {
            String version = getServerVersion();
            String[] parts = version.split("\\.");
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return 1;
        }
    }

    
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
