package org.twightlight.skywars.arena;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.api.server.SkyWarsTeam;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.sprays.Spray;
import org.twightlight.skywars.modules.privategames.User;
import org.twightlight.skywars.modules.privategames.settings.GameTimeSetting;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.ui.chest.ChestType;
import org.twightlight.skywars.ui.chest.SkyWarsChest;
import org.twightlight.skywars.ui.SkyWarsEvent;
import org.twightlight.skywars.ui.SkyWarsMode;
import org.twightlight.skywars.ui.SkyWarsType;
import org.twightlight.skywars.ui.server.ScanCallback;
import org.twightlight.skywars.utils.*;
import org.twightlight.skywars.utils.Logger.Level;
import org.twightlight.skywars.arena.type.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public abstract class Arena<T> extends SkyWarsServer {
    protected static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    protected int timer;
    protected ArenaConfig config;

    protected Timer timerTask;
    protected boolean isPrivate;
    protected List<SkyWarsTeam> teams = new ArrayList<>();
    protected List<SkyWarsChest> chests = new ArrayList<>();
    protected List<Spray> sprays = new ArrayList<>();
    protected Map<Integer, SkyWarsEvent> timeline = new TreeMap<>();
    protected List<Player> initialPlayers = new ArrayList<>();
    protected long startTime;
    protected long startTimeMillis;
    protected User serverOwner;

    public Arena(String yaml) {
        this(yaml, null, false);
    }

    public Arena(String yaml, ScanCallback callback, boolean isPrivate) {
        super();
        LOGGER.log(Level.INFO, "Loading arena: " + yaml + "...");
        this.timer = Language.game$countdown$start + 1;
        this.config = new ArenaConfig(yaml, isPrivate);
        this.name = config.getMapName();
        this.timerTask = new Timer(this);
        for (String spawn : this.config.listSpawns()) {
            this.teams.add(new SkyWarsTeam(this, this.teams.size(), spawn));
        }
        this.config.listChests().forEach(chest -> chests.add(new SkyWarsChest(this, chest)));
        this.timeline = Language.getSkyWarsEventTimeline(this.getType());

        this.state = SkyWarsState.WAITING;
        if (callback != null) {
            callback.finish();
        }
        this.isPrivate = isPrivate;

    }

    @Override
    public void destroy() {
        this.timer = 0;
        this.config.destroy();
        this.config = null;
        this.timerTask.cancel();
        this.teams.clear();
        this.teams = null;
        this.chests.clear();
        this.chests = null;
        this.initialPlayers.clear();
        this.initialPlayers = null;
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

    public List<Player> getInitialPlayers() {
        return initialPlayers;
    }

    public void setInitialPlayers(List<Player> initialPlayers) {
        this.initialPlayers = initialPlayers;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
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

    public void setServerOwner(User p) {
        if (isPrivate)
            serverOwner = p;
    }

    public SkyWarsTeam getAvailableTeam(Player player) {
        return getAvailableTeam(player, 1);
    }

    public SkyWarsTeam getAvailableTeam(Player player, int size) {
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


    public int getEventTime(boolean flag) {
        for (Integer integer : timeline.keySet()) {
            if (getTimer() >= (flag ? integer + 1 : integer)) {
                return integer;
            }
        }
        return 0;
    }

    public int getTimer() {
        return timer;
    }

    public Timer getTimerTask() {
        return timerTask;
    }

    public World getWorld() {
        return config.getWorld();
    }

    public ArenaConfig getConfig() {
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

    public List<SkyWarsChest> getChests() {
        return chests;
    }

    public Map<Integer, SkyWarsEvent> getTimeline() {
        return timeline;
    }

    public void setTimeline(Map<Integer, SkyWarsEvent> timeline) {
        this.timeline = timeline;
    }

    public void applyPrivateSettings() {
        if (isPrivate) {
            config.getWorld().setTime(GameTimeSetting.GameTime.valueOf(serverOwner.getGameTimeSetting().getValue()).getTime());
            getPlayers(false).forEach(player -> {
                player.setMaxHealth(serverOwner.getHealthMultiplySetting().getValue());
                player.setHealth(player.getMaxHealth());
            });
        }
    }

    public User getServerOwner() {
        return serverOwner;
    }

    @Override
    public SkyWarsType getType() {
        return SkyWarsType.fromName(this.config.getServerType());
    }

    @Override
    public int getMaxPlayers() {
        return config.listSpawns().size();
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("WorldServer");
    private static Map<String, Arena<?>> servers = new HashMap<>();

    public static void setupServers() {
        File ymlFolder = new File("plugins/LostSkyWars/servers");
        File zipFolder = new File("plugins/LostSkyWars/maps");

        if (!ymlFolder.exists()) {
            ymlFolder.mkdirs();
        }
        if (!zipFolder.exists()) {
            zipFolder.mkdirs();
        }
        File[] files = Bukkit.getWorldContainer().listFiles();

        for (File file : files) {
            if (file.isDirectory() && file.getName().contains("_temp")) {
                World loadedWorld = Bukkit.getWorld(file.getName());
                if (loadedWorld != null) {
                    Bukkit.unloadWorld(loadedWorld, false);
                }
                FileUtils.deleteFile(file);
            }
        }

        File[] yamlFiles = ymlFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (yamlFiles != null) {
            for (File yamlFile : yamlFiles) {
                Arena<?> server = loadArena(yamlFile, null);
                if (yamlFile.getName().contains("_temp")) {
                    Arena.removeArena(server);
                }
            }
        }

        LOGGER.log(Level.INFO, "Loaded " + servers.size() + " servers!");
    }
    public static Arena<?> loadArena(File yamlFile, ScanCallback callback) {
        return loadArena(yamlFile, callback, false);
    }

    public static Arena<?> loadArena(File yamlFile, ScanCallback callback, boolean temp) {
        try {
            String arenaName = yamlFile.getName().split("\\.")[0];

            String mode = ConfigUtils.getConfig(arenaName, "plugins/LostSkyWars/servers").getString("mode");
            String type = ConfigUtils.getConfig(arenaName, "plugins/LostSkyWars/servers").getString("type");

            Arena<?> server;
            if ("ranked".equalsIgnoreCase(type)) {
                server = "solo".equalsIgnoreCase(mode) ? new SoloRanked(arenaName, callback, temp)
                        : new DoublesRanked(arenaName, callback, temp);
            } else if ("duels".equalsIgnoreCase(type)) {
                server = new Duels(arenaName, callback, temp);
            } else {
                server = "solo".equalsIgnoreCase(mode) ? new Solo(arenaName, callback, temp)
                        : new Doubles(arenaName, callback, temp);
            }

            if (!server.isPrivate()) servers.put(arenaName, server);
            return server;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "loadArena(\"" + yamlFile + "\")", ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static void removeArena(Arena<?> server) {
        if (!server.isPrivate) {
            servers.remove(server.getServerName());
        }
        FileUtils.deleteFile(server.getConfig().getConfig().getFile());
        SkyWars.getInstance().getWorldLoader().deleteArenaWorld(server);
        server.destroy();
    }

    public static Arena<?> findRandom(SkyWarsMode mode, SkyWarsType type) {
        List<Arena<?>> servers = listServers().stream()
                .filter(server -> server.getMode().equals(mode) && server.getType().equals(type) && server.getState().canJoin() && !server.isPrivate() && server.getAlive() < server.getMaxPlayers())
                .collect(Collectors.toList());
        Collections.sort(servers, (s1, s2) -> Integer.compare(s2.getAlive(), s1.getAlive()));
        Arena<?> server = servers.stream().findFirst().orElse(null);
        if (server != null && server.getAlive() == 0) {
            server = servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
        }

        return server;
    }

    public static Map<String, List<Arena<?>>> getAsMap(SkyWarsMode mode, SkyWarsType type) {
        Map<String, List<Arena<?>>> result = new HashMap<>();
        listServers().stream().filter(server -> !server.isPrivate() && server.getMode().equals(mode) && server.getType().equals(type)).forEach(arena -> {
            List<Arena<?>> list = result.get(arena.getName());
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

    public static Arena<?> getByWorldName(String name) {
        return servers.get(name);
    }

    public static Collection<Arena<?>> listServers() {
        return ImmutableList.copyOf(servers.values().stream().filter((worldServer -> !worldServer.isPrivate())).collect(Collectors.toList()));
    }

    public Arena<?> cloneServer(boolean temp, String newArena) {

        if (temp) {
            newArena += "_temp";
        }

        ConfigUtils cu = ConfigUtils.getConfig(newArena, "plugins/LostSkyWars/servers");
        cu.set("name", getName());
        cu.set("mode", getMode().name().toLowerCase());
        cu.set("type", getType().name().toLowerCase());
        cu.set("cube", getConfig().getWorldCube().toString().replace(getConfig().getId(), newArena));
        cu.set("min-players", getConfig().getMinPlayers());
        if (getConfig().getConfig().contains("waiting-cube")) {
            cu.set("waiting-cube", getConfig().getWaitingCube().toString().replace(getConfig().getId(), newArena));
            cu.set("waiting-lobby", BukkitUtils.serializeLocation(getConfig().getWaitingLocation()).replace(getConfig().getId(), newArena));
        }
        List<String> spawns = new ArrayList<>();
        for (String spawn : getConfig().listSpawns()) {
            spawns.add(spawn.replace(getConfig().getId(), newArena));
        }
        cu.set("spawns", spawns);
        List<String> chests = new ArrayList<>();
        for (String chest : getConfig().listChests()) {
            chests.add(chest.replace(getConfig().getId(), newArena));
        }
        cu.set("chests", chests);
        List<String> balloons = new ArrayList<>();
        for (String balloon : getConfig().listBalloons()) {
            balloons.add(balloon.replace(getConfig().getId(), newArena));
        }
        cu.set("balloons", balloons);

        SkyWars.getInstance().getWorldLoader().cloneArenaWorld(this.getConfig().getId(), newArena);
        return Arena.loadArena(cu.getFile(), null, temp);
    }
}
