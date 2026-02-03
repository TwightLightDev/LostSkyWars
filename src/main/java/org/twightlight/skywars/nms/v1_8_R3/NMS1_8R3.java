package org.twightlight.skywars.nms.v1_8_R3;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.nms.BalloonEntity;
import org.twightlight.skywars.nms.MapHelper;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.nms.NMSBridge;
import org.twightlight.skywars.nms.v1_8_R3.entity.EntityArmorStand;
import org.twightlight.skywars.nms.v1_8_R3.entity.*;
import org.twightlight.skywars.nms.v1_8_R3.entity.EntityArmorStand.CraftArmorStand;
import org.twightlight.skywars.systems.holograms.Hologram;
import org.twightlight.skywars.systems.holograms.HologramLine;
import org.twightlight.skywars.systems.holograms.entity.IArmorStand;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NMS1_8R3 extends NMSBridge {

    private MapHelper mapHelper;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public NMS1_8R3() {
        try {
            Field d = EntityTypes.class.getDeclaredField("d");
            d.setAccessible(true);
            Map map = (Map) d.get(null);
            map.put(EntityArmorStand.class, "ArmorStand");
            map.put(EntityStand.class, "ArmorStand");

            map.put(BalloonEntityBat.class, "LSW-Bat");
            map.put(BalloonEntityLeash.class, "LSW-LeashKnot");
            map.put(BalloonEntityGiant.class, "LSW-Giant");

            Field f = EntityTypes.class.getDeclaredField("f");
            f.setAccessible(true);
            map = (Map) f.get(null);
            map.put(EntityArmorStand.class, 30);
            map.put(EntityStand.class, 30);

            map.put(BalloonEntityBat.class, 65);
            map.put(BalloonEntityLeash.class, 8);
            map.put(BalloonEntityGiant.class, 53);

            mapHelper = new org.twightlight.skywars.nms.v1_8_R3.sprays.MapHelper();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public BalloonEntity createBalloonLeash(Location location, List<UUID> viewers) {
        BalloonEntityLeash entity = new BalloonEntityLeash(location, viewers);

        if (!viewers.isEmpty()) {
            int entityID;
            if (SkyWars.packetevents) {
                entityID = com.github.retrooper.packetevents.protocol.entity.type.EntityTypes.LEASH_KNOT.getLegacyId(ClientVersion.V_1_8);
            } else {
                entityID = EntityTypes.a(entity);
            }

            PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity, entityID);
            PacketPlayOutEntityMetadata packet1 = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);

            viewers.forEach((UUID) -> {
                if (Bukkit.getPlayer(UUID).isOnline()) {
                    ((CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet);
                    ((CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet1);

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
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
            PacketPlayOutEntityMetadata packet1 = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);
            PacketPlayOutAttachEntity packet2 = new PacketPlayOutAttachEntity(1, entity, (BalloonEntityLeash) leash);
            viewers.forEach((UUID) -> {
                if (Bukkit.getPlayer(UUID).isOnline()) {
                    ((CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet);
                    ((CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet2);
                    ((CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet1);

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
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
            PacketPlayOutEntityMetadata packet1 = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);

            viewers.forEach((UUID) -> {
                if (Bukkit.getPlayer(UUID).isOnline()) {
                    ((CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet);
                    entity.setFrame(0);
                    ((CraftPlayer) Bukkit.getPlayer(UUID)).getHandle().playerConnection.sendPacket(packet1);

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
    public IArmorStand createArmorStand(Location location, String name, HologramLine line) {
        IArmorStand armor = line == null ? new EntityStand(location)
                : new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), line);
        net.minecraft.server.v1_8_R3.Entity entity = (net.minecraft.server.v1_8_R3.Entity) armor;
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
    public Hologram getHologram(Entity entity) {
        if (entity == null) {
            return null;
        }

        if (!(entity instanceof CraftArmorStand)) {
            return null;
        }

        net.minecraft.server.v1_8_R3.Entity en = ((CraftEntity) entity).getHandle();
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

    private boolean addEntity(net.minecraft.server.v1_8_R3.Entity entity) {
        try {
            return entity.world.addEntity(entity, SpawnReason.CUSTOM);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void playChestAction(Location location, boolean open) {
        BlockPosition pos =
                new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        PacketPlayOutBlockAction packet =
                new PacketPlayOutBlockAction(pos, Blocks.ENDER_CHEST, 1, open ? 1 : 0);
        for (Player players : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"" + message + "\"}"), (byte) 2);
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
        ep.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE,
                ChatSerializer.a("{\"text\": \"" + title + "\"}")));
        ep.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
                ChatSerializer.a("{\"text\": \"" + subtitle + "\"}")));
    }

    @Override
    public void sendTabHeaderFooter(Player player, String header, String footer) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();

        PacketPlayOutPlayerListHeaderFooter packet =
                new PacketPlayOutPlayerListHeaderFooter(ChatSerializer.a("{\"text\": \"" + header + "\"}"));
        try {
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
        return ((CraftEntity) entity).getHandle().getId();
    }

}
