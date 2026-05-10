package me.catst0day.Eclipse.Managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Managers.Perms.LuckPermsHandler5;
import me.catst0day.Eclipse.Managers.Perms.NoneHandler;
import me.catst0day.Eclipse.Managers.Perms.PermissionInfo;
import me.catst0day.Eclipse.Managers.Perms.PermissionInterface;
import me.catst0day.Eclipse.Utils.Schedulers.EclipseScheduler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import static me.catst0day.Eclipse.Utils.Util.log;

public class EclipsePermissionManager {
    private Eclipse plugin;
    private PermissionInterface perm = new NoneHandler();
    private HashMap<UUID, HashMap<String, PermissionInfo>> cache = new HashMap<>();

    public EclipsePermissionManager(Eclipse var1) {
        this.plugin = var1;
        EclipseScheduler.runTask(plugin, this::checkPermissions);
    }

    private void permissionMessage(String var1) {
        log("Permission plugin: &5" + var1);
    }

    public void checkPermissions() {
        PluginManager var1 = this.plugin.getServer().getPluginManager();
        Plugin var2 = var1.getPlugin("LuckPerms");
        if (var2 != null && var2.isEnabled()) {
            Integer var3 = this.plugin.getVersionCheckManager().convertVersion(var2.getDescription().getVersion());

            if (var3 >= 50000) {
                this.perm = new LuckPermsHandler5();
                this.permissionMessage("LuckPerms v5 " + var2.getDescription().getVersion());
            } else {
                log("&cLuckPerms plugin found but is outdated (v" + var2.getDescription().getVersion() + ")");
                this.perm = new NoneHandler();
            }
            return;
        }

        this.perm = new NoneHandler();
        this.permissionMessage("None");
    }

    public boolean hasPermission(Player player, EclipsePerm permission, String... args) {
        if (player == null) return false;
        String permNode = permission.getPermission(args);
        return isSetPermission(player, permNode);
    }

    public String getMainGroup(Player var1) {
        if (var1 == null) return "";
        String var2 = this.perm.getMainGroup(var1);
        return var2 == null ? "" : var2;
    }

    public String getPrefix(UUID var1) {
        String var2 = this.perm.getPrefix(var1);
        return var2 == null ? "" : var2;
    }

    public String getSufix(UUID var1) {
        String var2 = this.perm.getSufix(var1);
        return var2 == null ? "" : var2;
    }

    public String getPrefix(Player var1) {
        String var2 = this.perm.getPrefix(var1);
        return var2 == null ? "" : var2;
    }

    public String getSufix(Player var1) {
        String var2 = this.perm.getSufix(var1);
        return var2 == null ? "" : var2;
    }

    public String getNameColor(Player var1) {
        String var2 = this.perm.getNameColor(var1);
        return var2 == null ? "" : var2;
    }

    public PermissionAttachmentInfo getSetPermission(CommandSender var1, String var2) {
        if (var1 instanceof Player) {
            Iterator<PermissionAttachmentInfo> var4 = ((Player) var1).getEffectivePermissions().iterator();
            while (var4.hasNext()) {
                PermissionAttachmentInfo var3 = var4.next();
                if (var3.getPermission().equalsIgnoreCase(var2)) {
                    return var3;
                }
            }
            return null;
        }
        return null;
    }

    public boolean isSetPermission(CommandSender var1, String var2) {
        return !(var1 instanceof Player) || this.isSetPermission((Player) var1, var2);
    }

    public boolean isSetPermission(Player var1, String var2) {
        return var1.hasPermission(new Permission(var2, PermissionDefault.FALSE));
    }

    private static HashMap<String, Boolean> getAll(Player var0, String var1) {
        var1 = var1.endsWith(".") ? var1 : var1 + ".";
        HashMap<String, Boolean> var2 = new HashMap<>();
        for (PermissionAttachmentInfo var3 : var0.getEffectivePermissions()) {
            if (var3.getPermission().startsWith(var1)) {
                var2.put(var3.getPermission(), var3.getValue());
            }
        }
        return var2;
    }

    public void removeFromCache(Player var1) {
        this.cache.remove(var1.getUniqueId());
    }

    public PermissionInfo getFromCache(Player var1, String var2) {
        return this.getFromCache(var1.getUniqueId(), var2);
    }

    public PermissionInfo getFromCache(UUID var1, String var2) {
        HashMap<String, PermissionInfo> var3 = this.cache.get(var1);
        if (var3 == null) {
            return null;
        } else {
            PermissionInfo var4 = var3.get(var2);
            return var4 == null ? null : var4;
        }
    }

