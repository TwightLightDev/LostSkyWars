package org.twightlight.skywars.nms.v1_12_R1.entity;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.EulerAngle;
import org.twightlight.skywars.holograms.HologramLine;
import org.twightlight.skywars.holograms.entity.IArmorStand;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class EntityArmorStand extends net.minecraft.server.v1_12_R1.EntityArmorStand implements IArmorStand {

    private HologramLine line;

    public EntityArmorStand(World world, HologramLine line) {
        super(world);
        setInvisible(true);
        setSmall(true);
        setArms(false);
        setNoGravity(true);
        setBasePlate(true);
        this.line = line;
        a(new NullBoundingBox());
    }

    public void b(NBTTagCompound nbttagcompound) {
    }

    public boolean c(NBTTagCompound nbttagcompound) {
        return false;
    }

    public boolean d(NBTTagCompound nbttagcompound) {
        return false;
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        return nbttagcompound;
    }

    @Override
    public void f(NBTTagCompound nbttagcompound) {
    }

    public void a(NBTTagCompound nbttagcompound) {
    }

    public boolean isInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public EnumInteractionResult a(EntityHuman human, Vec3D vec3d, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    @Override
    public boolean c(int i, ItemStack item) {
        return false;
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
    }

    public void setCustomName(String customName) {
    }

    public void setCustomNameVisible(boolean visible) {
    }

    public boolean a(EntityHuman human, Vec3D vec3d) {
        return true;
    }

    public void B_() {
        this.ticksLived = 0;
        if (dead) {
            super.B_();
        }
    }

    public void makeSound(String sound, float f1, float f2) {
    }

    public CraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = new CraftArmorStand(world.getServer(), this);
        }
        return this.bukkitEntity;
    }

    public void die() {
        super.die();
    }

    @Override
    public void setLocation(double x, double y, double z) {
        super.setPosition(x, y, z);

        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(this);

        for (EntityHuman obj : world.players) {
            if (obj instanceof EntityPlayer) {
                EntityPlayer nmsPlayer = (EntityPlayer) obj;

                double distanceSquared = square(nmsPlayer.locX - this.locX) + square(nmsPlayer.locZ - this.locZ);
                if (distanceSquared < 8192.0 && nmsPlayer.playerConnection != null) {
                    nmsPlayer.playerConnection.sendPacket(teleportPacket);
                }
            }
        }
    }

    private static double square(double num) {
        return num * num;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public void killEntity() {
        die();
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void setName(String name) {
        if (name != null && name.length() > 300) {
            name = name.substring(0, 300);
        }
        super.setCustomName(name);
        super.setCustomNameVisible(name != null && !name.equals(""));
    }

    @Override
    public ArmorStand getEntity() {
        return (ArmorStand) getBukkitEntity();
    }

    @Override
    public HologramLine getLine() {
        return line;
    }

    public static class CraftArmorStand extends org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand implements IArmorStand {

        public CraftArmorStand(CraftServer server, EntityArmorStand entity) {
            super(server, entity);
        }

        @Override
        public int getId() {
            return ((EntityArmorStand) entity).getId();
        }

        @Override
        public void setName(String text) {
            ((EntityArmorStand) entity).setName(text);
        }

        @Override
        public void killEntity() {
            ((EntityArmorStand) entity).killEntity();
        }

        @Override
        public HologramLine getLine() {
            return ((EntityArmorStand) entity).getLine();
        }

        @Override
        public ArmorStand getEntity() {
            return (ArmorStand) this;
        }

        @Override
        public void setLocation(double x, double y, double z) {
            ((EntityArmorStand) entity).setLocation(x, y, z);
        }

        public void remove() {
        }

        public void setArms(boolean arms) {
        }

        public void setBasePlate(boolean basePlate) {
        }

        public void setBodyPose(EulerAngle pose) {
        }

        public void setGravity(boolean gravity) {
        }

        public void setHeadPose(EulerAngle pose) {
        }

        public void setLeftArmPose(EulerAngle pose) {
        }

        public void setLeftLegPose(EulerAngle pose) {
        }

        public void setRightArmPose(EulerAngle pose) {
        }

        public void setRightLegPose(EulerAngle pose) {
        }

        public void setSmall(boolean small) {
        }

        public void setVisible(boolean visible) {
        }

        @Override
        public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i) {
            return Collections.emptyList();
        }

        @Override
        public Block getTargetBlock(HashSet<Byte> hashSet, int i) {
            return null;
        }

        @Override
        public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i) {
            return Collections.emptyList();
        }

        @Override
        public Egg throwEgg() {
            return null;
        }

        @Override
        public Snowball throwSnowball() {
            return null;
        }

        @Override
        public Arrow shootArrow() {
            return null;
        }

        @Override
        public int _INVALID_getLastDamage() {
            return 0;
        }

        @Override
        public void _INVALID_setLastDamage(int i) {

        }

        public boolean addPotionEffect(PotionEffect effect) {
            return false;
        }

        public boolean addPotionEffect(PotionEffect effect, boolean param) {
            return false;
        }

        public boolean addPotionEffects(Collection<PotionEffect> effects) {
            return false;
        }

        public void setRemoveWhenFarAway(boolean remove) {
        }

        public boolean teleport(Location loc) {
            return false;
        }

        public boolean teleport(Entity entity) {
            return false;
        }

        public boolean teleport(Location loc, PlayerTeleportEvent.TeleportCause cause) {
            return false;
        }

        public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause cause) {
            return false;
        }

        public void setFireTicks(int ticks) {
        }

        public boolean setPassenger(Entity entity) {
            return false;
        }

        public boolean eject() {
            return false;
        }

        public boolean leaveVehicle() {
            return false;
        }

        public void playEffect(EntityEffect effect) {
        }

        public void setCustomName(String name) {
        }

        public void setCustomNameVisible(boolean flag) {
        }

        @Override
        public void _INVALID_damage(int i) {

        }

        @Override
        public void _INVALID_damage(int i, Entity entity) {

        }

        @Override
        public int _INVALID_getHealth() {
            return 0;
        }

        @Override
        public void _INVALID_setHealth(int i) {

        }

        @Override
        public int _INVALID_getMaxHealth() {
            return 0;
        }

        @Override
        public void _INVALID_setMaxHealth(int i) {

        }
    }
}
