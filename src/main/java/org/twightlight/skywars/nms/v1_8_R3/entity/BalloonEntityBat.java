package org.twightlight.skywars.nms.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.twightlight.skywars.nms.BalloonEntity;

import java.util.List;
import java.util.UUID;

public class BalloonEntityBat extends EntityBat implements BalloonEntity {
    private List<UUID> viewers;

    public BalloonEntityBat(Location location, BalloonEntityLeash leash, List<UUID> viewers) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.viewers = viewers;

        super.setInvisible(true);
        this.setLeashHolder(leash, true);

        this.setPosition(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void kill() {
        if (!viewers.isEmpty()) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(getId());

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

    @Override
    public void t_() {
    }

    @Override
    public void makeSound(String s, float f, float f1) {
    }

    @Override
    protected boolean a(EntityHuman entityhuman) {
        return false;
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
    public boolean d(int i, ItemStack itemstack) {
        return false;
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