    public PermissionInfo addToCache(Player var1, String var2, boolean var3, Long var4) {
        return this.addToCache(var1.getUniqueId(), var2, var3, var4);
    }

    public PermissionInfo addToCache(UUID var1, String var2, boolean var3, Long var4) {
        HashMap<String, PermissionInfo> var5 = this.cache.get(var1);
        if (var5 == null) {
            var5 = new HashMap<>();
        }
        PermissionInfo var6 = new PermissionInfo(var2, var4);
        var6.setLastChecked(System.currentTimeMillis());
        var6.setEnabled(var3);
        var5.put(var2, var6);
        this.cache.put(var1, var5);
        return var6;
    }

    public enum EclipsePerm {
        REVISE("eclipse.revise", "revise players"),
        MAIN("eclipse.main", "gives perm to use main command (not all cmds)"),
        UPDATE_NOTIFY("eclipse.update.notify", "Receive update notifications for EclipseAPI updates"),
        WARP_USE("eclipse.warp.use", "Permission to use /warp command"),
        WARP_SET("eclipse.warp.set", "Permission to use /setwarp command"),
        SUDO("eclipse.sudo", "Permission to use /sudo command"),
        RELOAD("eclipse.reload", "Permission to reload the plugin via /eclipse"),
        HELP("eclipse.help", "Permission to use the help command"),
        TP("eclipse.tp", "Permission to use teleport commands"),
        VANISH("eclipse.vanish", "Allows using the vanish command"),
        ENCHANT("eclipse.enchant", "Allows enchanting items via command"),
        TPHERE("eclipse.tphere", "Permission to use the /tphere command"),
        SETSPAWN("eclipse.setspawn", "Permission to set the spawn point"),
        SPAWN("eclipse.spawn", "Permission to teleport to the spawn"),
        SPEC("eclipse.spec", "Permission to enter spectator mode"),
        GM("eclipse.gm", "Permission to change game mode"),
        GOD("eclipse.god", "Permission to enable god mode"),
        FLY("eclipse.fly", "Permission to enable flying"),
        HEAL("eclipse.heal", "Permission to heal players"),
        FEED("eclipse.feed", "Permission to feed players"),
        FIX("eclipse.fix", "Permission to repair items"),
        NEAR("eclipse.near", "Permission to use the nearby players radar"),
        NEAR_RADIUS_5("eclipse.near.radius.5", "Permission to use the nearby players radar"),
        COOLDOWN_BYPASS("eclipse.cooldown.bypass", "Permission to bypass cooldowns"),
        ALL("eclipse.*", "All EclipseAPI permissions"),
        WARP_ALL("eclipse.warp.*", "All warp permissions"),
        DAY("eclipse.day", "Permission to set day time"),
        MAX_HOMES("eclipse.maxhomes.$1", "Maximum number of homes player can set"),
        HOME("eclipse.home", "Perm to use /home"),
        NEAR_RADIUS("eclipse.near.radius.$1", "Max radius for /near"),
        NIGHT("eclipse.night", "set night time"),
        TPA("eclipse.tpa", "send teleport request to player"),
        SUICIDE("eclipse.suicide", "kill your self"),
        ALIAS_EDITOR("eclipse.aliaseditor", "edit aliases"),
        AFK("eclipse.afk", "enter afk mode"),
        AFKCHECK("eclipse.afkcheck","chech afk mode"),
        PWEATHER("eclipse.playerweather", "set plr's weather"),
        PTIME("eclipse.playertime", "set plr's time"),
        BACK("eclipse.back", "back to death loc"),
        CLEAR("eclipse.clearinventory", "clear user invenotry"),
        INVSEE("eclipse.seeinventory","see other player's invenory's"),
        ELYTRAFLY("eclipse.elytrafly", "fly with elytra"),
        EXP("eclipse.expierience", "edit exp"),
        HOLOGRAMS("eclipse.holo","hologram api"),
        TNTGIVE("eclipse.givetnt", "give custom tnts");

        private final String permission;
        private final String description;

        EclipsePerm(String permission, String description) {
            this.permission = permission;
            this.description = description;
        }

        public String getPermission(String[] args) {
            return permission;
        }

        public String getDescription() {
            return description;
        }


        public boolean hasPermission(Player player, String... args) {
            if (player == null) return false;

            String permNode = this.permission;

            // Если есть параметры и шаблон содержит $1, заменяем
            if (args != null && args.length > 0 && permNode.contains("$1")) {
                permNode = permNode.replace("$1", args[0]);
            }

            return player.hasPermission(permNode);
        }
    }
}