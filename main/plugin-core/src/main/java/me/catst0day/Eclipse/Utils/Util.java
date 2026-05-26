package me.catst0day.Eclipse.Utils;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Util {
    private static final String PREFIX = TextUtil.YELLOW + "[EclipseAPI] ";
    private static final String PURPLE_COLOR = "#3B1757";
    private static final String[] STARTUP_BANNER = {
            "",
            "░██████████            ░██ ░██",
            "░██                    ░██",
            "░██          ░███████  ░██ ░██░████████   ░███████   ░███████ ",
            "░█████████  ░██    ░██ ░██ ░██░██    ░██ ░██        ░██    ░██",
            "░██         ░██        ░██ ░██░██    ░██  ░███████  ░█████████",
            " ██         ░██    ░██ ░██ ░██░███   ░██        ░██ ░██       ",
            "░██████████  ░███████  ░██ ░██░██░█████   ░███████   ░███████ ",
            "                              ░██                             ",
            "                              ░██                             ",
            ""
    };

    public static void printStartupBanner(JavaPlugin plugin) {
        String version = (plugin != null) ? plugin.getDescription().getVersion() : "Unknown";
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        String[] banner = new String[STARTUP_BANNER.length + 8];
        System.arraycopy(STARTUP_BANNER, 0, banner, 0, STARTUP_BANNER.length);

        banner[STARTUP_BANNER.length] = "&b╔══════════════════════════════════════╗";
        banner[STARTUP_BANNER.length + 1] = "&b║                                      ║";
        banner[STARTUP_BANNER.length + 2] = "&b║  Eclipse &bv" + version + "  succefully loaded     ║";
        banner[STARTUP_BANNER.length + 3] = "&b║                                      ║";
        banner[STARTUP_BANNER.length + 4] = "&b╚══════════════════════════════════════╝";
        banner[STARTUP_BANNER.length + 5] = "";
        banner[STARTUP_BANNER.length + 6] = "&bVersion: &f" + version;
        banner[STARTUP_BANNER.length + 7] = "&bAuthor: &fCatsT0day (aka. DreamersTygydykk)";

        for (String line : banner) {
            if (line != null) {
                console.sendMessage(PREFIX + TextUtil.translateHexAndAlternateColorCodes(line));
            }
        }
    }


    public static void log(String text) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        boolean monochrome = Eclipse.getI().getConfig().getBoolean("monochromeMode", false);

        if (monochrome) {
            console.sendMessage(TextUtil.deColorize(PREFIX) + TextUtil.stripColor(TextUtil.translateHexAndAlternateColorCodes(text)));
        } else {
            console.sendMessage(PREFIX + TextUtil.translateHexAndAlternateColorCodes(PURPLE_COLOR + text));
        }
    }

    public static void loadWithMessage(Object count, String msg, long time) {
        log("Loaded (&f" + count + PURPLE_COLOR + ") &7" + msg + PURPLE_COLOR + "into cache. &6Took &e" + time + "&6ms");
    }

    public static String color(String message) {
        return TextUtil.translateHexAndAlternateColorCodes(message);
    }
}