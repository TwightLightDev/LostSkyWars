package org.twightlight.skywars.hook.citizens;

import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.systems.delivery.Delivery;
import org.twightlight.skywars.systems.holograms.Hologram;
import org.twightlight.skywars.systems.holograms.Holograms;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.twightlight.skywars.hook.citizens.CitizensHook.LOGGER;

public class DeliveryNPC {

    private String id;
    private Location location;

    private NPC npc;
    private Hologram hologram;

    public DeliveryNPC(String id, Location location) {
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

        this.hologram = Holograms.createHologram(this.location.clone().add(0, 0.5, 0));
        List<String> lines = new ArrayList<>(Language.lobby$npcs$deliveryman$holograms);
        Collections.reverse(lines);
        for (String line : lines) {
            this.hologram.withLine(line);
        }
        this.npc = CitizensHook.getRegistry().createNPC(EntityType.PLAYER, "§8[NPC] ");
        this.npc.data().setPersistent("deliveryman", true);
        this.npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("[npc] ",
                Language.lobby$npcs$deliveryman$skin_value,
                Language.lobby$npcs$deliveryman$skin_signature);
        npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        this.npc.spawn(location);
    }

    public void update() {

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

    private static List<DeliveryNPC> npcs = new ArrayList<>();

    public static void setupDeliveryNPCs() {
        if (!CONFIG.contains("delivery-npcs")) {
            CONFIG.set("delivery-npcs", new ArrayList<>());
        }

        for (String serialized : CONFIG.getStringList("delivery-npcs")) {
            if (serialized.split("; ").length > 6) {
                String id = serialized.split("; ")[6];
                npcs.add(new DeliveryNPC(id, BukkitUtils.deserializeLocation(serialized)));
            }
        }
        Delivery.setupDeliveries();

        LOGGER.log(Level.INFO, "Loaded " + npcs.size() + " DeliveryNPCs!");
    }

    public static void add(String id, Location location) {
        npcs.add(new DeliveryNPC(id, location));
        List<String> list = CONFIG.getStringList("delivery-npcs");
        list.add(BukkitUtils.serializeLocation(location) + "; " + id);
        CONFIG.set("delivery-npcs", list);
    }

    public static void remove(DeliveryNPC npc) {
        npcs.remove(npc);
        List<String> list = CONFIG.getStringList("delivery-npcs");
        list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
        CONFIG.set("delivery-npcs", list);

        npc.destroy();
    }

    public static DeliveryNPC getById(String id) {
        return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<DeliveryNPC> listNPCs() {
        return ImmutableList.copyOf(npcs);
    }
}
