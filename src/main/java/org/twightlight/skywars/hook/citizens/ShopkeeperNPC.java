package org.twightlight.skywars.hook.citizens;

import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.systems.holograms.Hologram;
import org.twightlight.skywars.systems.holograms.Holograms;
import org.twightlight.skywars.hook.CitizensHook;
import org.twightlight.skywars.nms.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.Logger.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.twightlight.skywars.hook.CitizensHook.LOGGER;

@SuppressWarnings("deprecation")
public class ShopkeeperNPC {

    private String id;
    private Location location;

    private NPC npc;
    private Hologram hologram;

    public ShopkeeperNPC(String id, Location location) {
        this.id = id;
        this.location = location;
        if (!this.location.getChunk().isLoaded()) {
            this.location.getChunk().load(true);
        }

        this.spawn();
    }

    public void spawn() {
        if (this.npc != null) {
            this.npc.destroy();
            this.npc = null;
        }

        if (this.hologram != null) {
            Holograms.removeHologram(this.hologram);
            this.hologram = null;
        }

        this.hologram = Holograms.createHologram(this.location.clone().add(0, 1.2, 0));
        List<String> lines = new ArrayList<>(Language.lobby$npcs$shopkeeper$holograms);
        Collections.reverse(lines);
        for (String line : lines) {
            this.hologram.withLine(line);
        }
        EntityType wither = null;
        for (EntityType type : EntityType.values()) {
            if (type.name().equals("WITHER_SKELETON")) {
                wither = type;
            }
        }
        boolean witherFound = wither != null;
        this.npc = CitizensHook.getRegistry().createNPC(wither == null ? EntityType.SKELETON : wither, "");
        this.npc.data().setPersistent("shopkeeper", true);
        this.npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        this.npc.addTrait(new Trait("skeleton-weapon") {
            @Override
            public void onSpawn() {
                ((LivingEntity) this.npc.getEntity()).getEquipment().setItemInHand(BukkitUtils.deserializeItemStack("DIAMOND_SWORD"));
                if (!witherFound) {
                    ((Skeleton) this.npc.getEntity()).setSkeletonType(SkeletonType.WITHER);
                }
            }
        });
        npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        this.npc.spawn(location);
    }

    public void update() {
        Location location = this.getLocation().clone().add(0, 1.2, 0);
        ParticleEffect.FLAME.display(1.0F, 0.0F, 1.0F, 1.0F, 90, location, 16);
        ParticleEffect.SMOKE_NORMAL.display(1.0F, 0.0F, 1.0F, 0.7F, 90, location, 16);
    }

    public void destroy() {
        this.id = null;
        this.location = null;

        this.npc.destroy();
        this.npc = null;
        Holograms.removeHologram(hologram);
        this.hologram = null;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public NPC getNPC() {
        return npc;
    }

    public Hologram getHologram() {
        return hologram;
    }

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("locations");

    private static List<ShopkeeperNPC> npcs = new ArrayList<>();

    public static void setupShopkeeperNPCs() {
        if (!CONFIG.contains("shopkeeper-npcs")) {
            CONFIG.set("shopkeeper-npcs", new ArrayList<>());
        }

        for (String serialized : CONFIG.getStringList("shopkeeper-npcs")) {
            if (serialized.split("; ").length > 6) {
                String id = serialized.split("; ")[6];
                npcs.add(new ShopkeeperNPC(id, BukkitUtils.deserializeLocation(serialized)));
            }
        }

//    new BukkitRunnable() {
//      @Override
//      public void run() {
//        npcs.forEach(ShopkeeperNPC::update);
//      }
//    }.runTaskTimer(Main.getInstance(), 0, 10);
        LOGGER.log(Level.INFO, "Loaded " + npcs.size() + " ShopkeeperNPCs!");
    }

    public static void add(String id, Location location) {
        npcs.add(new ShopkeeperNPC(id, location));
        List<String> list = CONFIG.getStringList("shopkeeper-npcs");
        list.add(BukkitUtils.serializeLocation(location) + "; " + id);
        CONFIG.set("shopkeeper-npcs", list);
    }

    public static void remove(ShopkeeperNPC npc) {
        npcs.remove(npc);
        List<String> list = CONFIG.getStringList("shopkeeper-npcs");
        list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
        CONFIG.set("shopkeeper-npcs", list);

        npc.destroy();
    }

    public static ShopkeeperNPC getById(String id) {
        return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<ShopkeeperNPC> listNPCs() {
        return ImmutableList.copyOf(npcs);
    }
}
