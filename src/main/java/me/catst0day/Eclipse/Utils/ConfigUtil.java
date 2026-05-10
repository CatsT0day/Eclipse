package me.catst0day.Eclipse.Utils;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Utils.Exceptions.ConfigReloadException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ConfigUtil {

    private final Eclipse plugin;

    public ConfigUtil(Eclipse plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig() throws ConfigReloadException {
        plugin.reloadConfig();

        String lang = plugin.getConfig().getString("lang", "EN").toUpperCase();
        Eclipse.getI().currentLang = lang;

        File langFile = new File(plugin.getDataFolder(), "Translations/" + lang + ".yml");

        if (!langFile.exists()) {
            try {
                plugin.saveResource("Translations/" + lang + ".yml", false);
            } catch (Exception e) {
                throw new ConfigReloadException("Could not save default translation file: " + lang + ".yml", e);
            }
        }

        Eclipse.getI().langConfig = YamlConfiguration.loadConfiguration(langFile);

        for (CAPIConfig cfg : CAPIConfig.values()) {
            String path = "messages." + cfg.key;
            if (!Eclipse.getI().langConfig.contains(path)) {
                Eclipse.getI().langConfig.set(path, cfg.getDefaultValue());
            }
        }

        try {
            Eclipse.getI().langConfig.save(langFile);
        } catch (IOException e) {
            throw new ConfigReloadException("Failed to save " + lang, e);
        }
    }

    public enum CAPIConfig {
        playerOnly("playerOnlyCommand", "╔═════════════════════════════════\n║ #ff6600⚠ &fError! This command is for players only.\n╚═════════════════════════════════"),
        commandError("commandError", "╔═════════════════════════════════\n║ #ff6600✘ &fAn error occurred during execution.\n╚═════════════════════════════════"),
        cooldownMessage("cooldownMessage", "╔═════════════════════════════════\n║ #ff6600⚠ &fCooldown: &e%%&f секунд\n╚═════════════════════════════════"),
        teleportWithDelay("teleportWithDelay", "╔═════════════════════════════════\n║ #ff6600Ⓘ &fTeleporting in &e%seconds%&f sec...\n╚═════════════════════════════════"),
        teleportSuccess("teleportSuccess", "╔═════════════════════════════════\n║ #ff6600✔ &fYou have been teleported!\n╚═════════════════════════════════"),
        teleportCancelled("teleportCancelled", "╔═════════════════════════════════\n║ #ff6600✘ &fTeleport cancelled!\n╚═════════════════════════════════"),
        chunkNotLoaded("chunkNotLoaded", "╔═════════════════════════════════\n║ #ff6600✘ &fError: Chunk not loaded!\n╚═════════════════════════════════"),
        help("help", Arrays.asList(
                "#ff6600=======================",
                "",
                "&fCommand Help",
                "  &6  /heal - heal yourself or another player",
                "  &6  /feed - feed yourself or another player",
                "  &6  /spawn - teleport to spawn",
                "  &6  /tpa <player> - send teleport request",
                "  &6  /tpaccept - accept teleport request",
                "  &6  /tpdeny - deny teleport request",
                "  &6  /warp <name> - teleport to a warp",
                "  &6  /setwarp <name> - create a warp",
                "  &6  /home - home menu or teleport to home",
                "  &6  /sethome <name> - set a home point",
                "  &6  /delhome <name> - delete a home point",
                "  &6  /back - return to previous location",
                "",
                "#ff6600======================="
        )),
        helpUnavailable("helpUnavailable", "╔═════════════════════════════════\n║ #ff6600✘ &fCommand list is unavailable.\n╚═════════════════════════════════"),
        backNoInfo("backNoInfo", "╔═════════════════════════════════\n║ #ff6600✘ &fNo location to return to!\n╚═════════════════════════════════"),
        backWorldBlacklisted("backWorldBlacklisted", "╔═════════════════════════════════\n║ #ff6600✘ &fWorld is blacklisted!\n╚═════════════════════════════════"),
        backFeedback("backFeedback", "╔═════════════════════════════════\n║ #ff6600✔ &fBack: &e[worldName] &7([x], [y], [z])\n╚═════════════════════════════════"),
        backSuccess("backSuccess", "╔═════════════════════════════════\n║ #ff6600✔ &fSuccessfully returned.\n╚═════════════════════════════════"),
        speedCurrent("speedCurrent", "╔═════════════════════════════════\n║ #ff6600Ⓘ &fYour speed: &e%s\n╚═════════════════════════════════"),
        speedInvalid("speedInvalid", "╔═════════════════════════════════\n║ #ff6600✘ &fInvalid value (0-10).\n╚═════════════════════════════════"),
        speedSetWalk("speedSetWalk", "╔═════════════════════════════════\n║ #ff6600✔ &fWalk speed &e%player% &fis now &6%s\n╚═════════════════════════════════"),
        speedSetFly("speedSetFly", "╔═════════════════════════════════\n║ #ff6600✔ &fFly speed &e%player% &fis now &6%s\n╚═════════════════════════════════"),
        currentTime("currentTime", "╔═════════════════════════════════\n║ #ff6600Ⓘ &fTime for &e%player%&f: &6%time%\n╚═════════════════════════════════"),
        timeFrozen("timeFrozen", "╔═════════════════════════════════\n║ #ff6600✔ &fTime frozen for &e%player%&f.\n╚═════════════════════════════════"),
        timeUnfrozen("timeUnfrozen", "╔═════════════════════════════════\n║ #ff6600✔ &fTime unfrozen for &e%player%&f.\n╚═════════════════════════════════"),
        timeReset("timeReset", "╔═════════════════════════════════\n║ #ff6600✔ &fTime reset for &e%player%&f.\n╚═════════════════════════════════"),
        timeSet("timeSet", "╔═════════════════════════════════\n║ #ff6600✔ &fTime for &e%player%&f set to: &6%time%\n╚═════════════════════════════════"),
        yourTimeReset("yourTimeReset", "╔═════════════════════════════════\n║ #ff6600✔ &fYour time reset to server time.\n╚═════════════════════════════════"),
        yourTimeSet("yourTimeSet", "╔═════════════════════════════════\n║ #ff6600✔ &fYour time set to &6%time%\n╚═════════════════════════════════"),
        enabled("enabled", "&aenabled"),
        disabled("disabled", "&cdisabled"),
        afkSuccess("afkSuccess", "╔═════════════════════════════════\n║ #ff6600✔ &fYou are now AFK.\n╚═════════════════════════════════"),
        afkFail("afkFail", "╔═════════════════════════════════\n║ #ff6600✘ &fFailed to enter AFK mode.\n╚═════════════════════════════════"),
        afkReason("afkReason", "╔═════════════════════════════════\n║ #ff6600✘ &fAFK Reason: &e%reason%\n╚═════════════════════════════════"),
        afkCheck("afkCheck", "╔═════════════════════════════════\n║ #ff6600✔ &fAFK Status: &e%is_afk%\n╚═════════════════════════════════"),
        currentWeather("currentWeather", "╔═════════════════════════════════\n║ #ff6600Ⓘ &fWeather for &e%player%&f: &6%weather%\n╚═════════════════════════════════"),
        weatherReset("weatherReset", "╔═════════════════════════════════\n║ #ff6600✔ &fWeather reset for &e%player%&f.\n╚═════════════════════════════════"),
        yourWeatherReset("yourWeatherReset", "╔═════════════════════════════════\n║ #ff6600✔ &fYour weather has been reset.\n╚═════════════════════════════════"),
        weatherSet("weatherSet", "╔═════════════════════════════════\n║ #ff6600✔ &fWeather for &e%player%&f: &6%weather%\n╚═════════════════════════════════"),
        yourWeatherSet("yourWeatherSet", "╔═════════════════════════════════\n║ #ff6600✔ &fYour weather set to: &6%weather%\n╚═════════════════════════════════"),
        homeTeleported("homeTeleported", "╔═════════════════════════════════\n║ #ff6600✔ &fHome: &e{homename}\n╚═════════════════════════════════"),
        homeCreatedSuccessfully("homeCreatedSuccessfully", "╔═════════════════════════════════\n║ #ff6600✔ &fHome &e\"{homename}\" &fcreated!\n╚═════════════════════════════════"),
        homeAlreadyExists("homeAlreadyExists", "╔═════════════════════════════════\n║ #ff6600✘ &fHome &e\"{homename}\" &falready exists.\n╚═════════════════════════════════"),
        homeNotFound("homeNotFound", "╔═════════════════════════════════\n║ #ff6600✘ &fHome &e\"{homename}\" &fnot found.\n╚═════════════════════════════════"),
        homeDeletionSuccess("homeDeletionSuccess", "╔═════════════════════════════════\n║ #ffff00⚝ &fHome &e\"{homename}\" &fdeleted.\n╚═════════════════════════════════"),
        homeSet("homeSet", "╔═════════════════════════════════\n║ #ffff00⚝ &fHome &e%homename% &fset!\n╚═════════════════════════════════"),
        suicideMessage("suicideMessage", "╔═════════════════════════════════\n║ #ff6600✝ &fYou chose to... disappear.\n╚═════════════════════════════════"),
        suicideSuccess("suicideSuccess", "╔═════════════════════════════════\n║ #ff6600✝ &fPlayer &e%s &fleft this world.\n╚═════════════════════════════════"),
        noPermission("noPermission", "╔═════════════════════════════════\n║ #ff6600✘ &fYou &cdo not have permission&f!\n╚═════════════════════════════════"),
        playerNotFound("playerNotFound", "╔═════════════════════════════════\n║ #ff6600✘ &fPlayer not found.\n╚═════════════════════════════════"),
        warpTeleported("warpTeleported", "╔═════════════════════════════════\n║ #ff6600✔ &fWarp: &e{warpname}\n╚═════════════════════════════════"),
        warpCreatedSuccessfully("warpCreatedSuccessfully", "╔═════════════════════════════════\n║ #ff6600✔ &fWarp &e\"{warpname}\" &fcreated!\n╚═════════════════════════════════"),
        warpNotFound("warpNotFound", "╔═════════════════════════════════\n║ #ff6600✘ &fWarp &e\"{warpname}\" &fnot found.\n╚═════════════════════════════════"),
        spawnSet("spawnSet", "╔═════════════════════════════════\n║ #ff6600✔ &fSpawn successfully &6set\n╚═════════════════════════════════"),
        spawnTeleportSuccess("spawnTeleportSuccess", "╔═════════════════════════════════\n║ #ff6600✔ &fTeleporting to &6spawn...\n╚═════════════════════════════════"),
        repairSuccessAll("repairSuccessAll", "╔═════════════════════════════════\n║ #ff6600✔ &fAll items repaired!\n╚═════════════════════════════════"),
        repairSuccessHand("repairSuccessHand", "╔═════════════════════════════════\n║ #ff6600✔ &fItem in hand repaired.\n╚═════════════════════════════════"),
        tpaSent("tpaSent", "╔═════════════════════════════════\n║ #ff6600Ⓘ &fRequest sent to &e%player%&f!\n╚═════════════════════════════════"),
        tpaReceived("tpaReceived", "╔═════════════════════════════════\n║ #ff6600Ⓘ &ePlayer &f%player% &ewants to TP!\n╚═════════════════════════════════"),
        tpSuccess("tpSuccess", "╔═════════════════════════════════\n║ #ff6600✔ &fTeleported to &e%s&f!\n╚═════════════════════════════════"),
        tpaDenied("tpaDenied", "╔═════════════════════════════════\n║ #ff6600✘ &fPlayer &e%player% &fdenied request.\n╚═════════════════════════════════"),
        gmSuccess("gmSuccess", "╔═════════════════════════════════\n║ #ff6600✔ &fGamemode set to &e%s&a.\n╚═════════════════════════════════"),
        godToggled("godToggled", "╔═════════════════════════════════\n║ #ff6600✔ &fGod mode %s.\n╚═════════════════════════════════"),
        flyToggled("flyToggled", "╔═════════════════════════════════\n║ #ff6600✔ &fFly mode %s.\n╚═════════════════════════════════"),
        healSuccess("healSuccess", "╔═════════════════════════════════\n║ #ff6600❤ &fYou have been healed.\n╚═════════════════════════════════"),
        healTargeted("healTargeted", "╔═════════════════════════════════\n║ #ff6600❤ &fYou healed &e%s\n╚═════════════════════════════════"),
        feedSuccess("feedSuccess", "╔═════════════════════════════════\n║ #ff6600☕ &fHunger satisfied.\n╚═════════════════════════════════"),
        feedTargeted("feedTargeted", "╔═════════════════════════════════\n║ #ff6600☕ &fYou fed &e%s\n╚═════════════════════════════════"),
        vanishEnabled("vanishEnabled", "╔═════════════════════════════════\n║ #ff6600✔ &fVanish &6enabled&f.\n╚═════════════════════════════════"),
        vanishDisabled("vanishDisabled", "╔═════════════════════════════════\n║ #ff6600✘ &fVanish &6disabled&f.\n╚═════════════════════════════════"),
        gmSurvival("gamemodes.SURVIVAL", "╔═════════════════════════════════\n║ #ff6600⚡ &fGame mode set to Survival.\n╚═════════════════════════════════"),
        gmCreative("gamemodes.CREATIVE", "╔═════════════════════════════════\n║ #ff6600⚡ &fGame mode set to Creative.\n╚═════════════════════════════════"),
        gmAdventure("gamemodes.ADVENTURE", "╔═════════════════════════════════\n║ #ff6600⚡ &fGame mode set to Adventure.\n╚═════════════════════════════════"),
        gmSpectator("gamemodes.SPECTATOR", "╔═════════════════════════════════\n║ #ff6600⚡ &fGame mode set to Spectator.\n╚═════════════════════════════════");

        public final String key;
        private final Object defaultValue;

        CAPIConfig(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }
}