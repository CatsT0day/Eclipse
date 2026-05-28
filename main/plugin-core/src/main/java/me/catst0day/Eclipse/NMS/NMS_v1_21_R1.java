package me.catst0day.Eclipse.NMS;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * NMS handler for Minecraft 1.21.1 (v1_21_R1).
 * Uses direct NMS calls without reflection for better performance and stability.
 */
public class NMS_v1_21_R1 implements NMSHandler {

    @Override
    public String getVersion() {
        return "v1_21_R1";
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().connection.send((net.minecraft.network.protocol.Packet<?>) packet);
    }

    @Override
    public Object createSpawnPacket(int entityId, UUID entityUuid, Location location, Object entityType) {
        try {
            Object worldServer = getWorldServer(location.getWorld());
            Object entity;
            if (entityType == EntityType.TEXT_DISPLAY) {
                entity = createTextDisplay(worldServer, entityId, entityUuid, location);
            } else {
                entity = createArmorStand(worldServer, location);
                setEntityId(entity, entityId);
                setEntityUuid(entity, entityUuid);
            }
            net.minecraft.server.level.ServerLevel serverLevel = (net.minecraft.server.level.ServerLevel) worldServer;
            net.minecraft.server.level.ServerEntity serverEntity = new net.minecraft.server.level.ServerEntity(serverLevel, ((net.minecraft.world.entity.Entity) entity), 0, false, packet -> {}, java.util.Collections.emptySet());
            return ((net.minecraft.world.entity.Entity) entity).getAddEntityPacket(serverEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object createMetadataPacket(int entityId, List<?> data) {
        return new ClientboundSetEntityDataPacket(entityId, (List<SynchedEntityData.DataValue<?>>) data);
    }

    @Override
    public Object createDestroyPacket(int[] entityIds) {
        return new ClientboundRemoveEntitiesPacket(entityIds);
    }

    @Override
    public Object getArmorStandType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public Object getTextDisplayType() {
        return EntityType.TEXT_DISPLAY;
    }

    @Override
    public Object createTextDisplay(Object worldServer, int entityId, UUID entityUuid, Location location) {
        Level level = (Level) worldServer;
        Display.TextDisplay textDisplay = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        textDisplay.setId(entityId);
        textDisplay.setUUID(entityUuid);
        textDisplay.setPos(location.getX(), location.getY(), location.getZ());
        textDisplay.setRot(location.getYaw(), location.getPitch());
        return textDisplay;
    }

    @Override
    public Object createArmorStand(Object worldServer, Location location) {
        Level level = (Level) worldServer;
        return new ArmorStand(level, location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void setEntityId(Object entity, int entityId) {
        ((net.minecraft.world.entity.Entity) entity).setId(entityId);
    }

    @Override
    public void setEntityUuid(Object entity, UUID uuid) {
        ((net.minecraft.world.entity.Entity) entity).setUUID(uuid);
    }

    @Override
    public void setEntityPos(Object entity, double x, double y, double z) {
        ((net.minecraft.world.entity.Entity) entity).setPos(x, y, z);
    }

    @Override
    public void setEntityRot(Object entity, float yaw, float pitch) {
        ((net.minecraft.world.entity.Entity) entity).setRot(yaw, pitch);
    }

    @Override
    public void setCustomName(Object entity, Object component) {
        ((net.minecraft.world.entity.Entity) entity).setCustomName((net.minecraft.network.chat.Component) component);
    }

    @Override
    public void setCustomNameVisible(Object entity, boolean visible) {
        ((net.minecraft.world.entity.Entity) entity).setCustomNameVisible(visible);
    }

    @Override
    public void setInvisible(Object entity, boolean invisible) {
        ((net.minecraft.world.entity.Entity) entity).setInvisible(invisible);
    }

    @Override
    public Object getEntityData(Object entity) {
        return ((net.minecraft.world.entity.Entity) entity).getEntityData();
    }

    @Override
    public List<?> packAllEntityData(Object entityData) {
        return ((SynchedEntityData) entityData).packAll();
    }

    @Override
    public Object toNmsComponent(net.kyori.adventure.text.Component component) {
        if (component == null) return null;
        String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component);
        return CraftChatMessage.fromJSON(json);
    }

    @Override
    public Object getWorldServer(org.bukkit.World world) {
        return ((CraftWorld) world).getHandle();
    }

    @Override
    public boolean supportsTextDisplay() {
        return true;
    }

    @Override
    public void setTextDisplayText(Object textDisplay, Object component) {
        ((Display.TextDisplay) textDisplay).setText((net.minecraft.network.chat.Component) component);
    }

    @Override
    public void setTextDisplayLineWidth(Object textDisplay, int width) {
        try {
            java.lang.reflect.Method method = textDisplay.getClass().getDeclaredMethod("setLineWidth", int.class);
            method.setAccessible(true);
            method.invoke(textDisplay, width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTextDisplayBackgroundColor(Object textDisplay, int color) {
        try {
            java.lang.reflect.Method method = textDisplay.getClass().getDeclaredMethod("setBackgroundColor", int.class);
            method.setAccessible(true);
            method.invoke(textDisplay, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTextDisplayOpacity(Object textDisplay, byte opacity) {
        ((Display.TextDisplay) textDisplay).setTextOpacity(opacity);
    }

    @Override
    public void setTextDisplayFlags(Object textDisplay, byte flags) {
        ((Display.TextDisplay) textDisplay).setFlags(flags);
    }

    @Override
    public void setTextDisplayBillboard(Object textDisplay, Object billboard) {
        ((Display.TextDisplay) textDisplay).setBillboardConstraints((Display.BillboardConstraints) billboard);
    }

    @Override
    public Object getCenterBillboard() {
        return Display.BillboardConstraints.CENTER;
    }
}
