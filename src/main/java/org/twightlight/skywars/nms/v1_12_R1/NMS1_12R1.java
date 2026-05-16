package org.twightlight.skywars.nms.v1_12_R1;

import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.nms.abstracts.BalloonEntity;
import org.twightlight.skywars.nms.abstracts.MapHelper;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.nms.abstracts.NMSBridge;
import org.twightlight.skywars.nms.v1_12_R1.entity.EntityArmorStand;
import org.twightlight.skywars.nms.v1_12_R1.entity.*;
import org.twightlight.skywars.nms.v1_12_R1.entity.EntityArmorStand.CraftArmorStand;
import org.twightlight.skywars.systems.holograms.Hologram;
import org.twightlight.skywars.systems.holograms.HologramLine;
import org.twightlight.skywars.systems.holograms.entity.IArmorStand;

import java.lang.reflect.Field;
import java.util.*;

public class NMS1_12R1 extends NMSBridge {
    private MapHelper mapHelper;

    public NMS1_12R1() {
        this.registerEntity(65, "LSW-Bat", BalloonEntityBat.class);
        this.registerEntity(8, "LSW-LeashKnot", BalloonEntityLeash.class);
        this.registerEntity(53, "LSW-Giant", BalloonEntityGiant.class);
        mapHelper = new org.twightlight.skywars.nms.v1_12_R1.sprays.MapHelper();

    }

    private void registerEntity(int entityId, String entityName, Class<? extends net.minecraft.server.v1_12_R1.Entity> entityClass) {
        MinecraftKey key = new MinecraftKey(entityName);
        EntityTypes.b.a(entityId, key, entityClass);
        if (!EntityTypes.d.contains(key)) {
            EntityTypes.d.add(key);
        }
    }

