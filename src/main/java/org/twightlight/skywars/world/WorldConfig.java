package org.twightlight.skywars.world;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.ui.SkyWarsCube;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.FileUtils;
import org.twightlight.skywars.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("deprecation")
public class WorldConfig {

    private String yaml;
    private String name;
    private String mode;
    private String type;
    private World world;
    private int minPlayers;
    private SkyWarsCube cube;
    private SkyWarsCube waitingCube;
    private String waitingLobby;

    private List<String> spawns;
    private List<String> chests;
    private List<String> balloons;

    private ConfigUtils config;

    public WorldConfig(String yaml) {
        this.yaml = yaml;
        this.spawns = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.balloons = new ArrayList<>();
        this.config = ConfigUtils.getConfig(yaml, "plugins/LostSkyWars/servers");

        this.name = config.getString("name");
        this.mode = config.getString("mode");
        this.type = config.getString("type");
        this.minPlayers = config.getInt("min-players");
        this.cube = new SkyWarsCube(config.getString("cube"));
        if (this.config.contains("waiting-cube")) {
            this.waitingCube = new SkyWarsCube(this.config.getString("waiting-cube"));
            this.waitingLobby = this.config.getString("waiting-lobby");
        }
        this.spawns.addAll(config.getStringList("spawns"));
        this.chests.addAll(config.getStringList("chests"));
        if (!this.config.contains("balloons")) {
            this.config.set("balloons", new ArrayList<>());
        }
        this.balloons.addAll(config.getStringList("balloons"));

        File zipFile = new File("plugins/LostSkyWars/maps", yaml + ".zip");
        File worldFolder = new File(Bukkit.getWorldContainer(), yaml);

        if ((this.world = Bukkit.getWorld(yaml)) != null) {
            Bukkit.unloadWorld(world, false);
        }

        FileUtils.deleteFile(worldFolder);

        try {
            ZipUtils.unzip(zipFile, worldFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        WorldCreator wc = WorldCreator.name(yaml);
        wc.generateStructures(false);
        this.world = wc.createWorld();

        this.world.setTime(0L);
        this.world.setStorm(false);
        this.world.setThundering(false);
        this.world.setAutoSave(false);
        this.world.setAnimalSpawnLimit(0);
        this.world.setWaterAnimalSpawnLimit(0);
        this.world.setKeepSpawnInMemory(false);
        this.world.setGameRuleValue("doMobSpawning", "false");
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("mobGriefing", "false");
    }

    public void setWaitingLobby(SkyWarsCube waitingCube, Location waitingLobby) {
        this.config.set("waiting-cube", waitingCube.toString());
        this.config.set("waiting-lobby", BukkitUtils.serializeLocation(waitingLobby));
        this.waitingCube = new SkyWarsCube(this.config.getString("waiting-cube"));
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
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                        Iterator<Block> iterator = WorldConfig.this.waitingCube.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().setType(Material.AIR);
                        }
                    });
                }
            }.runTaskAsynchronously(Main.getInstance());
        }
    }

    public void destroy() {
        if ((this.world = Bukkit.getWorld(yaml)) != null) {
            Bukkit.unloadWorld(world, false);
        }

        FileUtils.deleteFile(new File(yaml));
        this.yaml = null;
        this.name = null;
        this.mode = null;
        this.type = null;
        this.minPlayers = 0;
        this.cube = null;
        this.spawns.clear();
        this.spawns = null;
        this.chests.clear();
        this.chests = null;
        this.world = null;
        ConfigUtils.removeConfig(config);
        this.config = null;
    }

    public void reload() {
        File zipFile = new File("plugins/LostSkyWars/maps", yaml + ".zip");
        File worldFolder = new File(Bukkit.getWorldContainer(), yaml);

        World loadedWorld = Bukkit.getWorld(yaml);
        if (loadedWorld != null) {
            Bukkit.unloadWorld(loadedWorld, false);
        }

        FileUtils.deleteFile(worldFolder);

        try {
            ZipUtils.unzip(zipFile, worldFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        WorldCreator wc = WorldCreator.name(yaml);
        wc.generateStructures(false);
        this.world = wc.createWorld();

        this.world.setTime(0L);
        this.world.setStorm(false);
        this.world.setThundering(false);
        this.world.setAutoSave(false);
        this.world.setAnimalSpawnLimit(0);
        this.world.setWaterAnimalSpawnLimit(0);
        this.world.setKeepSpawnInMemory(false);
        this.world.setGameRuleValue("doMobSpawning", "false");
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("mobGriefing", "false");
    }

    public ConfigUtils getConfig() {
        return config;
    }

    public String getMapName() {
        return name;
    }

    public String getServerMode() {
        return mode;
    }

    public String getServerType() {
        return type;
    }

    public World getWorld() {
        return world;
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
        return BukkitUtils.deserializeLocation(this.waitingLobby);
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
}
