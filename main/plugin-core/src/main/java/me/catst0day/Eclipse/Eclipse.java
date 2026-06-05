package me.catst0day.Eclipse;

import me.catst0day.Eclipse.Bossbar.EclipseBarColor;
import me.catst0day.Eclipse.Bossbar.EclipseBarStyle;
import me.catst0day.Eclipse.Bossbar.EclipseBossBar;
import me.catst0day.Eclipse.EventListeners.*;
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Managers.*;
import me.catst0day.Eclipse.Managers.EclipseMailManager;
import me.catst0day.Eclipse.Managers.EclipseAuctionManager;
import me.catst0day.Eclipse.Economy.VaultEconomy;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import me.catst0day.Eclipse.Utils.Util;
import me.catst0day.Eclipse.Utils.VersionChecker;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Entity.Player.GuiListener;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Utils.Schedulers.EclipseScheduler;
import me.catst0day.Eclipse.Managers.Database.EclipseSQLiteManager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static me.catst0day.Eclipse.Utils.Util.log;
import static org.bukkit.Bukkit.getCommandMap;

public class Eclipse extends JavaPlugin {
    private static Eclipse instance;
    private final Map<UUID, Boolean> godMode = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> flyMode = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> tpaRequests = new ConcurrentHashMap<>();
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();
    public static boolean fullyLoaded = false;
    public static YamlConfiguration langConfig;
    public static String currentLang;

    private EclipseHomeManager homeManager;
    private EclipseWarpManager warpManager;
    private EclipsePermissionManager permManager;
    private EclipseAliasManager aliasManager;
    private EclipseEconomyManager economyManager;
    private EclipseChatManager chatManager;
    private EclipseModuleManager moduleManager;
    private EclipseHologramManager hologramManager;
    private EclipseKitManager kitManager;
    private VersionChecker versionCheckManager;
    private EclipseSQLiteManager SQLiteManager;
    private me.catst0day.Eclipse.Holograms.Animations.AnimationManager animationManager;
    private EclipseMailManager mailManager;
    private EclipseAuctionManager auctionManager;


    @Override
    public void onEnable() {
        instance = this;
        Util.printStartupBanner(this);
        saveDefaultConfig();
        reloadConfig();
        loadTranslations();
        this.homeManager = new EclipseHomeManager(this);
        this.warpManager = new EclipseWarpManager(this);
        this.economyManager = new EclipseEconomyManager(this);
        this.chatManager = new EclipseChatManager(this);
        this.moduleManager = new EclipseModuleManager(this);
        this.hologramManager = new EclipseHologramManager(this);
        this.animationManager = new me.catst0day.Eclipse.Holograms.Animations.AnimationManager(this);
        this.mailManager = new EclipseMailManager(this);
        this.auctionManager = new EclipseAuctionManager(this);

        setupVaultEconomy();

        register();
        setup();
        registerEvents();
        getVersionCheckManager().checkForUpdates();

        fullyLoaded = true;
    }
    
    private void setupVaultEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager()
                    .getRegistration(Economy.class);
            
