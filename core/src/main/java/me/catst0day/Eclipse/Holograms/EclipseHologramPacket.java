// there's a bug with test warpping, cascade, can you fix it? every letter, text wraps, disable the wrapping

package me.catst0day.Eclipse.Holograms;

import me.catst0day.Eclipse.Utils.Text.RawJsonMessage;
import me.catst0day.Eclipse.Utils.Text.TextUtil;
import me.catst0day.Eclipse.Utils.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class EclipseHologramPacket {
    private static final Map<UUID, Map<Integer, UUID>> hologramEntities = new HashMap<>();
    private static final double LINE_HEIGHT = 0.28;
    private static final boolean IS_MODERN;

    private static Class<?> craftPlayerClass;
    private static Class<?> craftWorldClass;
    private static Class<?> craftChatMessageClass;
    private static Class<?> packetClass;
    private static Class<?> packetPlayOutSpawnEntityClass;
    private static Class<?> packetPlayOutEntityMetadataClass;
    private static Class<?> packetPlayOutEntityDestroyClass;
    private static Class<?> entityTypesClass;
    private static Class<?> iChatBaseComponentClass;
    private static Class<?> synchedEntityDataClass;
    private static Class<?> vec3DClass;

    private static Object armorStandType;
    private static Object textDisplayType;

    static {
        boolean modernTemp = false;
        try {
            String version = Bukkit.getBukkitVersion().split("-")[0];
            String[] parts = version.split("\\.");
            if (parts.length >= 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                if (major > 1 || (major == 1 && minor >= 19)) {
                    modernTemp = true;
                }
            }
        } catch (Exception e) {
            modernTemp = true;
        }
        IS_MODERN = modernTemp;

        try {
            craftPlayerClass = resolveClass("org.bukkit.craftbukkit." + getNmsVersion() + ".entity.CraftPlayer", "org.bukkit.craftbukkit.entity.CraftPlayer");
            craftWorldClass = resolveClass("org.bukkit.craftbukkit." + getNmsVersion() + ".CraftWorld", "org.bukkit.craftbukkit.CraftWorld");
            craftChatMessageClass = resolveClass("org.bukkit.craftbukkit." + getNmsVersion() + ".util.CraftChatMessage", "org.bukkit.craftbukkit.util.CraftChatMessage");

            packetClass = resolveClass("net.minecraft.network.protocol.Packet");
            packetPlayOutSpawnEntityClass = resolveClass("net.minecraft.network.protocol.game.ClientboundAddEntityPacket", "net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
            packetPlayOutEntityMetadataClass = resolveClass("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket", "net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
            packetPlayOutEntityDestroyClass = resolveClass("net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket", "net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
            entityTypesClass = resolveClass("net.minecraft.world.entity.EntityType", "net.minecraft.world.entity.EntityTypes");
            iChatBaseComponentClass = resolveClass("net.minecraft.network.chat.Component", "net.minecraft.network.chat.IChatBaseComponent");
            synchedEntityDataClass = resolveClass("net.minecraft.network.syncher.SynchedEntityData");
            vec3DClass = resolveClass("net.minecraft.world.phys.Vec3", "net.minecraft.world.phys.Vec3D");

            if (entityTypesClass != null) {
                armorStandType = getStaticFieldValue(entityTypesClass, "ARMOR_STAND", "d", "c");
                if (IS_MODERN) {
                    textDisplayType = getStaticFieldValue(entityTypesClass, "TEXT_DISPLAY", "aI");
                }
            }
        } catch (Exception e) {
            Util.log("an error ouccured while initting holo reflect!" + e.getMessage());
        }
    }

    private static String getNmsVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private static Class<?> resolveClass(String... names) {
        for (String name : names) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ignored) {}
        }
        return null;
    }

    private static Object getStaticFieldValue(Class<?> clazz, String... names) throws Exception {
        for (String name : names) {
            try {
                Field field = clazz.getField(name);
                field.setAccessible(true);
                return field.get(null);
            } catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException("None of the fields found in " + clazz.getName());
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

    /**
     * Calculates automatic line width based on text content.
     * Minecraft characters are approximately 6-8 pixels wide.
     * This prevents the "one letter per line" bug when lineWidth is too small.
     */
    private static int calculateAutoLineWidth(String text, int configuredWidth) {
        if (text == null || text.isEmpty()) {
            return Math.max(configuredWidth, 1000);
        }
        String stripped = text.replaceAll("§[0-9a-fk-or]", "").replaceAll("&[0-9a-fk-or]", "");
        int estimatedWidth = (int) (stripped.length() * 7 * 1.5) + 50;
        return Math.max(Math.max(estimatedWidth, configuredWidth), 1000);
    }

    private static Method findMethodByParams(Class<?> clazz, Class<?> returnType, Class<?>... parameterTypes) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getParameterCount() == parameterTypes.length) {
                boolean match = true;
                Class<?>[] params = method.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    if (!params[i].isAssignableFrom(parameterTypes[i])) {
                        match = false;
                        break;
                    }
                }
                if (match && (returnType == null || returnType.isAssignableFrom(method.getReturnType()))) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        return null;
    }

    private static Object getSpawnPacket(Object entity, Location loc, int entityId, UUID uuid, Object entityType) throws Exception {
        try {
            Method getAddEntityPacket = entity.getClass().getMethod("getAddEntityPacket");
            return getAddEntityPacket.invoke(entity);
        } catch (Exception ignored) {}

        try {
            Method getAddEntityPacket = findMethodByParams(entity.getClass(), packetClass);
            if (getAddEntityPacket != null) {
                return getAddEntityPacket.invoke(entity);
            }
        } catch (Exception ignored) {}

        for (Constructor<?> constructor : packetPlayOutSpawnEntityClass.getConstructors()) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length >= 7 && paramTypes[0] == int.class && paramTypes[1] == UUID.class) {
                try {
                    Object[] args = new Object[paramTypes.length];
                    args[0] = entityId;
                    args[1] = uuid;
                    args[2] = loc.getX();
                    args[3] = loc.getY();
                    args[4] = loc.getZ();
                    args[5] = loc.getPitch();
                    args[6] = loc.getYaw();

                    for (int i = 7; i < paramTypes.length; i++) {
                        Class<?> type = paramTypes[i];
                        if (type == entityTypesClass) {
                            args[i] = entityType;
                        } else if (type == int.class) {
                            args[i] = 0;
                        } else if (type == vec3DClass) {
                            args[i] = vec3DClass.getField("ZERO").get(null);
                        } else if (type == double.class) {
                            args[i] = 0.0;
                        } else if (type == float.class) {
                            args[i] = 0.0f;
                        } else if (type == byte.class) {
                            args[i] = (byte) 0;
                        } else {
                            args[i] = null;
                        }
                    }
                    return constructor.newInstance(args);
                } catch (Exception ignored) {}
            }
        }

        throw new NoSuchMethodException("Could not find suitable spawn packet constructor or method");
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
            
            Util.log("DEBUG: Line " + i + " text before display: '" + lineText + "' (length: " + lineText.length() + ")");
            Util.log("DEBUG: Line " + i + " contains newline: " + lineText.contains("\n") + ", carriage return: " + lineText.contains("\r"));

            if (IS_MODERN && textDisplayType != null) {
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
        if (adventureComponent == null) return null;
        String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(adventureComponent);
        Method fromJSON = craftChatMessageClass.getMethod("fromJSON", String.class);
        return fromJSON.invoke(null, json);
    }

    private static void sendPacket(Player player, Object packet) throws Exception {
        Object craftPlayer = craftPlayerClass.cast(player);
        Object handle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);

        Object connection;
        try {
            connection = handle.getClass().getField("connection").get(handle);
        } catch (NoSuchFieldException e) {
            connection = handle.getClass().getField("c").get(handle);
        }

        Method sendMethod = null;
        for (Method m : connection.getClass().getMethods()) {
            if ((m.getName().equals("send") || m.getName().equals("sendPacket") || m.getName().equals("a"))
                    && m.getParameterCount() == 1
                    && packetClass.isAssignableFrom(m.getParameterTypes()[0])) {
                sendMethod = m;
                break;
            }
        }
        if (sendMethod != null) {
            sendMethod.invoke(connection, packet);
        }
    }

    private static void spawnTextDisplay(Player player, int entityId, UUID entityUuid, Location location, String name, EclipseHologram hologram) {
        try {
            Object craftWorld = craftWorldClass.cast(location.getWorld());
            Object worldServer = craftWorldClass.getMethod("getHandle").invoke(craftWorld);

            Class<?> textDisplayClass = Class.forName("net.minecraft.world.entity.Display$TextDisplay");
            Constructor<?> entityConstructor = textDisplayClass.getConstructor(entityTypesClass, Class.forName("net.minecraft.world.level.Level"));
            Object textDisplay = entityConstructor.newInstance(textDisplayType, worldServer);

            textDisplayClass.getMethod("setId", int.class).invoke(textDisplay, entityId);
            textDisplayClass.getMethod("setUUID", UUID.class).invoke(textDisplay, entityUuid);
            textDisplayClass.getMethod("setPos", double.class, double.class, double.class).invoke(textDisplay, location.getX(), location.getY(), location.getZ());
            textDisplayClass.getMethod("setRot", float.class, float.class).invoke(textDisplay, location.getYaw(), location.getPitch());

            RawJsonMessage jsonMessage = new RawJsonMessage().addText(name);
            Object chatComponent = toNmsComponent(jsonMessage.getResult());

            Method setTextMethod;
            try {
                setTextMethod = textDisplayClass.getMethod("setText", iChatBaseComponentClass);
            } catch (NoSuchMethodException e) {
                setTextMethod = findMethodByParams(textDisplayClass, void.class, iChatBaseComponentClass);
            }
            if (setTextMethod != null) {
                setTextMethod.invoke(textDisplay, chatComponent);
            }
            try {
                int lineWidth = 3000;
                Util.log("DEBUG: Setting lineWidth to " + lineWidth + " to disable text wrapping");

                Method setLineWidthMethod = null;
                try {
                    setLineWidthMethod = textDisplayClass.getMethod("setLineWidth", int.class);
                } catch (NoSuchMethodException ignored) {}
                if (setLineWidthMethod == null) {
                    for (Method method : textDisplayClass.getDeclaredMethods()) {
                        if (method.getParameterCount() == 1
                                && method.getParameterTypes()[0] == int.class
                                && method.getReturnType() == void.class) {

                            String mName = method.getName().toLowerCase();
                            if (!mName.contains("background") && !mName.contains("id") && !mName.contains("pos")) {
                                setLineWidthMethod = method;
                                setLineWidthMethod.setAccessible(true);
                                break;
                            }
                        }
                    }
                }

                if (setLineWidthMethod != null) {
                    setLineWidthMethod.invoke(textDisplay, lineWidth);
                    Util.log("DEBUG: Set lineWidth through method: " + setLineWidthMethod.getName());
                } else {
                    Util.log("DEBUG: Could not find any method to set lineWidth");
                }
            } catch (Exception e) {
                Util.log("DEBUG: Error in lineWidth post-setting: " + e.getMessage());
            }

            try {
                Object settings = hologram.getClass().getMethod("getTextSettings").invoke(hologram);
                String backgroundColor = (String) settings.getClass().getMethod("getBackgroundColor").invoke(settings);
                int textAlpha = (int) settings.getClass().getMethod("getTextAlpha").invoke(settings);
                boolean shadowed = (boolean) settings.getClass().getMethod("isShadowed").invoke(settings);
                boolean seeThrough = (boolean) settings.getClass().getMethod("isSeeThrough").invoke(settings);
                Object alignment = settings.getClass().getMethod("getTextAlignment").invoke(settings);

                Method setBgMethod = null;
                try {
                    setBgMethod = textDisplayClass.getMethod("setBackgroundColor", int.class);
                } catch (NoSuchMethodException e) {
                    for (Method m : textDisplayClass.getDeclaredMethods()) {
                        if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == int.class && m.getName().toLowerCase().contains("background")) {
                            setBgMethod = m;
                            setBgMethod.setAccessible(true);
                            break;
                        }
                    }
                }
                if (setBgMethod != null) {
                    setBgMethod.invoke(textDisplay, hexToRgb(backgroundColor));
                }

                Method setOpacityMethod;
                try {
                    setOpacityMethod = textDisplayClass.getMethod("setTextOpacity", byte.class);
                } catch (NoSuchMethodException e) {
                    setOpacityMethod = findMethodByParams(textDisplayClass, void.class, byte.class);
                }
                if (setOpacityMethod != null) {
                    setOpacityMethod.invoke(textDisplay, (byte) textAlpha);
                }

                byte flags = 0;
                if (shadowed) flags |= 1;
                if (seeThrough) flags |= 2;

                String alignName = alignment.toString();
                if (alignName.equals("LEFT")) {
                    flags |= (1 << 2);
                } else if (alignName.equals("RIGHT")) {
                    flags |= (2 << 2);
                }

                Method setFlagsMethod;
                try {
                    setFlagsMethod = textDisplayClass.getMethod("setFlags", byte.class);
                } catch (NoSuchMethodException e) {
                    setFlagsMethod = findMethodByParams(textDisplayClass, void.class, byte.class);
                }
                if (setFlagsMethod != null) {
                    setFlagsMethod.invoke(textDisplay, flags);
                }

                Class<?> billboardClass = Class.forName("net.minecraft.world.entity.Display$BillboardConstraints");
                Object centerBillboard = billboardClass.getField("CENTER").get(null);

                Method setBillboardMethod;
                try {
                    setBillboardMethod = textDisplayClass.getMethod("setBillboardConstraints", billboardClass);
                } catch (NoSuchMethodException e) {
                    setBillboardMethod = findMethodByParams(textDisplayClass, void.class, billboardClass);
                }
                if (setBillboardMethod != null) {
                    setBillboardMethod.invoke(textDisplay, centerBillboard);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Object spawnPacket = getSpawnPacket(textDisplay, location, entityId, entityUuid, textDisplayType);
            Object entityData = textDisplayClass.getMethod("getEntityData").invoke(textDisplay);

            List<?> allData;
            try {
                allData = (List<?>) synchedEntityDataClass.getMethod("packAll").invoke(entityData);
            } catch (NoSuchMethodException e) {
                allData = (List<?>) synchedEntityDataClass.getMethod("getNonDefaultValues").invoke(entityData);
            }

            Object metadataPacket = packetPlayOutEntityMetadataClass.getConstructor(int.class, List.class).newInstance(entityId, allData);

            sendPacket(player, spawnPacket);
            sendPacket(player, metadataPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void spawnArmorStand(Player player, int entityId, UUID entityUuid, Location location, String name) {
        try {
            Object craftWorld = craftWorldClass.cast(location.getWorld());
            Object worldServer = craftWorldClass.getMethod("getHandle").invoke(craftWorld);

            Class<?> armorStandClass = Class.forName("net.minecraft.world.entity.decoration.ArmorStand");
            Constructor<?> entityConstructor = armorStandClass.getConstructor(Class.forName("net.minecraft.world.level.Level"), double.class, double.class, double.class);
            Object armorStand = entityConstructor.newInstance(worldServer, location.getX(), location.getY(), location.getZ());

            armorStandClass.getMethod("setId", int.class).invoke(armorStand, entityId);
            armorStandClass.getMethod("setUUID", UUID.class).invoke(armorStand, entityUuid);
            armorStandClass.getMethod("setInvisible", boolean.class).invoke(armorStand, true);
            armorStandClass.getMethod("setCustomNameVisible", boolean.class).invoke(armorStand, true);

            RawJsonMessage jsonMessage = new RawJsonMessage().addText(name);
            Object chatComponent = toNmsComponent(jsonMessage.getResult());
            armorStandClass.getMethod("setCustomName", iChatBaseComponentClass).invoke(armorStand, chatComponent);

            Object spawnPacket = getSpawnPacket(armorStand, location, entityId, entityUuid, armorStandType);

            Object entityData = armorStandClass.getMethod("getEntityData").invoke(armorStand);

            List<?> allData;
            try {
                allData = (List<?>) synchedEntityDataClass.getMethod("packAll").invoke(entityData);
            } catch (NoSuchMethodException e) {
                allData = (List<?>) synchedEntityDataClass.getMethod("getNonDefaultValues").invoke(entityData);
            }

            Object metadataPacket = packetPlayOutEntityMetadataClass.getConstructor(int.class, List.class).newInstance(entityId, allData);

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
            Object destroyPacket = packetPlayOutEntityDestroyClass.getConstructor(int[].class).newInstance((Object) ids);
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
}