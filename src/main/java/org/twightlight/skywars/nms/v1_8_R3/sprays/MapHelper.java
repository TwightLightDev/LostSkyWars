package org.twightlight.skywars.nms.v1_8_R3.sprays;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutMap;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.twightlight.skywars.nms.NMS;

public class MapHelper extends org.twightlight.skywars.nms.MapHelper {
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
        frame.setLocation(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
        frame.setDirection(CraftBlock.blockFaceToNotch(direction));
        NMS.setFieldValue(ENTITY_ID, frame, Integer.valueOf(frameId));
        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(frame, 71, frame.direction.b());
        BlockPosition position = frame.getBlockPosition();
        spawnPacket.a(MathHelper.d((position.getX() * 32)));
        spawnPacket.b(MathHelper.d((position.getY() * 32)));
        spawnPacket.c(MathHelper.d((position.getZ() * 32)));
        players.forEach((player -> {
            PlayerConnection connection = (((CraftPlayer)player).getHandle()).playerConnection;
            connection.sendPacket(spawnPacket);
            connection.sendPacket(new PacketPlayOutEntityMetadata(frame.getId(), frame.getDataWatcher(), true));
            connection.sendPacket(new PacketPlayOutMap(mapId, (byte)3, Collections.emptyList(), pixels, 0, 0, 128, 128));

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
}
