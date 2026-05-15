package org.twightlight.skywars.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.arena.ui.cuboid.SkyWarsCube;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
public class ArenaConfig {

    protected static Logger LOGGER = SkyWars.LOGGER.getModule("Arena");
    protected String yaml;
    protected String name;
    protected String groupId;
    protected World world;
    protected int minPlayers;
    protected SkyWarsCube cube;
    protected SkyWarsCube waitingCube;
    protected String waitingLobby;

    protected List<String> spawns;
    protected List<String> chests;
    protected List<String> balloons;

    protected ConfigWrapper config;
    protected String worldName;
    protected CompletableFuture<World> cf;

    public ArenaConfig(String yaml, boolean isPrivate) {
        long start = System.currentTimeMillis();
        this.yaml = yaml;
        if (!isPrivate) {
            worldName = yaml + "_" + System.nanoTime() + "_temp";
        } else {
            worldName = yaml;
        }
        this.spawns = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.balloons = new ArrayList<>();
        this.config = ConfigWrapper.getConfig(yaml, "plugins/LostSkyWars/servers");

        this.name = config.getString("name");

        // Migration: convert old mode+type to group
        if (config.contains("group")) {
            this.groupId = config.getString("group");
        } else if (config.contains("mode") && config.contains("type")) {
            String oldMode = config.getString("mode");
            String oldType = config.getString("type");
            ArenaGroup migrated = GroupManager.fromLegacy(oldMode, oldType);
            this.groupId = migrated != null ? migrated.getId() : "solo";
            config.set("group", this.groupId);
            config.set("mode", null);
            config.set("type", null);
            LOGGER.log(Logger.Level.INFO, "Migrated arena " + yaml + " from mode=" + oldMode + "+type=" + oldType + " to group=" + this.groupId);
        } else {
            this.groupId = "solo";
        }

        this.minPlayers = config.getInt("min-players");
        this.cube = new SkyWarsCube(config.getString("cube"), worldName);
        if (this.config.contains("waiting-cube")) {
            this.waitingCube = new SkyWarsCube(this.config.getString("waiting-cube"), worldName);
            this.waitingLobby = this.config.getString("waiting-lobby");
        }
        this.spawns.addAll(config.getStringList("spawns"));
        this.chests.addAll(config.getStringList("chests"));
        if (!this.config.contains("balloons")) {
            this.config.set("balloons", new ArrayList<>());
        }
        this.balloons.addAll(config.getStringList("balloons"));

        LOGGER.log(Logger.Level.INFO, "Initializing original world for arena: " + yaml + "...");

        cf = SkyWars.getInstance().getWorldLoader().createOriginalWorld(yaml);

        if (!isPrivate) {
            cf = cf.thenApply((world1 -> {
                LOGGER.log(Logger.Level.INFO, "Initializing playing world for arena: " + yaml + "...");
                return world1;
            })).thenCompose((world) -> SkyWars.getInstance().getWorldLoader().createArenaWorld(yaml, worldName));
        }
        cf = cf.thenApply(w2 -> {
            world = w2;
            w2.getEntities().forEach(Entity::remove);
            long end = System.currentTimeMillis();
            LOGGER.log(Logger.Level.INFO, "Arena " + yaml + " loaded in " + (end - start) + "ms");
            return w2;
        });
    }

    public void setWaitingLobby(SkyWarsCube waitingCube, Location waitingLobby) {
        this.config.set("waiting-cube", waitingCube.toString());
        this.config.set("waiting-lobby", BukkitUtils.serializeLocation(waitingLobby));
        this.waitingCube = new SkyWarsCube(this.config.getString("waiting-cube"), worldName);
        this.waitingLobby = this.config.getString("waiting-lobby");
    }

    public void addBalloon(String balloon) {
        this.balloons.add(balloon);
        this.config.set("balloons", this.balloons);
    }

    public boolean isBalloon(String blockLocation) {
        return this.balloons.stream().filter(balloon -> balloon.equals(blockLocation)).count() > 0;
    }

    public void removeWaitingLobby() {
        if (this.waitingCube != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> {
                        Iterator<Block> iterator = ArenaConfig.this.waitingCube.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().setType(Material.AIR);
                        }
                    });
                }
            }.runTaskAsynchronously(SkyWars.getInstance());
        }
    }

    public void destroy() {
        SkyWars.getInstance().getWorldLoader().unload(worldName);
        this.worldName = null;
        this.yaml = null;
        this.name = null;
        this.groupId = null;
        this.minPlayers = 0;
        this.cube = null;
        this.spawns.clear();
        this.spawns = null;
        this.chests.clear();
        this.chests = null;
        this.world = null;
        ConfigWrapper.removeConfig(config);
        this.config = null;
    }

    public void reload(Arena server) {
        SkyWars.getInstance().getWorldLoader().deleteWorld(worldName);

        String id = String.valueOf(System.nanoTime());

        worldName = yaml + "_" + id + "_temp";

        this.cube = new SkyWarsCube(config.getString("cube"), worldName);
        if (this.config.contains("waiting-cube")) {
            this.waitingCube = new SkyWarsCube(this.config.getString("waiting-cube"), worldName);
        }

        cf = SkyWars.getInstance().getWorldLoader().createArenaWorld(yaml, worldName);

        cf = cf.thenApply(w2 -> {
            world = w2;
            w2.getEntities().forEach(Entity::remove);
            server.setState(SkyWarsState.WAITING);
            return w2;
        });
    }

    public ConfigWrapper getConfig() {
        return config;
    }

    public String getMapName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public World getWorld() {
        return world;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getId() {
        return yaml;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public SkyWarsCube getWorldCube() {
        return cube;
    }

    public SkyWarsCube getWaitingCube() {
        return waitingCube;
    }

    public Location getWaitingLocation() {
        return BukkitUtils.deserializeLocation(this.waitingLobby, worldName);
    }

    public boolean hasWaitingLobby() {
        return this.waitingCube != null;
    }

    public List<String> listSpawns() {
        return spawns;
    }

    public List<String> listChests() {
        return chests;
    }

    public List<String> listBalloons() {
        return balloons;
    }

    public String getBalloon(int index) {
        return index >= this.balloons.size() ? null : this.balloons.get(index);
    }

    public CompletableFuture<World> getCompletableWorld() {
        return cf;
    }
}
