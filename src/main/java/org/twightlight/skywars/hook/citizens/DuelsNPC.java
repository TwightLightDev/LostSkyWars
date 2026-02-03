package org.twightlight.skywars.hook.citizens;

import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.ui.enums.SkyWarsType;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.systems.holograms.Hologram;
import org.twightlight.skywars.systems.holograms.Holograms;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.twightlight.skywars.hook.citizens.CitizensHook.LOGGER;

public class DuelsNPC {

    private String id;
    private Location location;

    private NPC npc;
    private Hologram hologram;

    public DuelsNPC(String id, Location location) {
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
        List<String> lines = new ArrayList<>(Language.lobby$npcs$duels$holograms);
        Collections.reverse(lines);
        for (String line : lines) {
            this.hologram.withLine(line.replace("{players}", "0"));
        }
        this.npc = CitizensHook.getRegistry().createNPC(EntityType.PLAYER, "§8[NPC] ");
        this.npc.data().setPersistent("duels-npc", "duels");
        this.npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("[npc] ",
                Language.lobby$npcs$duels$skin_value,
                Language.lobby$npcs$duels$skin_signature);

        npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        this.npc.spawn(location);
    }

    public void update() {
        int playing = CoreLobbies.SOLO_DUELS + CoreLobbies.DOUBLES_DUELS;
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            for (Arena<?> server : Arena.listServers()) {
                if (server.getType().equals(SkyWarsType.DUELS)) {
                    playing += server.getOnline();
                }
            }
        }

        List<String> lines = new ArrayList<>(Language.lobby$npcs$duels$holograms);
        Collections.reverse(lines);
        for (int slot = 0; slot < lines.size(); slot++) {
            this.hologram.updateLine(slot + 1, lines.get(slot).replace("{players}", StringUtils.formatNumber(playing)));
        }
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

    private static List<DuelsNPC> npcs = new ArrayList<>();

    public static void setupDuelsNPCs() {
        if (!CONFIG.contains("duels-npcs")) {
            CONFIG.set("duels-npcs", new ArrayList<>());
        }

        for (String serialized : CONFIG.getStringList("duels-npcs")) {
            if (serialized.split("; ").length > 6) {
                String id = serialized.split("; ")[6];
                npcs.add(new DuelsNPC(id, BukkitUtils.deserializeLocation(serialized)));
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                npcs.forEach(DuelsNPC::update);
            }
        }.runTaskTimer(SkyWars.getInstance(), 20, 20);

        LOGGER.log(Level.INFO, "Loaded " + npcs.size() + " DuelsNPCs!");
    }

    public static void add(String id, Location location) {
        npcs.add(new DuelsNPC(id, location));
        List<String> list = CONFIG.getStringList("duels-npcs");
        list.add(BukkitUtils.serializeLocation(location) + "; " + id);
        CONFIG.set("duels-npcs", list);
    }

    public static void remove(DuelsNPC npc) {
        npcs.remove(npc);
        List<String> list = CONFIG.getStringList("duels-npcs");
        list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
        CONFIG.set("duels-npcs", list);

        npc.destroy();
    }

    public static DuelsNPC getById(String id) {
        return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<DuelsNPC> listNPCs() {
        return ImmutableList.copyOf(npcs);
    }
}
