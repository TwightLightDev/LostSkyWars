package org.twightlight.skywars.nms.abstracts;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.twightlight.skywars.systems.holograms.Hologram;
import org.twightlight.skywars.systems.holograms.HologramLine;
import org.twightlight.skywars.systems.holograms.entity.IArmorStand;

import java.util.List;
import java.util.UUID;

public abstract class NMSBridge {

    public abstract IArmorStand createArmorStand(Location location, String name, HologramLine line);

    public abstract BalloonEntity createBalloonLeash(Location location, List<UUID> viewers);

    public abstract BalloonEntity createBalloonBat(Location location, BalloonEntity leash, List<UUID> viewers);

    public abstract BalloonEntity createBalloonGiant(Location location, List<String> frames, List<UUID> viewers);

    public abstract Hologram getHologram(Entity entity);

    public abstract boolean isHologramEntity(Entity entity);

    public abstract void playChestAction(Location location, boolean open);

    public abstract void sendActionBar(Player player, String message);

    public abstract void sendTitle(Player player, String title, String subtitle);

    public abstract void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);

    public abstract void sendTabHeaderFooter(Player player, String header, String footer);

    public abstract MapHelper getMapHelper();

    public abstract int getIdOfEntity(Entity entity);

    public abstract void sendChunksAroundLocation(Player player, Location loc,
                             int radius);

    public abstract void unloadChunksAroundLocation(Player player, Location loc,
                                            int radius);

    public abstract void resyncTrackedEntities(org.bukkit.entity.Player player);

    public abstract void resyncEntity(Player player, Entity entity);
}
