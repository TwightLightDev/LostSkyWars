package org.twightlight.skywars.nms.v1_12_R1.entity;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.twightlight.skywars.nms.abstracts.BalloonEntity;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;

import java.util.List;
import java.util.UUID;

public class BalloonEntityGiant extends EntityGiantZombie implements BalloonEntity {

    private Location location;
    private List<String> frames;
    private List<UUID> viewers;

    public BalloonEntityGiant(Location location, List<String> frames, List<UUID> viewers) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.frames = frames;
        this.location = location.clone();
        this.viewers = viewers;
        super.setInvisible(true);
        this.setPosition(location.getX(), location.getY(), location.getZ());
        setFrame(0);
    }

    public void setFrame(int index) {
        if (index > frames.size() - 1) {
            return;
        }

        if (!viewers.isEmpty()) {
            net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment entityEquipment = new net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment(getId(), EnumItemSlot.MAINHAND, org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : skinvalue=" + this.frames.get(index))));

            viewers.forEach((UUID) -> {
                Player player = Bukkit.getPlayer(UUID);

                if (player.isOnline()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(entityEquipment);
                }

            });
        } else {
            this.setEquipment(EnumItemSlot.MAINHAND, org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : skinvalue=" + this.frames.get(index))));
        }
    }

    @Override
    public void kill() {
        if (!viewers.isEmpty()) {
            net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy packet = new net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy(getId());

            viewers.forEach((UUID) -> {
                Player player = Bukkit.getPlayer(UUID);

                if (player.isOnline()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
            });
            return;
        }
        this.dead = true;
    }

    private int frame = 0;

    @Override
    public void B_() {
        this.motY = 0.0;
        this.setPosition(location.getX(), location.getY(), location.getZ());
        if (this.frames == null) {
            this.kill();
            return;
        }

        if (this.frame >= this.frames.size()) {
            this.frame = 0;
        }

        super.B_();
        if (MinecraftServer.currentTick % 10 == 0) {

            if (!viewers.isEmpty()) {
                net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment entityEquipment = new net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment(getId(), EnumItemSlot.MAINHAND, org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : skinvalue=" + this.frames.get(this.frame++))));

                viewers.forEach((UUID) -> {
                    Player player = Bukkit.getPlayer(UUID);

                    if (player.isOnline()) {
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(entityEquipment);
                    }

                });
            } else {
                this.setEquipment(EnumItemSlot.MAINHAND, org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : skinvalue=" + this.frames.get(this.frame++))));
            }
        }
    }

    @Override
    public boolean isInvulnerable(DamageSource damagesource) {
        return true;
    }

    @Override
    public void setCustomName(String s) {
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
    }

    @Override
    public void die() {
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void setInvisible(boolean flag) {
    }

    public void a(NBTTagCompound nbttagcompound) {
    }

    public void b(NBTTagCompound nbttagcompound) {
    }

    public boolean c(NBTTagCompound nbttagcompound) {
        return false;
    }

    public boolean d(NBTTagCompound nbttagcompound) {
        return false;
    }

    public void e(NBTTagCompound nbttagcompound) {
    }

    public void f(NBTTagCompound nbttagcompound) {
    }
}