    @Override
    public IArmorStand createArmorStand(Location location, String name, HologramLine line) {
        IArmorStand armor = line == null ? new EntityStand(location) : new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), line);
        net.minecraft.server.v1_12_R1.Entity entity = (net.minecraft.server.v1_12_R1.Entity) armor;
        armor.setLocation(location.getX(), location.getY(), location.getZ());
        entity.yaw = location.getYaw();
        entity.pitch = location.getPitch();
        armor.setName(name);

        if (addEntity(entity)) {
            return armor;
        }

        return null;
    }

    @Override
    public BalloonEntity createBalloonLeash(Location location, List<UUID> viewers) {
        BalloonEntityLeash entity = new BalloonEntityLeash(location, viewers);

        if (!viewers.isEmpty()) {
            net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity packet = new net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity(entity, EntityTypes.b.a(entity.getClass()));
            net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata packet1 = new net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);

            viewers.forEach((UUID) -> {
                if (Bukkit.getPlayer(UUID).isOnline()) {
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet);
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet1);
                }
            });
            return entity;
        }

        if (addEntity(entity)) {
            return entity;
        }

        return null;
    }

    @Override
    public BalloonEntity createBalloonBat(Location location, BalloonEntity leash, List<UUID> viewers) {
        BalloonEntityBat entity = new BalloonEntityBat(location, (BalloonEntityLeash) leash, viewers);

        if (!viewers.isEmpty()) {
            net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving packet = new net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving(entity);
            net.minecraft.server.v1_12_R1.PacketPlayOutAttachEntity packet2 = new PacketPlayOutAttachEntity(entity, (BalloonEntityLeash) leash);
            net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata packet1 = new net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);

            viewers.forEach((UUID) -> {
                if (Bukkit.getPlayer(UUID).isOnline()) {
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet);
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet2);
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet1);

                }
            });
            return entity;
        }

        if (addEntity(entity)) {
            return entity;
        }

        return null;
    }

    @Override
    public BalloonEntity createBalloonGiant(Location location, List<String> frames, List<UUID> viewers) {
        BalloonEntityGiant entity = new BalloonEntityGiant(location, frames, viewers);

        if (!viewers.isEmpty()) {
            net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving packet = new net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving(entity);
            net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata packet1 = new net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);

            viewers.forEach((UUID) -> {
                if (Bukkit.getPlayer(UUID).isOnline()) {
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet);
                    entity.setFrame(0);
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet1);
                }
            });
            return entity;
        }

        if (addEntity(entity)) {
            return entity;
        }

        return null;
    }

    @Override
    public Hologram getHologram(Entity entity) {
        if (entity == null) {
            return null;
        }

        if (!(entity instanceof CraftArmorStand)) {
            return null;
        }

        net.minecraft.server.v1_12_R1.Entity en = ((CraftEntity) entity).getHandle();
        if (!(en instanceof EntityArmorStand)) {
            return null;
        }

        HologramLine e = ((EntityArmorStand) en).getLine();
        return e != null ? e.getHologram() : null;
    }

    @Override
    public boolean isHologramEntity(Entity entity) {
        return this.getHologram(entity) != null;
    }

    private boolean addEntity(net.minecraft.server.v1_12_R1.Entity entity) {
        try {
            return entity.world.addEntity(entity, SpawnReason.CUSTOM);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void playChestAction(Location location, boolean open) {
        BlockPosition pos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(pos, Blocks.ENDER_CHEST, 1, open ? 1 : 0);
        for (Player players : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"" + message + "\"}"), ChatMessageType.GAME_INFO);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle) {
        this.sendTitle(player, title, subtitle, 20, 60, 20);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.sendPacket(new PacketPlayOutTitle(fadeIn, stay, fadeOut));
        ep.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\": \"" + title + "\"}")));
        ep.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\": \"" + subtitle + "\"}")));
    }

    @Override
    public void sendTabHeaderFooter(Player player, String header, String footer) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet, ChatSerializer.a("{\"text\": \"" + header + "\"}"));
            Field b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);
            b.set(packet, ChatSerializer.a("{\"text\": \"" + footer + "\"}"));
        } catch (ReflectiveOperationException e) {
            NMS.LOGGER.log(Level.WARNING, "Unexpected error on sendTabHeaderFooter(player, header, footer): ", e);
        }

        ep.playerConnection.sendPacket(packet);
    }

    public MapHelper getMapHelper() {
        return mapHelper;
    }

    public int getIdOfEntity(Entity entity) {
        return ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity) entity).getHandle().getId();
    }

    public void sendChunksAroundLocation(Player player, Location loc,
                                          int radius) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        int centerX = loc.getBlockX() >> 4;
        int centerZ = loc.getBlockZ() >> 4;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int chunkX = centerX + x;
                int chunkZ = centerZ + z;

                // Make sure the chunk is loaded server-side
                World world = ((CraftWorld) loc.getWorld()).getHandle();
                Chunk nmsChunk =
                        world.getChunkAt(chunkX, chunkZ);

                // Send full chunk data with ALL sections (bitmask 65535)
                PacketPlayOutMapChunk chunkPacket =
                        new PacketPlayOutMapChunk(nmsChunk, 65535);
                nmsPlayer.playerConnection.sendPacket(chunkPacket);
            }
        }
    }

    public void unloadChunksAroundLocation(Player player, Location loc,
                                            int radius) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        int centerX = loc.getBlockX() >> 4;
        int centerZ = loc.getBlockZ() >> 4;

        // Get the chunks that the player SHOULD have loaded
        // (around their actual position)
        Set<Long> playerChunks = getPlayerChunkSet(player,
                player.getLocation(), Bukkit.getViewDistance());

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int chunkX = centerX + x;
                int chunkZ = centerZ + z;
                long key = chunkKey(chunkX, chunkZ);

                // Only unload chunks that the player shouldn't
                // normally have loaded (don't unload overlapping chunks)
                if (!playerChunks.contains(key)) {
                    // In 1.8, sending a MapChunk with groundUp=true
                    // and bitmask=0 effectively unloads the chunk
                    World world =
                            ((CraftWorld) loc.getWorld()).getHandle();
                    Chunk nmsChunk =
                            world.getChunkAt(chunkX, chunkZ);
                    PacketPlayOutMapChunk unloadPacket = new PacketPlayOutMapChunk(nmsChunk, 0);
                    nmsPlayer.playerConnection.sendPacket(unloadPacket);
                }
            }
        }
    }

    private Set<Long> getPlayerChunkSet(Player player, Location loc,
                                        int viewDistance) {
        Set<Long> chunks = new HashSet<>();
        int centerX = loc.getBlockX() >> 4;
        int centerZ = loc.getBlockZ() >> 4;
        for (int x = -viewDistance; x <= viewDistance; x++) {
            for (int z = -viewDistance; z <= viewDistance; z++) {
                chunks.add(chunkKey(centerX + x, centerZ + z));
            }
        }
        return chunks;
    }

    private long chunkKey(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    @SuppressWarnings("unchecked")
    public void resyncTrackedEntities(org.bukkit.entity.Player player) {
        try {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        WorldServer world = (WorldServer) entityPlayer.world;
        EntityTracker tracker = world.tracker;

        Field field = NMS.findField(EntityTracker.class, "c");
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        Set<EntityTrackerEntry> entries = (Set<EntityTrackerEntry>) field.get(tracker);
        for (EntityTrackerEntry entry : entries) {
            Field tracker1 = NMS.findField(EntityTrackerEntry.class, "tracker");
            if (!tracker1.isAccessible()) {
                tracker1.setAccessible(true);
            }

            net.minecraft.server.v1_12_R1.Entity entity = (net.minecraft.server.v1_12_R1.Entity) tracker1.get(entry);

            if (entity == entityPlayer) continue;

            entry.clear(entityPlayer);

            entry.trackedPlayers.remove(entityPlayer);

            entry.scanPlayers(Arrays.asList(entityPlayer));
        }
        } catch (IllegalAccessException | NullPointerException e) {
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("unchecked")
    public void resyncEntity(Player player, Entity entity) {

        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        WorldServer world = ((CraftWorld) entity.getWorld()).getHandle();
        EntityTracker tracker = world.tracker;

        try {

            IntHashMap<EntityTrackerEntry> map = null;

            for (Field f : EntityTracker.class.getDeclaredFields()) {
                if (f.getType().equals(IntHashMap.class)) {
                    f.setAccessible(true);
                    map = (IntHashMap<EntityTrackerEntry>) f.get(tracker);
                    break;
                }
            }

            if (map == null) return;

            EntityTrackerEntry entry = map.get(entity.getEntityId());
            if (entry == null) return;

            entry.trackedPlayers.remove(ep);
            entry.scanPlayers(Collections.singletonList(ep));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
