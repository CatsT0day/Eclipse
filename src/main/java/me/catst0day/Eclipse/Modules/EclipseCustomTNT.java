package me.catst0day.Eclipse.Modules;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Schedulers.EclipseScheduler;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class EclipseCustomTNT implements Listener {

    private final Eclipse plugin;
    private final FileConfiguration config;

    public EclipseCustomTNT(Eclipse plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.TNT || !item.hasItemMeta()) return;
        String displayName = item.getItemMeta().getDisplayName();
        ConfigurationSection tnts = config.getConfigurationSection("tnts");
        if (tnts == null) return;

        for (String key : tnts.getKeys(false)) {
            String configName = TextUtil.translateHexAndAlternateColorCodes(tnts.getString(key + ".display-hologramm"));
            if (displayName.contains(Objects.requireNonNull(TextUtil.stripColor(configName)))) {
                event.getBlock().setType(Material.AIR);
                spawnCustomTNT(event.getBlock().getLocation(), key);
                break;
            }
        }
    }

    private void spawnCustomTNT(Location loc, String type) {
        TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc.add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
        tnt.setMetadata("eclipse_tnt_type", new FixedMetadataValue(plugin, type));
        String text = config.getString("tnts." + type + ".display-hologramm");
        ArmorStand holo = loc.getWorld().spawn(loc.clone().add(0, 1, 0), ArmorStand.class, as -> {
            as.setVisible(false);
            as.setMarker(true);
            as.setCustomNameVisible(true);
            as.setCustomName(TextUtil.translateHexAndAlternateColorCodes(text));
        });
        EclipseScheduler.runTaskTimer(plugin, task -> {
            if (!tnt.isValid() || tnt.isDead()) {
                holo.remove();
                task.cancel();
                return;
            }
            holo.teleport(tnt.getLocation().add(0, 1.2, 0));
        }, 0L, 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (!event.getEntity().hasMetadata("eclipse_tnt_type")) return;
        String type = event.getEntity().getMetadata("eclipse_tnt_type").get(0).asString();
        ConfigurationSection sec = config.getConfigurationSection("tnts." + type);
        if (sec == null) return;
        float power = (float) sec.getDouble("explosionPower", 4.0);
        Location loc = event.getLocation();
        event.setCancelled(true);
        loc.getWorld().createExplosion(loc, power, false, sec.contains("break-obsidian"));
        if (sec.getBoolean("effects.is")) {
            ConfigurationSection effects = sec.getConfigurationSection("effects");
            for (String eKey : effects.getKeys(false)) {
                if (eKey.equals("is")) continue;
                applyEffect(Objects.requireNonNull(effects.getConfigurationSection(eKey)), loc);
            }
        }
    }

    private void applyEffect(ConfigurationSection s, Location loc) {
        PotionEffectType type = PotionEffectType.getByName(Objects.requireNonNull(s.getString("type")));
        if (type == null) return;

        int duration = s.getInt("effectTime") * 20;
        int amp = s.getInt("amplifier");
        double rad = s.getDouble("radius");

        loc.getWorld().getNearbyEntities(loc, rad, rad, rad).forEach(ent -> {
            if (ent instanceof LivingEntity le) le.addPotionEffect(new PotionEffect(type, duration, amp));
        });
    }
}