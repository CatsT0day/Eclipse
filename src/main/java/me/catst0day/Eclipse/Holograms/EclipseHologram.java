package me.catst0day.Eclipse.Holograms;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EclipseHologram {

    private final UUID id;
    private final Location baseLocation;
    private final List<String> rawLines;
    private final List<HoloLine> entityLines = new ArrayList<>();
    private static final double LINE_SPACING = 0.23;

    public EclipseHologram(UUID id, Location loc, List<String> textLines) {
        this.id = id;
        this.baseLocation = loc.clone();
        this.rawLines = textLines;

        Location current = baseLocation.clone();
        for (String text : textLines) {
            entityLines.add(new HoloLine(current, text));
            current.subtract(0, LINE_SPACING, 0);
        }
    }

    public void show(Player player) {
        entityLines.forEach(line -> line.sendSpawnPackets(player));
    }

    public void hide(Player player) {
        entityLines.forEach(line -> line.sendDestroyPacket(player));
    }

    public UUID getId() { return id; }
    public Location getBaseLocation() { return baseLocation; }
    public List<String> getRawLines() { return rawLines; }


    private static class HoloLine {
        private final ArmorStand nmsEntity;

        public HoloLine(Location loc, String text) {
            var world = ((CraftWorld) loc.getWorld()).getHandle();
            this.nmsEntity = new ArmorStand(EntityType.ARMOR_STAND, world);
            this.nmsEntity.setPos(loc.getX(), loc.getY(), loc.getZ());
            this.nmsEntity.setCustomName(new TextComponent(text));
            this.nmsEntity.setCustomNameVisible(true);
            this.nmsEntity.setInvisible(true);
            this.nmsEntity.setMarker(true);
            this.nmsEntity.setNoGravity(true);
        }

        public void sendSpawnPackets(Player player) {
            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            nmsPlayer.connection.send(new ClientboundAddMobPacket(nmsEntity));
            nmsPlayer.connection.send(new ClientboundSetEntityDataPacket(nmsEntity.getId(), nmsEntity.getEntityData(), true));
        }

        public void sendDestroyPacket(Player player) {
            ((CraftPlayer) player).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(nmsEntity.getId()));
        }
    }
}