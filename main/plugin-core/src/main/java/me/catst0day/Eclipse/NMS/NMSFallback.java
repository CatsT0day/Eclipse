package me.catst0day.Eclipse.NMS;

import me.catst0day.Eclipse.Utils.Util;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Fallback NMS handler using reflection for unsupported versions.
 * This maintains backward compatibility with the original reflection-based approach.
 */
public class NMSFallback implements NMSHandler {

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
        try {
            String nmsVersion = NMSVersion.getNmsVersion();
            craftPlayerClass = resolveClass("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer", "org.bukkit.craftbukkit.entity.CraftPlayer");
            craftWorldClass = resolveClass("org.bukkit.craftbukkit." + nmsVersion + ".CraftWorld", "org.bukkit.craftbukkit.CraftWorld");
            craftChatMessageClass = resolveClass("org.bukkit.craftbukkit." + nmsVersion + ".util.CraftChatMessage", "org.bukkit.craftbukkit.util.CraftChatMessage");

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
                if (NMSVersion.isModern()) {
                    textDisplayType = getStaticFieldValue(entityTypesClass, "TEXT_DISPLAY", "aI");
                }
            }
        } catch (Exception e) {
            Util.log("Error initializing fallback NMS handler: " + e.getMessage());
        }
    }

    @Override
    public String getVersion() {
        return "Fallback";
    }

    @Override
    public void sendPacket(Player player, Object packet) throws Exception {
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

    @Override
    public Object createSpawnPacket(int entityId, java.util.UUID entityUuid, Location location, Object entityType) throws Exception {
        for (Constructor<?> constructor : packetPlayOutSpawnEntityClass.getConstructors()) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length >= 7 && paramTypes[0] == int.class && paramTypes[1] == java.util.UUID.class) {
                try {
                    Object[] args = new Object[paramTypes.length];
                    args[0] = entityId;
                    args[1] = entityUuid;
                    args[2] = location.getX();
                    args[3] = location.getY();
                    args[4] = location.getZ();
                    args[5] = location.getPitch();
                    args[6] = location.getYaw();

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
        throw new NoSuchMethodException("Could not find suitable spawn packet constructor");
    }

    @Override
    public Object createMetadataPacket(int entityId, List<?> data) throws Exception {
        return packetPlayOutEntityMetadataClass.getConstructor(int.class, List.class).newInstance(entityId, data);
    }

    @Override
    public Object createDestroyPacket(int[] entityIds) throws Exception {
        return packetPlayOutEntityDestroyClass.getConstructor(int[].class).newInstance((Object) entityIds);
    }

    @Override
    public Object getArmorStandType() {
        return armorStandType;
    }

    @Override
    public @Nullable Object getTextDisplayType() {
        return textDisplayType;
    }

    @Override
    public Object createTextDisplay(Object worldServer, int entityId, java.util.UUID entityUuid, Location location) throws Exception {
        Class<?> textDisplayClass = Class.forName("net.minecraft.world.entity.Display$TextDisplay");
        Constructor<?> entityConstructor = textDisplayClass.getConstructor(entityTypesClass, Class.forName("net.minecraft.world.level.Level"));
        Object textDisplay = entityConstructor.newInstance(textDisplayType, worldServer);
        setEntityId(textDisplay, entityId);
        setEntityUuid(textDisplay, entityUuid);
        setEntityPos(textDisplay, location.getX(), location.getY(), location.getZ());
        setEntityRot(textDisplay, location.getYaw(), location.getPitch());
        return textDisplay;
    }

    @Override
    public Object createArmorStand(Object worldServer, Location location) throws Exception {
        Class<?> armorStandClass = Class.forName("net.minecraft.world.entity.decoration.ArmorStand");
        Constructor<?> entityConstructor = armorStandClass.getConstructor(Class.forName("net.minecraft.world.level.Level"), double.class, double.class, double.class);
        Object armorStand = entityConstructor.newInstance(worldServer, location.getX(), location.getY(), location.getZ());
        return armorStand;
    }

    @Override
    public void setEntityId(Object entity, int entityId) throws Exception {
        entity.getClass().getMethod("setId", int.class).invoke(entity, entityId);
    }

    @Override
    public void setEntityUuid(Object entity, java.util.UUID uuid) throws Exception {
        entity.getClass().getMethod("setUUID", java.util.UUID.class).invoke(entity, uuid);
    }

    @Override
    public void setEntityPos(Object entity, double x, double y, double z) throws Exception {
        entity.getClass().getMethod("setPos", double.class, double.class, double.class).invoke(entity, x, y, z);
    }

    @Override
    public void setEntityRot(Object entity, float yaw, float pitch) throws Exception {
        entity.getClass().getMethod("setRot", float.class, float.class).invoke(entity, yaw, pitch);
    }

    @Override
    public void setCustomName(Object entity, Object component) throws Exception {
        entity.getClass().getMethod("setCustomName", iChatBaseComponentClass).invoke(entity, component);
    }

    @Override
    public void setCustomNameVisible(Object entity, boolean visible) throws Exception {
        entity.getClass().getMethod("setCustomNameVisible", boolean.class).invoke(entity, visible);
    }

    @Override
    public void setInvisible(Object entity, boolean invisible) throws Exception {
        entity.getClass().getMethod("setInvisible", boolean.class).invoke(entity, invisible);
    }

    @Override
    public Object getEntityData(Object entity) throws Exception {
        return entity.getClass().getMethod("getEntityData").invoke(entity);
    }

    @Override
    public List<?> packAllEntityData(Object entityData) throws Exception {
        try {
            return (List<?>) synchedEntityDataClass.getMethod("packAll").invoke(entityData);
        } catch (NoSuchMethodException e) {
            return (List<?>) synchedEntityDataClass.getMethod("getNonDefaultValues").invoke(entityData);
        }
    }

    @Override
    public Object toNmsComponent(net.kyori.adventure.text.Component component) throws Exception {
        if (component == null) return null;
        String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component);
        Method fromJSON = craftChatMessageClass.getMethod("fromJSON", String.class);
        return fromJSON.invoke(null, json);
    }

    @Override
    public Object getWorldServer(org.bukkit.World world) throws Exception {
        Object craftWorld = craftWorldClass.cast(world);
        return craftWorldClass.getMethod("getHandle").invoke(craftWorld);
    }

    @Override
    public boolean supportsTextDisplay() {
        return NMSVersion.isModern();
    }

    @Override
    public void setTextDisplayText(Object textDisplay, Object component) throws Exception {
        try {
            textDisplay.getClass().getMethod("setText", iChatBaseComponentClass).invoke(textDisplay, component);
        } catch (NoSuchMethodException e) {
            Method method = findMethodByParams(textDisplay.getClass(), void.class, iChatBaseComponentClass);
            if (method != null) {
                method.invoke(textDisplay, component);
            }
        }
    }

    @Override
    public void setTextDisplayLineWidth(Object textDisplay, int width) throws Exception {
        try {
            textDisplay.getClass().getMethod("setLineWidth", int.class).invoke(textDisplay, width);
        } catch (NoSuchMethodException ignored) {
            for (Method method : textDisplay.getClass().getDeclaredMethods()) {
                if (method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == int.class
                        && method.getReturnType() == void.class) {
                    String mName = method.getName().toLowerCase();
                    if (!mName.contains("background") && !mName.contains("id") && !mName.contains("pos")) {
                        method.setAccessible(true);
                        method.invoke(textDisplay, width);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void setTextDisplayBackgroundColor(Object textDisplay, int color) throws Exception {
        try {
            textDisplay.getClass().getMethod("setBackgroundColor", int.class).invoke(textDisplay, color);
        } catch (NoSuchMethodException e) {
            for (Method m : textDisplay.getClass().getDeclaredMethods()) {
                if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == int.class && m.getName().toLowerCase().contains("background")) {
                    m.setAccessible(true);
                    m.invoke(textDisplay, color);
                    break;
                }
            }
        }
    }

    @Override
    public void setTextDisplayOpacity(Object textDisplay, byte opacity) throws Exception {
        try {
            textDisplay.getClass().getMethod("setTextOpacity", byte.class).invoke(textDisplay, opacity);
        } catch (NoSuchMethodException e) {
            Method method = findMethodByParams(textDisplay.getClass(), void.class, byte.class);
            if (method != null) {
                method.invoke(textDisplay, opacity);
            }
        }
    }

    @Override
    public void setTextDisplayFlags(Object textDisplay, byte flags) throws Exception {
        try {
            textDisplay.getClass().getMethod("setFlags", byte.class).invoke(textDisplay, flags);
        } catch (NoSuchMethodException e) {
            Method method = findMethodByParams(textDisplay.getClass(), void.class, byte.class);
            if (method != null) {
                method.invoke(textDisplay, flags);
            }
        }
    }

    @Override
    public void setTextDisplayBillboard(Object textDisplay, Object billboard) throws Exception {
        try {
            textDisplay.getClass().getMethod("setBillboardConstraints", billboard.getClass()).invoke(textDisplay, billboard);
        } catch (NoSuchMethodException e) {
            Method method = findMethodByParams(textDisplay.getClass(), void.class, billboard.getClass());
            if (method != null) {
                method.invoke(textDisplay, billboard);
            }
        }
    }

    @Override
    public Object getCenterBillboard() throws Exception {
        Class<?> billboardClass = Class.forName("net.minecraft.world.entity.Display$BillboardConstraints");
        return billboardClass.getField("CENTER").get(null);
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
}