            if (rsp == null) {
                VaultEconomy vaultEconomy = new VaultEconomy(this, this.economyManager);
                Bukkit.getServicesManager().register(Economy.class, vaultEconomy, this, ServicePriority.Highest);
                getLogger().info("Vault economy service registered.");
            } else {
                getLogger().info("Vault economy already provided by " + rsp.getPlugin().getName() + ". Using existing economy.");
            }
        }
    }

    @Override
    public void onDisable() {
        
        Bukkit.getScheduler().cancelTasks(this);
        bossBars.values().forEach(BossBar::removeAll);
        bossBars.clear();
        if (economyManager != null) {
            economyManager.shutdown();
        }
        if (hologramManager != null) {
            hologramManager.shutdown();
        }
        if (kitManager != null) {
            kitManager.shutdown();
        }
        if (animationManager != null) {
            animationManager.shutdown();
        }
    }

    public static Eclipse getI() {
        return instance == null ? JavaPlugin.getPlugin(Eclipse.class) : instance;
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        if (getConfig().getBoolean("DisableAchievements")) {
            pm.registerEvents((Listener) new EclipseHideAchievements(), this);
        }
        pm.registerEvents(new EclipseOnPlayerJoinEvent(), this);
        pm.registerEvents(new EclipseOnEntityDamageByEntityEvent(), this);
        pm.registerEvents(new EclipseOnPlayerRespawnEvent(), this);
        pm.registerEvents(new GuiListener(), this);
        pm.registerEvents(new EclipseOnEntityDamageEvent(this), this);
        pm.registerEvents(new EclipseOnItemPickupEvent(this), this);
        pm.registerEvents(new EclipseChatListener(this), this);
        pm.registerEvents(new EclipseHologramListener(this), this);
        pm.registerEvents(new EclipseOnPlayerAsyncChatHologramEvent(this), this);
        pm.registerEvents(new EclipseHologramClickListener(this), this);
        pm.registerEvents(new EclipseMailListener(this), this);
        pm.registerEvents(new EclipseAuctionListener(this), this);
    }

    public void loadTranslations() {
        currentLang = getConfig().getString("lang", "EN").toUpperCase();
        File langFile = new File(getDataFolder(), "Translations/" + currentLang + ".yml");

        if (!langFile.exists()) {
            saveResource("Translations/" + currentLang + ".yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        log("Loaded translation: " + langFile.getName());
    }

    public String getMessage(String key) {
        if (langConfig == null) return "§cLang not loaded";
        String raw = langConfig.getString("messages." + key);
        if (raw == null) {
            getLogger().warning("Missing translation key: " + key);
            return "Msg '" + key + "' missing";
        }
        return TextUtil.translateHexAndAlternateColorCodes(raw);
    }

    public String getGameModeMessage(String key) {
        if (langConfig == null) return "§cLang not loaded";
        String raw = langConfig.getString("messages.gamemodes." + key);
        if (raw == null) return getMessage(key);
        return TextUtil.translateHexAndAlternateColorCodes(raw);
    }

    public void sendCFGmessage(CommandSender sender, String key) {
        String msg = getMessage(key);
        if (sender instanceof Player player) {
            getPlayer(player).sendMsg(msg);
            return;
        }
        sender.sendMessage(msg);
    }

    private void register() {
        String packageName = "me.catst0day.Eclipse.Commands.list";
        long startTime = System.currentTimeMillis();
        String path = packageName.replace('.', '/');
        URL packageURL = getClassLoader().getResource(path);

        if (packageURL == null) {
            log("&cPackage " + packageName + " not found!");
            return;
        }

        int count = 0;
        try {
            if ("jar".equals(packageURL.getProtocol())) {
                JarURLConnection connection = (JarURLConnection) packageURL.openConnection();
                try (JarFile jarFile = connection.getJarFile()) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(path) && name.endsWith(".class") && !name.contains("$")) {
                            String className = name.substring(0, name.length() - 6).replace('/', '.');
                            if (attemptRegister(className)) {
                                count++;
                            }
                        }
                    }
                }
            }
            long endTime = System.currentTimeMillis() - startTime;
            Util.loadWithMessage(count, "commands ", endTime);

        } catch (Exception e) {
            log("&cerror scanning package: " + e.getMessage());
        }
    }

    private boolean attemptRegister(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (CommandTemplate.class.isAssignableFrom(clazz) &&
                    !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {

                CommandTemplate cmd = (CommandTemplate) clazz.getConstructor(Eclipse.class).newInstance(this);

                SimpleCommandMap commandMap = (SimpleCommandMap) getCommandMap();
                Command bukkitCmd = createBukkitCmd(cmd.getName(), cmd);
                commandMap.register(getName(), bukkitCmd);
                CommandTemplate.getRegisteredCommands().put(cmd.getName(), cmd);
                if (cmd.getAliases() != null) {
                    for (String alias : cmd.getAliases()) {
                        commandMap.register(getName(), createBukkitCmd(alias, cmd));
                        CommandTemplate.getRegisteredCommands().put(alias, cmd);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            log("&cError registering " + className + ": " + e.getMessage());
        }
        return false;
    }

    private Command createBukkitCmd(String name, CommandTemplate template) {
        return new Command(name) {
            @Override public boolean execute(@NotNull CommandSender s, @NotNull String l, @NotNull String[] a) { return template.onCommand(s, this, l, a); }
            @Override public @NotNull List<String> tabComplete(@NotNull CommandSender s, @NotNull String al, @NotNull String[] a) {
                List<String> result = template.onTabComplete(s, this, al, a);
                return result != null ? result : Collections.emptyList();
            }
        };
    }

    private void setup() {
        PluginCommand main = getCommand("eclipse");
        if (main == null) return;

        main.setExecutor((sender, command, label, args) -> {
            if (args.length == 0) {
                sender.sendMessage("=== Commands ===");
                CommandTemplate.getRegisteredCommands().forEach((name, exec) -> {
                    if (exec instanceof CommandTemplate cmd)
                        sender.sendMessage("/eclipse " + name + " - " + cmd.getDescription());
                });
                return true;
            }

            String sub = args[0].toLowerCase();
            CommandExecutor exec = CommandTemplate.getRegisteredCommands().get(sub);
            if (exec == null) {
                sender.sendMessage(getMessage("unknownCommand").replace("%command%", sub));
                return true;
            }
            return exec.onCommand(sender, command, sub, Arrays.copyOfRange(args, 1, args.length));
        });

        main.setTabCompleter((sender, command, alias, args) -> {
            if (args.length == 1) {
                return CommandTemplate.getRegisteredCommands().keySet().stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .toList();
            }
            CommandExecutor exec = CommandTemplate.getRegisteredCommands().get(args[0].toLowerCase());
            if (exec instanceof CommandTemplate cmd) {
                return cmd.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
            }
            return Collections.emptyList();
        });
    }

    public void teleport(Player player, Location target) {
        if (target == null || target.getWorld() == null) {
            player.sendMessage(getMessage("invalidLocation"));
            return;
        }

        int delaySeconds = 7;
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            String permission = perm.getPermission();
            if (permission.startsWith("eclipse.teleport.delay.")) {
                String delayStr = permission.substring(22);
                if (delayStr.matches("\\d+")) {
                    try {
                        delaySeconds = Integer.parseInt(delayStr);
                        break;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        if (delaySeconds <= 0) {
            player.teleport(target);
            player.sendMessage(getMessage("teleportSuccess"));
            return;
        }

        EclipseBossBar barInfo = new EclipseBossBar(this, player, "teleport_delay");
        barInfo.setColor(EclipseBarColor.BLUE);
        barInfo.setStyle(EclipseBarStyle.SOLID);
        barInfo.setSeconds(delaySeconds);
        bossBars.put(player.getUniqueId(), barInfo.getBar());

        EclipseScheduler.runTaskTimer(this, task -> {
            if (!player.isOnline() || !barInfo.stillRunning()) {
                barInfo.remove();
                bossBars.remove(player.getUniqueId());
                task.cancel();
                return;
            }

            int remaining = (int) (barInfo.getLeftDuration() / 1000);
            if (remaining <= 0) {
                player.teleport(target);
                player.sendMessage(getMessage("teleportSuccess"));
                barInfo.remove();
                bossBars.remove(player.getUniqueId());
                task.cancel();
            } else {
                barInfo.setTitleOfBar(getMessage("teleportWithDelay").replace("%seconds%", String.valueOf(remaining)));
            }
        }, 0L, 20L);
    }

    

    public EclipseHomeManager getHomeManager() { return homeManager == null ? (homeManager = new EclipseHomeManager(this)) : homeManager; }
    public EclipsePermissionManager getPermissionManager() { return permManager == null ? (permManager = new EclipsePermissionManager(this)) : permManager; }
    public EclipseWarpManager getWarpManager() { return warpManager == null ? (warpManager = new EclipseWarpManager(this)) : warpManager; }
    public EclipseAliasManager getAliasManager() { return aliasManager == null ? (aliasManager = new EclipseAliasManager(this)) : aliasManager; }
    public EclipseEconomyManager getEconomyManager() { return economyManager == null ? (economyManager = new EclipseEconomyManager(this)) : economyManager; }
    public EclipseChatManager getChatManager() { return chatManager == null ? (chatManager = new EclipseChatManager(this)) : chatManager; }
    public EclipseModuleManager getModuleManager() { return moduleManager == null ? (moduleManager = new EclipseModuleManager(this)) : moduleManager; }
    public EclipseHologramManager getHologramManager() { return hologramManager == null ? (hologramManager = new EclipseHologramManager(this)) : hologramManager; }
    public EclipseKitManager getKitManager() { return kitManager == null ? (kitManager = new EclipseKitManager(this)) : kitManager; }
    public VersionChecker getVersionCheckManager() { return versionCheckManager == null ? (versionCheckManager = new VersionChecker(this, "CatsT0day", "Eclipse")) : versionCheckManager; }
    public EclipseSQLiteManager getSQLiteManager() { return SQLiteManager == null ? (EclipseSQLiteManager) null : SQLiteManager;}
    public me.catst0day.Eclipse.Holograms.Animations.AnimationManager getAnimationManager() { return animationManager; }
    public EclipseMailManager getMailManager() { return mailManager == null ? (mailManager = new EclipseMailManager(this)) : mailManager; }
    public EclipseAuctionManager getAuctionManager() { return auctionManager == null ? (auctionManager = new EclipseAuctionManager(this)) : auctionManager; }
    public boolean isGodMode(UUID uuid) { return godMode.getOrDefault(uuid, false); }
    public Map<UUID, UUID> getTpaRequests() { return tpaRequests; }

    public void toggleGodMode(@NotNull Player sender, @NotNull String[] args) {
        Player target = (args.length == 1) ? Bukkit.getPlayer(args[0]) : sender;
        if (target == null) {
            sender.sendMessage(getMessage("playerNotFound"));
            return;
        }

        UUID uuid = target.getUniqueId();
        boolean newState = !isGodMode(uuid);
        godMode.put(uuid, newState);

        String status = newState ? getMessage("enabled") : getMessage("disabled");
        target.sendMessage(getMessage("godToggled").replace("%status%", status));
    }

    public EclipsePlr getPlayer(UUID uuid) { return uuid == null ? null : new EclipsePlr(uuid); }
    public EclipsePlr getPlayer(Player player) { return player == null ? null : new EclipsePlr(player.getUniqueId()); }
}