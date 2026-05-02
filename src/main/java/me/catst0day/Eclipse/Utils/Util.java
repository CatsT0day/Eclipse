package me.catst0day.Eclipse.Utils;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Util {
    private static final String PREFIX = TextUtil.YELLOW + "[EclipseAPI] ";
    private static final String PURPLE = "#3B1757";

    public static void printStartupBanner(JavaPlugin plugin) {
        String version = (plugin != null) ? plugin.getDescription().getVersion() : "Unknown";
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        String[] banner = {
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
        "",
                "&b╔══════════════════════════════════════╗",
                "&b║                                      ║",
                "&b║  Eclipse &bv" + version + "  succefully loaded     ║",
                "&b║                                      ║",
                "&b╚══════════════════════════════════════╝",
                "",
                "&bVersion: &f" + version,
                "&bAuthor: &fCatsT0day (aka DreamersTygydykk)",
                ""
        };

        for (String line : banner) {
            console.sendMessage(PREFIX + TextUtil.translateHexAndAlternateColorCodes(line));
        }
    }

    public static void log(String text) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        boolean monochrome = Eclipse.getI().getConfig().getBoolean("monochromeMode", false);

        if (monochrome) {
            console.sendMessage(PREFIX + TextUtil.stripColor(TextUtil.translateHexAndAlternateColorCodes(text)));
        } else {
            console.sendMessage(PREFIX + TextUtil.translateHexAndAlternateColorCodes("#3B1757" + text));
        }
    }

    public static void loadWithMessage(Object count, String msg, long time) {
        log("Loaded (&f" + count + "#3B1757" + ") &7" + msg + "#3B1757into cache. &6Took &e" + time + "&6ms");
    }

    public static String color(String message) {
        return TextUtil.translateHexAndAlternateColorCodes(message);
    }
}