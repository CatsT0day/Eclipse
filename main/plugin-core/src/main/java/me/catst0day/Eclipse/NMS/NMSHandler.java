package me.catst0day.Eclipse.NMS;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Interface for NMS (Net Minecraft Server) operations.
 * Implementations handle version-specific Minecraft server internals.
 */
public interface NMSHandler {

    /**
     * Gets the version string this handler supports.
     */
    String getVersion();

    /**
     * Sends a packet to a player.
     */
    void sendPacket(Player player, Object packet) throws Exception;

    /**
     * Creates a spawn entity packet.
     */
    Object createSpawnPacket(int entityId, UUID entityUuid, Location location, Object entityType) throws Exception;

    /**
     * Creates an entity metadata packet.
     */
    Object createMetadataPacket(int entityId, List<?> data) throws Exception;

    /**
     * Creates an entity destroy packet.
     */
    Object createDestroyPacket(int[] entityIds) throws Exception;

    /**
     * Gets the Armor Stand entity type.
     */
    Object getArmorStandType();

    /**
     * Gets the Text Display entity type (null if not supported).
     */
    @Nullable
    Object getTextDisplayType();

    /**
     * Creates a Text Display entity.
     */
    Object createTextDisplay(Object worldServer, int entityId, UUID entityUuid, Location location) throws Exception;

    /**
     * Creates an Armor Stand entity.
     */
    Object createArmorStand(Object worldServer, Location location) throws Exception;

    /**
     * Sets entity ID.
     */
    void setEntityId(Object entity, int entityId) throws Exception;

    /**
     * Sets entity UUID.
     */
    void setEntityUuid(Object entity, UUID uuid) throws Exception;

    /**
     * Sets entity position.
     */
    void setEntityPos(Object entity, double x, double y, double z) throws Exception;

    /**
     * Sets entity rotation.
     */
    void setEntityRot(Object entity, float yaw, float pitch) throws Exception;

    /**
     * Sets entity custom name.
     */
    void setCustomName(Object entity, Object component) throws Exception;

    /**
     * Sets custom name visible.
     */
    void setCustomNameVisible(Object entity, boolean visible) throws Exception;

    /**
     * Sets invisible.
     */
    void setInvisible(Object entity, boolean invisible) throws Exception;

    /**
     * Gets entity data.
     */
    Object getEntityData(Object entity) throws Exception;

    /**
     * Packs all entity data.
     */
    List<?> packAllEntityData(Object entityData) throws Exception;

    /**
     * Converts Adventure component to NMS component.
     */
    Object toNmsComponent(net.kyori.adventure.text.Component component) throws Exception;

    /**
     * Gets the world server handle from a CraftWorld.
     */
    Object getWorldServer(org.bukkit.World world) throws Exception;

    /**
     * Checks if this version supports Text Display entities (1.19+).
     */
    boolean supportsTextDisplay();

    /**
     * Sets text on Text Display entity.
     */
    void setTextDisplayText(Object textDisplay, Object component) throws Exception;

    /**
     * Sets line width on Text Display entity.
     */
    void setTextDisplayLineWidth(Object textDisplay, int width) throws Exception;

    /**
     * Sets background color on Text Display entity.
     */
    void setTextDisplayBackgroundColor(Object textDisplay, int color) throws Exception;

    /**
     * Sets text opacity on Text Display entity.
     */
    void setTextDisplayOpacity(Object textDisplay, byte opacity) throws Exception;

    /**
     * Sets flags on Text Display entity.
     */
    void setTextDisplayFlags(Object textDisplay, byte flags) throws Exception;

    /**
     * Sets billboard constraints on Text Display entity.
     */
    void setTextDisplayBillboard(Object textDisplay, Object billboard) throws Exception;

    /**
     * Gets the CENTER billboard constraint.
     */
    Object getCenterBillboard() throws Exception;
}
