package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.sprays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsSpray;
import org.twightlight.skywars.utils.RenderUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Spray {

    private static Map<ItemFrame, Spray> sprays = new HashMap<>();

    private final ItemFrame frame;
    private ArmorStand hologram;
    private boolean usable = true;

    private Spray(ItemFrame frame) {
        this.frame = frame;

        sprays.put(frame, this);
        Location hologramLoc = frame.getLocation().clone();
        BlockFace facing = frame.getAttachedFace().getOppositeFace();

        Vector offset = new Vector(facing.getModX() * - 0.25, facing.getModY(), facing.getModZ() * - 0.25);
        offset.setY(offset.getY() - 0.9);

        hologramLoc.add(offset);

        spawnHolo(hologramLoc);
    }

    public void spawnHolo(Location holoLoc) {
        if (this.hologram != null) {
            destroyHolo();
        }

        hologram = (ArmorStand)holoLoc.getWorld().spawnEntity(holoLoc, EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setCustomName(StringUtils.formatColors(Language.cosmetics$sprays$holograms));
        hologram.setMetadata("HOLO_ITEM_FRAME", (MetadataValue)new FixedMetadataValue(SkyWars.getInstance(), ""));
        hologram.setCustomNameVisible(true);
        hologram.setMarker(true);
        hologram.setSmall(true);
    }

    public void destroyHolo() {
        try {
            hologram.remove();
            this.hologram = null;
        } catch (NullPointerException ignored) {}
    }

    public void applyImage(Player p, SkyWarsSpray spray) {
        if (!usable) {
            return;
        }
        BufferedImage image = spray.getImage();

        MapView mapView = Bukkit.createMap(p.getWorld());
        mapView.getRenderers().clear();
        mapView.addRenderer(new RenderUtils.ImageMapRenderer(image));

        ItemStack mapItem = new ItemStack(Material.MAP);
        mapItem.setDurability(mapView.getId());

        frame.setItem(mapItem);
        destroyHolo();
        usable = false;

        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            usable = true;
        }, 60);
    }


    public static Spray getSpray(ItemFrame frame) {
        return sprays.getOrDefault(frame, null);
    }

    public ItemFrame getFrame() {
        return frame;
    }

    public static Spray createSpray(ItemFrame frame) {
        return new Spray(frame);
    }
}
