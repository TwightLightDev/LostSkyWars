package org.twightlight.skywars.hook.citizens;

import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.holograms.Hologram;
import org.twightlight.skywars.holograms.Holograms;
import org.twightlight.skywars.hook.CitizensHook;
import org.twightlight.skywars.ui.SkyWarsMode;
import org.twightlight.skywars.ui.SkyWarsType;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.Logger.Level;
import org.twightlight.skywars.utils.StringUtils;
import org.twightlight.skywars.arena.Arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.twightlight.skywars.hook.CitizensHook.LOGGER;

public class PlayNPC {

    private String id;
    private SkyWarsMode mode;
    private Location location;

    private NPC npc;
    private Hologram hologram;

    public PlayNPC(String id, SkyWarsMode mode, Location location) {
        this.id = id;
        this.mode = mode;
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
        List<String> lines = new ArrayList<>(mode.equals(SkyWarsMode.SOLO) ? Language.lobby$npcs$play$solo$holograms : Language.lobby$npcs$play$team$holograms);
        Collections.reverse(lines);
        for (String line : lines) {
            this.hologram.withLine(line.replace("{players}", "0"));
        }
        this.npc = CitizensHook.getRegistry().createNPC(EntityType.PLAYER, "§8[NPC] ");
        this.npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        this.npc.data().setPersistent("play-npc", this.mode.name());
        this.npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, "[npc] ");
        this.npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);
        this.npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, mode.equals(SkyWarsMode.SOLO) ? Language.lobby$npcs$play$solo$skin_value : Language.lobby$npcs$play$team$skin_value);
        this.npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA,
                mode.equals(SkyWarsMode.SOLO) ? Language.lobby$npcs$play$solo$skin_signature : Language.lobby$npcs$play$team$skin_signature);

        this.npc.spawn(location);
    }

    public void update() {
        int playing = mode.equals(SkyWarsMode.SOLO) ? (CoreLobbies.SOLO_NORMAL + CoreLobbies.SOLO_INSANE) : (CoreLobbies.DOUBLES_NORMAL + CoreLobbies.DOUBLES_INSANE);
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            for (Arena<?> server : Arena.listServers()) {
                if (server.getMode().equals(mode) && (server.getType() == SkyWarsType.NORMAL || server.getType() == SkyWarsType.INSANE)) {
                    playing += server.getOnline();
                }
            }
        }

        List<String> list = new ArrayList<>(mode.equals(SkyWarsMode.SOLO) ? Language.lobby$npcs$play$solo$holograms : Language.lobby$npcs$play$team$holograms);
        Collections.reverse(list);
        for (int slot = 0; slot < list.size(); slot++) {
            this.hologram.updateLine(slot + 1, list.get(slot).replace("{players}", StringUtils.formatNumber(playing)));
        }
    }

    public void destroy() {
        this.id = null;
        this.mode = null;
        this.location = null;

        this.npc.destroy();
        this.npc = null;
        Holograms.removeHologram(hologram);
        this.hologram = null;
    }

    public String getId() {
        return id;
    }

    public SkyWarsMode getMode() {
        return mode;
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

    private static List<PlayNPC> npcs = new ArrayList<>();

    public static void setupPlayNPCs() {
        if (!CONFIG.contains("play-npcs")) {
            CONFIG.set("play-npcs", new ArrayList<>());
        }

        for (String serialized : CONFIG.getStringList("play-npcs")) {
            if (serialized.split("; ").length > 6) {
                String id = serialized.split("; ")[6];
                SkyWarsMode mode = SkyWarsMode.fromName(serialized.split("; ")[7]);
                if (mode == null) {
                    continue;
                }

                npcs.add(new PlayNPC(id, mode, BukkitUtils.deserializeLocation(serialized)));
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                npcs.forEach(PlayNPC::update);
            }
        }.runTaskTimer(SkyWars.getInstance(), 20, 20);

        LOGGER.log(Level.INFO, "Loaded " + npcs.size() + " PlayNPCs!");
    }

    public static void add(String id, Location location, SkyWarsMode mode) {
        npcs.add(new PlayNPC(id, mode, location));
        List<String> list = CONFIG.getStringList("play-npcs");
        list.add(BukkitUtils.serializeLocation(location) + "; " + id + "; " + mode);
        CONFIG.set("play-npcs", list);
    }

    public static void remove(PlayNPC npc) {
        npcs.remove(npc);
        List<String> list = CONFIG.getStringList("play-npcs");
        list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId() + "; " + npc.getMode());
        CONFIG.set("play-npcs", list);

        npc.destroy();
    }

    public static PlayNPC getById(String id) {
        return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<PlayNPC> listNPCs() {
        return ImmutableList.copyOf(npcs);
    }
}
