package me.catst0day.Eclipse.Holograms;

import me.catst0day.Eclipse.NMS.NMSHandler;
import me.catst0day.Eclipse.NMS.NMSRegistry;
import me.catst0day.Eclipse.Utils.Text.RawJsonMessage;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import me.catst0day.Eclipse.Utils.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class EclipseHologramPacket {
    private static final Map<UUID, Map<Integer, UUID>> hologramEntities = new HashMap<>();
    private static final double LINE_HEIGHT = 0.28;
    private static NMSHandler nmsHandler;

    static {
        nmsHandler = NMSRegistry.getHandler();
    }


    private static int generateEntityId() {
        return (int) (System.nanoTime() & 0x7FFFFFFF);
    }

    private static int hexToRgb(String hex) {
        if (hex == null || hex.isEmpty()) return 0;
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            return Integer.parseInt(hex, 16);
        } catch (Exception e) {
            return 0;
        }
    }


    private static int calculateAutoLineWidth(String text, int configuredWidth) {
        if (text == null || text.isEmpty()) {
            return Math.max(configuredWidth, 1000);
        }
        String stripped = text.replaceAll("§[0-9a-fk-or]", "").replaceAll("&[0-9a-fk-or]", "");
        int estimatedWidth = (int) (stripped.length() * 7 * 1.5) + 50;
        return Math.max(Math.max(estimatedWidth, configuredWidth), 1000);
    }

    private static Object getSpawnPacket(Location loc, int entityId, UUID uuid, Object entityType) throws Exception {
        return nmsHandler.createSpawnPacket(entityId, uuid, loc, entityType);
    }

    public static void showHologram(Player player, EclipseHologram hologram) {
        UUID playerUuid = player.getUniqueId();
        Map<Integer, UUID> playerEntities = hologramEntities.computeIfAbsent(playerUuid, k -> new HashMap<>());

        Location baseLoc = hologram.getLocation();
        int page = hologram.getPlayerPage(player);
        List<String> lines = hologram.getLinesForPage(page);

        for (int i = 0; i < lines.size(); i++) {
            UUID entityUuid = UUID.randomUUID();
            int entityId = generateEntityId();

            Location lineLoc = baseLoc.clone().subtract(0, i * LINE_HEIGHT, 0);
            String lineText = hologram.parseLine(lines.get(i), player);
            lineText = TextUtil.translateHexAndAlternateColorCodes(lineText);
            if (nmsHandler.supportsTextDisplay() && nmsHandler.getTextDisplayType() != null) {
                spawnTextDisplay(player, entityId, entityUuid, lineLoc, lineText, hologram);
            } else {
                spawnArmorStand(player, entityId, entityUuid, lineLoc, lineText);
            }
            playerEntities.put(entityId, entityUuid);
        }
    }

    public static void hideHologram(Player player, EclipseHologram hologram) {
        UUID playerUuid = player.getUniqueId();
        Map<Integer, UUID> playerEntities = hologramEntities.get(playerUuid);

        if (playerEntities != null) {
            List<Integer> idsToDestroy = new ArrayList<>(playerEntities.keySet());
            if (!idsToDestroy.isEmpty()) {
                destroyEntities(player, idsToDestroy);
            }
            playerEntities.clear();
            hologramEntities.remove(playerUuid);
        }
    }

    public static void updateHologram(Player player, EclipseHologram hologram) {
        hideHologram(player, hologram);
        showHologram(player, hologram);
    }

    private static Object toNmsComponent(Component adventureComponent) throws Exception {
        return nmsHandler.toNmsComponent(adventureComponent);
    }

    private static void sendPacket(Player player, Object packet) throws Exception {
        nmsHandler.sendPacket(player, packet);
    }

    private static void spawnTextDisplay(Player player, int entityId, UUID entityUuid, Location location, String name, EclipseHologram hologram) {
        try {
            Object worldServer = nmsHandler.getWorldServer(location.getWorld());
            Object textDisplay = nmsHandler.createTextDisplay(worldServer, entityId, entityUuid, location);

            RawJsonMessage jsonMessage = new RawJsonMessage().addText(name);
            Object chatComponent = toNmsComponent(jsonMessage.getResult());
            nmsHandler.setTextDisplayText(textDisplay, chatComponent);
                int lineWidth = 3000;
                nmsHandler.setTextDisplayLineWidth(textDisplay, lineWidth);
            try {
                String backgroundColor = hologram.getBackgroundColor();
                int textAlpha = hologram.getTextAlpha();
                boolean shadowed = hologram.isTextShadow();
                boolean seeThrough = hologram.isTextSeeThrough();
                EclipseHologram.TextAlignment alignment = hologram.getTextAlignment();
                nmsHandler.setTextDisplayBackgroundColor(textDisplay, hexToRgb(backgroundColor));
                nmsHandler.setTextDisplayOpacity(textDisplay, (byte) textAlpha);

                byte flags = 0;
                if (shadowed) flags |= 1;
                if (seeThrough) flags |= 2;

                if (alignment == EclipseHologram.TextAlignment.LEFT) {
                    flags |= (1 << 2);
                } else if (alignment == EclipseHologram.TextAlignment.RIGHT) {
                    flags |= (2 << 2);
                }

                nmsHandler.setTextDisplayFlags(textDisplay, flags);
                Object centerBillboard = nmsHandler.getCenterBillboard();
                nmsHandler.setTextDisplayBillboard(textDisplay, centerBillboard);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Object spawnPacket = getSpawnPacket(location, entityId, entityUuid, nmsHandler.getTextDisplayType());
            Object entityData = nmsHandler.getEntityData(textDisplay);
            List<?> allData = nmsHandler.packAllEntityData(entityData);
            Object metadataPacket = nmsHandler.createMetadataPacket(entityId, allData);

            sendPacket(player, spawnPacket);
            sendPacket(player, metadataPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void spawnArmorStand(Player player, int entityId, UUID entityUuid, Location location, String name) {
        try {
            Object worldServer = nmsHandler.getWorldServer(location.getWorld());
            Object armorStand = nmsHandler.createArmorStand(worldServer, location);

            nmsHandler.setEntityId(armorStand, entityId);
            nmsHandler.setEntityUuid(armorStand, entityUuid);
            nmsHandler.setInvisible(armorStand, true);
            nmsHandler.setCustomNameVisible(armorStand, true);

            RawJsonMessage jsonMessage = new RawJsonMessage().addText(name);
            Object chatComponent = toNmsComponent(jsonMessage.getResult());
            nmsHandler.setCustomName(armorStand, chatComponent);

            Object spawnPacket = getSpawnPacket(location, entityId, entityUuid, nmsHandler.getArmorStandType());
            Object entityData = nmsHandler.getEntityData(armorStand);
            List<?> allData = nmsHandler.packAllEntityData(entityData);
            Object metadataPacket = nmsHandler.createMetadataPacket(entityId, allData);

            sendPacket(player, spawnPacket);
            sendPacket(player, metadataPacket);
        } catch (Exception e) {
            Util.log("&4Error while spawning armorstand for " + player.getName());
            e.printStackTrace();
        }
    }

    private static void destroyEntities(Player player, List<Integer> entityIds) {
        try {
            int[] ids = entityIds.stream().mapToInt(i -> i).toArray();
            Object destroyPacket = nmsHandler.createDestroyPacket(ids);
            sendPacket(player, destroyPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearPlayerData(Player player) {
        hologramEntities.remove(player.getUniqueId());
    }

    public static void clearPlayerData(UUID uuid) {
        hologramEntities.remove(uuid);
    }

    public static void clearHologramEntities(UUID hologramUuid) {
        for (Map<Integer, UUID> playerEntities : hologramEntities.values()) {
            playerEntities.values().remove(hologramUuid);
        }
    }

    public static UUID getHologramFromEntity(Player player, Location location) {
        Map<Integer, UUID> playerEntities = hologramEntities.get(player.getUniqueId());
        if (playerEntities == null) {
            return null;
        }
        
        for (UUID hologramUuid : playerEntities.values()) {
            return hologramUuid;
        }
        
        return null;
    }
}