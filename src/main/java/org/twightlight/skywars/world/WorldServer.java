package org.twightlight.skywars.world;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.api.server.SkyWarsTeam;
import org.twightlight.skywars.cosmetics.skywars.sprays.Spray;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.ui.SkyWarsChest;
import org.twightlight.skywars.ui.SkyWarsChest.ChestType;
import org.twightlight.skywars.ui.SkyWarsEvent;
import org.twightlight.skywars.ui.SkyWarsMode;
import org.twightlight.skywars.ui.SkyWarsType;
import org.twightlight.skywars.ui.server.ScanCallback;
import org.twightlight.skywars.utils.*;
import org.twightlight.skywars.utils.LostLogger.LostLevel;
import org.twightlight.skywars.world.type.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public abstract class WorldServer<T> extends SkyWarsServer {

    protected int timer;
    protected WorldConfig config;

    protected WorldTimer task;
    protected boolean isPrivate;
    protected List<SkyWarsTeam> teams = new ArrayList<>();
    protected List<SkyWarsChest> chests = new ArrayList<>();
    protected List<Spray> sprays = new ArrayList<>();
    protected Map<Integer, SkyWarsEvent> timeline = new HashMap<>();

    public WorldServer(String yaml) {
        this(yaml, null);
    }

    public WorldServer(String yaml, ScanCallback callback) {
        super();
        this.timer = Language.game$countdown$start + 1;
        this.config = new WorldConfig(yaml);
        this.name = config.getMapName();
        this.task = new WorldTimer(this);
        for (String spawn : this.config.listSpawns()) {
            this.teams.add(new SkyWarsTeam(this, this.teams.size(), spawn));
        }
        this.config.listChests().forEach(chest -> chests.add(new SkyWarsChest(this, chest)));
        this.timeline = Language.getSkyWarsEventTimeline(this.getType());
        this.state = SkyWarsState.WAITING;
        if (callback != null) {
            callback.finish();
        }
    }

    @Override
    public void destroy() {
        this.timer = 0;
        this.config.destroy();
        this.config = null;
        this.task.cancel();
        this.teams.clear();
        this.teams = null;
        this.chests.clear();
        this.chests = null;
    }

    public abstract void start();

    public abstract void broadcast(String message);

    public abstract void broadcastAction(String message);

    public abstract void broadcastTitle(String title, String subtitle);

    public abstract void broadcast(String message, boolean spectators);

    public abstract void updateScoreboards();

    public abstract void stop(T winner);

    public abstract void reset();

    public abstract void kill(Account account, Account killer, boolean byMob);

    public abstract List<Player> getPlayers(boolean spectators);

    public boolean isPrivate() {
        return isPrivate;
    }

    public List<SkyWarsTeam> getAliveTeams() {
        return teams.stream().filter(team -> team.isAlive()).collect(Collectors.toList());
    }

    public List<SkyWarsTeam> getTeams() {
        return teams;
    }

    public SkyWarsTeam getTeam(Player player) {
        for (SkyWarsTeam team : teams) {
            if (team.hasMember(player)) {
                return team;
            }
        }

        return null;
    }

    public SkyWarsTeam getAvaibleTeam(Player player) {
        return getAvaibleTeam(player, 1);
    }

    public SkyWarsTeam getAvaibleTeam(Player player, int size) {
        for (SkyWarsTeam team : teams) {
            if (team.canJoin(size)) {
                team.addMember(player);
                return team;
            }
        }

        return null;
    }

    public abstract boolean isAlive(Player player);

    public abstract boolean isSpectator(Player player);

    public abstract int getAlive();

    public abstract int getKills(Player player);

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void changeChest(SkyWarsChest chest, ChestType type) {
        this.config.listChests().remove(chest.toString());
        chest.setType(type);
        this.config.listChests().add(chest.toString());
        this.config.getConfig().set("chests", this.config.listChests());
    }

    public String getEvent() {
        if (this.getState() == SkyWarsState.STARTING) {
            return Language.game$event$start + new SimpleDateFormat("mm:ss").format(getTimer() * 1000);
        }

        int eventTime = getEventTime(true);
        SkyWarsEvent currentEvent = timeline.get(eventTime);
        String target;
        if (currentEvent == SkyWarsEvent.Refill) {
            target = Language.game$event$refill;
        } else if (currentEvent == SkyWarsEvent.Doom) {
            target = Language.game$event$doom;
        } else if (currentEvent == SkyWarsEvent.End) {
            target = Language.game$event$end;
        } else {
            target = "";
        }
        return target + new SimpleDateFormat("mm:ss").format((getTimer() - eventTime) * 1000);
    }

    public String getEventTime() {
        int eventTime = getEventTime(true);
        return new SimpleDateFormat("mm:ss").format((getTimer() - eventTime) * 1000);
    }

    public int getEventTime(boolean flag) {
        for (Integer integer : timeline.keySet()) {
            if (getTimer() >= (flag ? integer + 1 : integer)) {
                return integer;
            }
        }
        return 0;
    }

    public int getNextEventTime() {
        for (Integer integer : timeline.keySet()) {
            if (getTimer() > integer) {
                return integer;
            }
        }
        return 0;
    }

    public int getTimer() {
        return timer;
    }

    public WorldTimer getTask() {
        return task;
    }

    public World getWorld() {
        return config.getWorld();
    }

    public WorldConfig getConfig() {
        return config;
    }

    public SkyWarsChest getChest(Block block) {
        for (SkyWarsChest chest : chests) {
            if (chest.getLocation().equals(block.getLocation())) {
                return chest;
            }
        }

        return null;
    }

    public Map<Integer, SkyWarsEvent> getTimeline() {
        return timeline;
    }

    @Override
    public SkyWarsType getType() {
        return SkyWarsType.fromName(this.config.getServerType());
    }

    @Override
    public int getMaxPlayers() {
        return config.listSpawns().size();
    }

    public static final LostLogger LOGGER = Main.LOGGER.getModule("WorldServer");
    private static Map<String, WorldServer<?>> servers = new HashMap<>();

    public static void setupServers() {
        File ymlFolder = new File("plugins/LostSkyWars/servers");
        File zipFolder = new File("plugins/LostSkyWars/maps");

        if (!ymlFolder.exists()) {
            ymlFolder.mkdirs();
        }
        if (!zipFolder.exists()) {
            zipFolder.mkdirs();
        }

        File[] yamlFiles = ymlFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (yamlFiles != null) {
            for (File yamlFile : yamlFiles) {
                WorldServer<?> server = loadArena(yamlFile, null);
                if (yamlFile.getName().contains("_temp")) {
                    WorldServer.removeArena(server);
                }
            }
        }

        LOGGER.log(LostLevel.INFO, "Loaded " + servers.size() + " servers!");
    }
    public static WorldServer<?> loadArena(File yamlFile, ScanCallback callback) {
        return loadArena(yamlFile, callback, false);
    }

    public static WorldServer<?> loadArena(File yamlFile, ScanCallback callback, boolean temp) {
        String arenaName = yamlFile.getName().split("\\.")[0];
        File zipFile = new File("plugins/LostSkyWars/maps", arenaName + ".zip");

        try {
            if (!zipFile.exists()) {
                throw new IllegalArgumentException("Cannot find world zip for arena " + arenaName);
            }

            File destWorldFolder = new File(Bukkit.getWorldContainer(), arenaName);
            if (destWorldFolder.exists()) {
                FileUtils.deleteFile(destWorldFolder);
            }

            ZipUtils.unzip(zipFile, destWorldFolder);

            String mode = ConfigUtils.getConfig(arenaName, "plugins/LostSkyWars/servers").getString("mode");
            String type = ConfigUtils.getConfig(arenaName, "plugins/LostSkyWars/servers").getString("type");

            WorldServer<?> server;
            if ("ranked".equalsIgnoreCase(type)) {
                server = "solo".equalsIgnoreCase(mode) ? new SoloRankedServer(arenaName, callback)
                        : new DoublesRankedServer(arenaName, callback);
            } else if ("duels".equalsIgnoreCase(type)) {
                server = new DuelsServer(arenaName, callback);
            } else {
                server = "solo".equalsIgnoreCase(mode) ? new SoloServer(arenaName, callback)
                        : new DoublesServer(arenaName, callback);
            }

            server.getWorld().getEntities().forEach(Entity::remove);
            server.isPrivate = temp;
            if (!server.isPrivate()) servers.put(arenaName, server);
            return server;
        } catch (Exception ex) {
            LOGGER.log(LostLevel.WARNING, "loadArena(\"" + yamlFile + "\")", ex);
        }
        return null;
    }

    public static void removeArena(WorldServer<?> server) {
        if (!server.isPrivate) {
            servers.remove(server.getServerName());
        }

        FileUtils.deleteFile(server.getConfig().getConfig().getFile());
        File zipFile = new File("plugins/LostSkyWars/maps", server.getServerName() + ".zip");
        FileUtils.deleteFile(zipFile);
        File loadedWorldFolder = new File(Bukkit.getWorldContainer(), server.getServerName());
        FileUtils.deleteFile(loadedWorldFolder);

        server.destroy();
    }

    public static WorldServer<?> findRandom(SkyWarsMode mode, SkyWarsType type) {
        List<WorldServer<?>> servers = listServers().stream()
                .filter(server -> server.getMode().equals(mode) && server.getType().equals(type) && server.getState().canJoin() && !server.isPrivate() && server.getAlive() < server.getMaxPlayers())
                .collect(Collectors.toList());
        Collections.sort(servers, (s1, s2) -> Integer.compare(s2.getAlive(), s1.getAlive()));
        WorldServer<?> server = servers.stream().findFirst().orElse(null);
        if (server != null && server.getAlive() == 0) {
            server = servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
        }

        return server;
    }

    public static Map<String, List<WorldServer<?>>> getAsMap(SkyWarsMode mode, SkyWarsType type) {
        Map<String, List<WorldServer<?>>> result = new HashMap<>();
        listServers().stream().filter(server -> !server.isPrivate() && server.getMode().equals(mode) && server.getType().equals(type)).forEach(arena -> {
            List<WorldServer<?>> list = result.get(arena.getName());
            if (list == null) {
                list = new ArrayList<>();
                result.put(arena.getName(), list);
            }

            if (arena.getState().canJoin() && arena.getAlive() < arena.getMaxPlayers()) {
                list.add(arena);
            }
        });

        return result;
    }

    public static WorldServer<?> getByWorldName(String name) {
        return servers.get(name);
    }

    public static Collection<WorldServer<?>> listServers() {
        return ImmutableList.copyOf(servers.values().stream().filter((worldServer -> !worldServer.isPrivate())).collect(Collectors.toList()));
    }

    public WorldServer<?> cloneServer(boolean temp, String worldName) {

        if (temp) {
            worldName += "_temp";
        }

        ConfigUtils cu = ConfigUtils.getConfig(worldName, "plugins/LostSkyWars/servers");
        cu.set("name", getName());
        cu.set("mode", getMode().name().toLowerCase());
        cu.set("type", getType().name().toLowerCase());
        cu.set("cube", getConfig().getWorldCube().toString().replace(getWorld().getName(), worldName));
        cu.set("min-players", getConfig().getMinPlayers());
        if (getConfig().getConfig().contains("waiting-cube")) {
            cu.set("waiting-cube", getConfig().getWaitingCube().toString().replace(getWorld().getName(), worldName));
            cu.set("waiting-lobby", BukkitUtils.serializeLocation(getConfig().getWaitingLocation()).replace(getWorld().getName(), worldName));
        }
        List<String> spawns = new ArrayList<>();
        for (String spawn : getConfig().listSpawns()) {
            spawns.add(spawn.replace(getWorld().getName(), worldName));
        }
        cu.set("spawns", spawns);
        List<String> chests = new ArrayList<>();
        for (String chest : getConfig().listChests()) {
            chests.add(chest.replace(getWorld().getName(), worldName));
        }
        cu.set("chests", chests);
        List<String> balloons = new ArrayList<>();
        for (String balloon : getConfig().listBalloons()) {
            balloons.add(balloon.replace(getWorld().getName(), worldName));
        }
        cu.set("balloons", balloons);

        FileUtils.copyFiles(new File("plugins/LostSkyWars/maps/" + getWorld().getName() + ".zip"), new File("plugins/LostSkyWars/maps/" + worldName + ".zip"));

        return WorldServer.loadArena(cu.getFile(), null, temp);
    }
}
