package org.twightlight.skywars.nms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.systems.holograms.Hologram;
import org.twightlight.skywars.systems.holograms.HologramLine;
import org.twightlight.skywars.systems.holograms.entity.IArmorStand;
import org.twightlight.skywars.nms.v1_12_R1.NMS1_12R1;
import org.twightlight.skywars.nms.v1_8_R3.NMS1_8R3;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.utils.MinecraftVersion;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class NMS {

    private static NMSBridge BRIDGE;
    public static final Logger LOGGER = SkyWars.LOGGER.getModule("NMS");

    public static IArmorStand createArmorStand(Location location, String name, HologramLine line) {
        return BRIDGE.createArmorStand(location, name, line);
    }

    public static BalloonEntity createBalloonLeash(Location location) {
        return BRIDGE.createBalloonLeash(location, Collections.emptyList());
    }

    public static BalloonEntity createBalloonBat(Location location, BalloonEntity leash) {
        return BRIDGE.createBalloonBat(location, leash, Collections.emptyList());
    }

    public static BalloonEntity createBalloonLeash(Location location, List<UUID> viewers) {
        return BRIDGE.createBalloonLeash(location, viewers);
    }

    public static BalloonEntity createBalloonBat(Location location, BalloonEntity leash, List<UUID> viewers) {
        return BRIDGE.createBalloonBat(location, leash, viewers);
    }

    public static BalloonEntity createBalloonGiant(Location location, List<String> frames) {
        return BRIDGE.createBalloonGiant(location, frames, Collections.emptyList());
    }

    public static BalloonEntity createBalloonGiant(Location location, List<String> frames, List<UUID> viewers) {
        return BRIDGE.createBalloonGiant(location, frames, viewers);
    }

    public static Hologram getHologram(Entity entity) {
        return BRIDGE.getHologram(entity);
    }

    public static boolean isHologramEntity(Entity entity) {
        return BRIDGE.isHologramEntity(entity);
    }

    public static void playChestAction(Location location, boolean open) {
        BRIDGE.playChestAction(location, open);
    }

    public static void sendActionBar(Player player, String message) {
        BRIDGE.sendActionBar(player, message);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        BRIDGE.sendTitle(player, title, subtitle);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        BRIDGE.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendTabHeaderFooter(Player player, String header, String footer) {
        BRIDGE.sendTabHeaderFooter(player, header, footer);
    }

    public static boolean setupNMS() {
        if (MinecraftVersion.getCurrentVersion().getCompareId() == 183) {
            BRIDGE = new NMS1_8R3();
            return true;
        } else if (MinecraftVersion.getCurrentVersion().getCompareId() == 1121) {
            BRIDGE = new NMS1_12R1();
            return true;
        }

        return false;
    }

    public static Material STATIONARY_WATER, SKULL_ITEM, ENDER_PORTAL_FRAME;

    static {
        STATIONARY_WATER = Material.matchMaterial("STATIONARY_WATER");
        if (STATIONARY_WATER == null) {
            STATIONARY_WATER = Material.STATIONARY_WATER;
        }

        SKULL_ITEM = Material.matchMaterial("SKULL_ITEM");
        if (SKULL_ITEM == null) {
            SKULL_ITEM = Material.SKULL_ITEM;
        }

        ENDER_PORTAL_FRAME = Material.matchMaterial("ENDER_PORTAL_FRAME");
        if (ENDER_PORTAL_FRAME == null) {
            ENDER_PORTAL_FRAME = Material.ENDER_PORTAL_FRAME;
        }
    }

    public static void setFieldValue(Field field, Object instance, Object value) throws SecurityException {
        if (!field.isAccessible())
            field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Field findField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            NoSuchFieldException exception = e;
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != null && superClazz != Object.class)
                try {
                    return superClazz.getField(name);
                } catch (NoSuchFieldException noSuchFieldException) {}
            e.printStackTrace();
        }
        return null;
    }

    public static MapHelper getMapHelper() {
        return BRIDGE.getMapHelper();
    }

    public static int getIdOfEntity(Entity e) {
        return BRIDGE.getIdOfEntity(e);
    }
}
