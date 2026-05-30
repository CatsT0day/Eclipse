package me.catst0day.Eclipse.NMS;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;


public interface NMSHandler {

    
    String getVersion();

    
    void sendPacket(Player player, Object packet) throws Exception;

    
    Object createSpawnPacket(int entityId, UUID entityUuid, Location location, Object entityType) throws Exception;

    
    Object createMetadataPacket(int entityId, List<?> data) throws Exception;

    
    Object createDestroyPacket(int[] entityIds) throws Exception;

    
    Object getArmorStandType();

    
    @Nullable
    Object getTextDisplayType();

    
    Object createTextDisplay(Object worldServer, int entityId, UUID entityUuid, Location location) throws Exception;

    
    Object createArmorStand(Object worldServer, Location location) throws Exception;

    
    void setEntityId(Object entity, int entityId) throws Exception;

    
    void setEntityUuid(Object entity, UUID uuid) throws Exception;

    
    void setEntityPos(Object entity, double x, double y, double z) throws Exception;

    
    void setEntityRot(Object entity, float yaw, float pitch) throws Exception;

    
    void setCustomName(Object entity, Object component) throws Exception;

    
    void setCustomNameVisible(Object entity, boolean visible) throws Exception;

    
    void setInvisible(Object entity, boolean invisible) throws Exception;

    
    Object getEntityData(Object entity) throws Exception;

    
    List<?> packAllEntityData(Object entityData) throws Exception;

    
    Object toNmsComponent(net.kyori.adventure.text.Component component) throws Exception;

    
    Object getWorldServer(org.bukkit.World world) throws Exception;

    
    boolean supportsTextDisplay();

    
    void setTextDisplayText(Object textDisplay, Object component) throws Exception;

    
    void setTextDisplayLineWidth(Object textDisplay, int width) throws Exception;

    
    void setTextDisplayBackgroundColor(Object textDisplay, int color) throws Exception;

    
    void setTextDisplayOpacity(Object textDisplay, byte opacity) throws Exception;

    
    void setTextDisplayFlags(Object textDisplay, byte flags) throws Exception;

    
    void setTextDisplayBillboard(Object textDisplay, Object billboard) throws Exception;

    
    Object getCenterBillboard() throws Exception;
}
