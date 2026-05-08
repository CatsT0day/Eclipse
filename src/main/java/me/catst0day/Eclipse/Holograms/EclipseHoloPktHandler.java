package me.catst0day.Eclipse.Holograms;

import me.catst0day.Eclipse.Holograms.Settings.TextAlignment;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EclipseHoloPktHandler {
    private static final Map<UUID, Map<Integer, UUID>> hologramEntities = new HashMap<>();
    private static final double LINE_HEIGHT = 0.28;
    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final boolean USE_TEXT_DISPLAY = isTextDisplayAvailable();

    private static Class<?> packetSpawnEntity;
    private static Class<?> packetEntityMetadata;
    private static Class<?> packetEntityDestroy;
    private static Class<?> entityTypes;
    private static Class<?> chatBaseComponent;
    private static Class<?> packetDataSerializer;
    private static Class<?> iChatBaseComponent;
    private static Object armorStandType;
    private static Object textDisplayType;

    static {
        try {
            String nmsPackage = "net.minecraft.server." + VERSION;
            String craftPackage = "org.bukkit.craftbukkit." + VERSION;

            packetSpawnEntity = Class.forName(nmsPackage + ".PacketPlayOutSpawnEntity");
            packetEntityMetadata = Class.forName(nmsPackage + ".PacketPlayOutEntityMetadata");
            packetEntityDestroy = Class.forName(nmsPackage + ".PacketPlayOutEntityDestroy");
            entityTypes = Class.forName(nmsPackage + ".EntityTypes");
            chatBaseComponent = Class.forName(nmsPackage + ".IChatBaseComponent");
            packetDataSerializer = Class.forName(nmsPackage + ".PacketDataSerializer");
            iChatBaseComponent = Class.forName(nmsPackage + ".IChatBaseComponent");

            armorStandType = entityTypes.getField("ARMOR_STAND").get(null);

            if (USE_TEXT_DISPLAY) {
                try {
                    textDisplayType = entityTypes.getField("TEXT_DISPLAY").get(null);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isTextDisplayAvailable() {
        try {
            String version = Bukkit.getBukkitVersion();
            String[] parts = version.split("-");
            String mainVersion = parts[0];
            String[] versionNumbers = mainVersion.split("\\.");
            
            if (versionNumbers.length >= 2) {
                int major = Integer.parseInt(versionNumbers[0]);
                int minor = Integer.parseInt(versionNumbers[1]);
                
                if (major > 1) return true;
                if (major == 1 && minor >= 19) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
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

            if (USE_TEXT_DISPLAY && textDisplayType != null) {
                spawnTextDisplay(player, entityId, entityUuid, lineLoc, lineText, hologram);
            } else {
                spawnArmorStand(player, entityId, entityUuid, lineLoc, lineText, hologram);
            }
            playerEntities.put(entityId, entityUuid);
        }
    }

    public static void hideHologram(Player player, EclipseHologram hologram) {
        UUID playerUuid = player.getUniqueId();
        Map<Integer, UUID> playerEntities = hologramEntities.get(playerUuid);

        if (playerEntities != null) {
            for (Map.Entry<Integer, UUID> entry : new HashMap<>(playerEntities).entrySet()) {
                destroyEntity(player, entry.getKey(), entry.getValue());
            }
            playerEntities.clear();
            hologramEntities.remove(playerUuid);
        }
    }

    public static void updateHologram(Player player, EclipseHologram hologram) {
        hideHologram(player, hologram);
        showHologram(player, hologram);
    }

    private static void spawnTextDisplay(Player player, int entityId, UUID entityUuid, Location location, String name, EclipseHologram hologram) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            Method sendPacket = playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + VERSION + ".Packet"));

            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            float yaw = location.getYaw();
            float pitch = location.getPitch();

            String nmsPackage = "net.minecraft.server." + VERSION;

            Constructor<?> spawnConstructor = packetSpawnEntity.getConstructor(
                    int.class, UUID.class, double.class, double.class, double.class,
                    float.class, float.class, entityTypes.getClass(), int.class, int.class, int.class
            );
            Object spawnPacket = spawnConstructor.newInstance(
                    entityId, entityUuid, x, y, z, yaw, pitch, textDisplayType, 0, 0, 0
            );
            sendPacket.invoke(playerConnection, spawnPacket);

            Object buf = Class.forName("io.netty.buffer.Unpooled").getMethod("buffer").invoke(null);
            Object serializer = packetDataSerializer.getConstructor(Class.forName("io.netty.buffer.ByteBuf")).newInstance(buf);

            Method writeVarInt = packetDataSerializer.getMethod("writeVarInt", int.class);
            Method writeByte = packetDataSerializer.getMethod("writeByte", int.class);
            Method writeBoolean = packetDataSerializer.getMethod("writeBoolean", boolean.class);

            writeVarInt.invoke(serializer, entityId);

            writeByte.invoke(serializer, 0x02);
            Class<?> chatSerializerClass = Class.forName(nmsPackage + ".IChatBaseComponent$ChatSerializer");
            Method aMethod = chatSerializerClass.getMethod("a", String.class);
            Object chatComponent = aMethod.invoke(null, "{\"text\":\"" + escapeJson(name) + "\"}");
            Method aMethod2 = packetDataSerializer.getMethod("a", iChatBaseComponent);
            aMethod2.invoke(serializer, chatComponent);

            writeByte.invoke(serializer, 0x03);
            writeVarInt.invoke(serializer, hexToRgb(hologram.getTextSettings().getBackgroundColor()));

            writeByte.invoke(serializer, 0x04);
            writeByte.invoke(serializer, hologram.getTextSettings().getTextAlpha());

            writeByte.invoke(serializer, 0x05);
            writeByte.invoke(serializer, hologram.getTextSettings().getLineWidth());

            writeByte.invoke(serializer, 0x06);
            writeByte.invoke(serializer, hologram.getTextSettings().getBackgroundAlpha());

            if (hologram.getTextSettings().isShadowed()) {
                writeByte.invoke(serializer, 0x08);
                writeBoolean.invoke(serializer, true);
            }

            if (hologram.getTextSettings().isSeeThrough()) {
                writeByte.invoke(serializer, 0x09);
                writeBoolean.invoke(serializer, true);
            }

            writeByte.invoke(serializer, 0x0A);
            writeByte.invoke(serializer, getAlignmentByte(hologram.getTextSettings().getTextAlignment()));

            writeByte.invoke(serializer, 0xFF);

            Constructor<?> metadataConstructor = packetEntityMetadata.getConstructor(packetDataSerializer);
            Object metadataPacket = metadataConstructor.newInstance(serializer);
            sendPacket.invoke(playerConnection, metadataPacket);

            Class.forName("io.netty.buffer.ByteBuf").getMethod("release").invoke(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void spawnArmorStand(Player player, int entityId, UUID entityUuid, Location location, String name, EclipseHologram hologram) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            Method sendPacket = playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + VERSION + ".Packet"));

            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            float yaw = location.getYaw();
            float pitch = location.getPitch();
            Constructor<?> spawnConstructor = packetSpawnEntity.getConstructor(
                    int.class, UUID.class, double.class, double.class, double.class,
                    float.class, float.class, entityTypes.getClass(), int.class, int.class, int.class
            );
            Object spawnPacket = spawnConstructor.newInstance(
                    entityId, entityUuid, x, y, z, yaw, pitch, armorStandType, 0, 0, 0
            );
            sendPacket.invoke(playerConnection, spawnPacket);
            Object buf = Class.forName("io.netty.buffer.Unpooled").getMethod("buffer").invoke(null);
            Object serializer = packetDataSerializer.getConstructor(Class.forName("io.netty.buffer.ByteBuf")).newInstance(buf);

            Method writeVarInt = packetDataSerializer.getMethod("writeVarInt", int.class);
            Method writeByte = packetDataSerializer.getMethod("writeByte", int.class);
            Method writeBoolean = packetDataSerializer.getMethod("writeBoolean", boolean.class);

            writeVarInt.invoke(serializer, entityId);
            writeByte.invoke(serializer, 0x20);
            writeByte.invoke(serializer, 0x02);
            Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + VERSION + ".IChatBaseComponent$ChatSerializer");
            Method aMethod = chatSerializerClass.getMethod("a", String.class);
            Object chatComponent = aMethod.invoke(null, "{\"text\":\"" + escapeJson(name) + "\"}");
            Method aMethod2 = packetDataSerializer.getMethod("a", iChatBaseComponent);
            aMethod2.invoke(serializer, chatComponent);
            writeByte.invoke(serializer, 0x03);
            writeBoolean.invoke(serializer, true);
            writeByte.invoke(serializer, 0x05);
            writeBoolean.invoke(serializer, true);
            writeByte.invoke(serializer, 0xFF);

            Constructor<?> metadataConstructor = packetEntityMetadata.getConstructor(packetDataSerializer);
            Object metadataPacket = metadataConstructor.newInstance(serializer);
            sendPacket.invoke(playerConnection, metadataPacket);

            Class.forName("io.netty.buffer.ByteBuf").getMethod("release").invoke(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void destroyEntity(Player player, int entityId, UUID entityUuid) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            Method sendPacket = playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + VERSION + ".Packet"));

            Constructor<?> destroyConstructor = packetEntityDestroy.getConstructor(int[].class);
            Object destroyPacket = destroyConstructor.newInstance(new Object[]{new int[]{entityId}});
            sendPacket.invoke(playerConnection, destroyPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int generateEntityId() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static int hexToRgb(String hex) {
        if (hex == null || !hex.startsWith("#") || hex.length() != 7) return 0;
        try {
            int r = Integer.parseInt(hex.substring(1, 3), 16);
            int g = Integer.parseInt(hex.substring(3, 5), 16);
            int b = Integer.parseInt(hex.substring(5, 7), 16);
            return (r << 16) | (g << 8) | b;
        } catch (Exception e) {
            return 0;
        }
    }

    private static byte getAlignmentByte(TextAlignment alignment) {
        switch (alignment) {
            case LEFT: return 0;
            case CENTER: return 1;
            case RIGHT: return 2;
            default: return 0;
        }
    }

    public static void clearPlayerData(Player player) {
        hologramEntities.remove(player.getUniqueId());
    }
}
