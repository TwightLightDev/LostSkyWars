package org.twightlight.skywars.nms.v1_12_R1.sprays;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;
import org.twightlight.skywars.nms.NMS;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MapHelper extends org.twightlight.skywars.nms.abstracts.MapHelper {
    static final int DEFAULT_STARTING_ID = 8000;

    private static final Field ENTITY_ID = NMS.findField(Entity.class, "id");
    private static final Map<UUID, AtomicInteger> MAP_IDS = new HashMap<>(4);


    protected int nextMapId(World world) {
        return MAP_IDS.computeIfAbsent(world.getWorld().getUID(), __ ->
                new AtomicInteger(DEFAULT_STARTING_ID)).getAndIncrement();
    }

    public void createMap(int frameId, List<Player> players, Location location, BlockFace direction, BufferedImage image) {
        byte[] pixels = createPixels(image);
        ItemStack item = new ItemStack(Items.FILLED_MAP);
        int mapId = nextMapId(((CraftWorld) location.getWorld()).getHandle());
        item.setData(mapId);
        EntityItemFrame frame = new EntityItemFrame(((CraftWorld)location.getWorld()).getHandle());
        frame.setItem(item);
        frame.setSilent(true);
        frame.setDirection(CraftBlock.blockFaceToNotch(direction));
        NMS.setFieldValue(ENTITY_ID, frame, Integer.valueOf(frameId));
        players.forEach((player -> {
            PlayerConnection connection = (((CraftPlayer)player).getHandle()).playerConnection;
            connection.sendPacket(new PacketPlayOutSpawnEntity(frame, 71, frame.direction
                    .get2DRotationValue(), frame.getBlockPosition()));
            connection.sendPacket(new PacketPlayOutEntityMetadata(frame.getId(), frame.getDataWatcher(), true));
            connection.sendPacket(new PacketPlayOutMap(mapId, (byte)3, false, Collections.emptyList(), pixels, 0, 0, 128, 128));
        }));
    }

    public void destroyMap(List<Player> players, int[] frameIds) {
        players.forEach((player -> {
            (((CraftPlayer)player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityDestroy(frameIds));
        }));
    }

    public byte[] createPixels(BufferedImage image) {
        int pixelCount = image.getWidth() * image.getHeight();
        int[] pixels = new int[pixelCount];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        byte[] colors = new byte[pixelCount];
        for (int i = 0; i < pixelCount; i++)
            colors[i] = MapPalette.matchColor(new Color(pixels[i], true));
        return colors;
    }

    private void setLocation(EntityItemFrame entity, double x, double y, double z) {
        entity.locX = MathHelper.a(x, -3.0E7D, 3.0E7D);
        entity.locY = y;
        entity.locZ = MathHelper.a(z, -3.0E7D, 3.0E7D);
        entity.lastX = entity.locX;
        entity.lastY = entity.locY;
        entity.lastZ = entity.locZ;
        entity.yaw = 0.0F;
        entity.pitch = 0.0F;
        entity.lastYaw = entity.yaw;
        entity.lastPitch = entity.pitch;
        double yawDiff = (entity.lastYaw - 0.0F);
        if (yawDiff < -180.0D)
            entity.lastYaw += 360.0F;
        if (yawDiff >= 180.0D)
            entity.lastYaw -= 360.0F;
        entity.setPosition(entity.locX, entity.locY, entity.locZ);
    }
}
