package org.twightlight.skywars.arena;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.arena.ui.chest.ChestType;
import org.twightlight.skywars.arena.ui.chest.SkyWarsChest;
import org.twightlight.skywars.arena.ui.enums.SkyWarsEvent;
import org.twightlight.skywars.arena.ui.interfaces.ScanCallback;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.sprays.Spray;
import org.twightlight.skywars.modules.privategames.User;
import org.twightlight.skywars.modules.privategames.settings.GameTimeSetting;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;
import org.twightlight.skywars.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public abstract class Arena {

    protected static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    protected String name;
    protected SkyWarsState state;
    protected int timer;
    protected ArenaConfig config;
    protected ArenaGroup group;

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
    protected List<ChatColor> teamcolors = Arrays.stream(ChatColor.values()).collect(Collectors.toList());

    public Arena(String yaml) {
        this(yaml, null, false);
    }

    public Arena(String yaml, ScanCallback callback, boolean isPrivate) {
        this(yaml, callback, isPrivate, new ArenaConfig(yaml, isPrivate));
    }

    public Arena(String yaml, ScanCallback callback, boolean isPrivate, ArenaConfig config) {
        super();
        LOGGER.log(Level.INFO, "Loading arena: " + yaml + "...");
        this.timer = Language.game$countdown$start + 1;
        this.config = config;
        this.name = config.getMapName();
        this.group = GroupManager.get(config.getGroupId());
        if (this.group == null) {
            this.group = GroupManager.getOrDefault(config.getGroupId());
            LOGGER.log(Level.WARNING, "Arena " + yaml + " has unknown group '" + config.getGroupId() + "', falling back to " + (this.group != null ? this.group.getId() : "null"));
        }
        this.timerTask = new Timer(this);
        for (String spawn : this.config.listSpawns()) {
            this.teams.add(new SkyWarsTeam(this, this.teams.size(), spawn));
        }
        this.config.listChests().forEach(chest -> chests.add(new SkyWarsChest(this, chest)));
        this.timeline = this.group.buildTimeline();

        this.state = SkyWarsState.WAITING;
        if (callback != null) {
            callback.finish();
        }
        this.isPrivate = isPrivate;
        Collections.shuffle(teamcolors);
    }

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

    public abstract void stop(SkyWarsTeam winner);

    public abstract void reset();

    public abstract void kill(Account account, Account killer, boolean byMob);

    public abstract void connect(Account account, String... skipParty);

    public abstract void disconnect(Account account);

    public abstract void disconnect(Account account, String options);

    public abstract void spectate(Account account, Player target);

    public abstract List<Player> getPlayers(boolean spectators);

    public abstract boolean isAlive(Player player);

    public abstract boolean isSpectator(Player player);

    public abstract int getAlive();

    public abstract int getOnline();

    public abstract int getKills(Player player);

    public abstract String getServerName();

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
        return teams.stream().filter(SkyWarsTeam::isAlive).collect(Collectors.toList());
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
        if (isPrivate) serverOwner = p;
    }

    public List<ChatColor> getTeamColors() {
        return teamcolors;
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

    public void setState(SkyWarsState state) {
        this.state = state;
    }

    public SkyWarsState getState() {
        return state;
    }

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

    public ArenaGroup getGroup() {
        return this.group;
    }

    public int getMaxPlayers() {
        return config.listSpawns().size();
    }

    public String getName() {
        return name;
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("WorldServer");
    private static Map<String, Arena> servers = new HashMap<>();

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
                Arena server = loadArena(yamlFile, null);
                if (yamlFile.getName().contains("_temp")) {
                    Arena.removeArena(server);
                }
            }
        }

        LOGGER.log(Level.INFO, "Loaded " + servers.size() + " servers!");
    }

    public static Arena loadArena(File yamlFile, ScanCallback callback) {
        return loadArena(yamlFile, callback, false);
    }

    public static Arena loadArena(File yamlFile, ScanCallback callback, boolean temp) {
        try {
            String arenaName = yamlFile.getName().split("\\.")[0];
            Arena server = new GameArena(arenaName, callback, temp);
            if (!server.isPrivate()) servers.put(arenaName, server);
            return server;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "loadArena(\"" + yamlFile + "\")", ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static void removeArena(Arena arena) {
        if (!arena.isPrivate) {
            servers.remove(arena.getConfig().getId());
        }
        FileUtils.deleteFile(arena.getConfig().getConfig().getFile());
        SkyWars.getInstance().getWorldLoader().deleteArenaWorld(arena);
        arena.destroy();
    }

    public static Arena findRandom(ArenaGroup group) {
        List<Arena> matching = listArenas().stream()
                .filter(server -> server.getGroup().equals(group) && server.getState().canJoin()
                        && !server.isPrivate() && server.getAlive() < server.getMaxPlayers())
                .collect(Collectors.toList());
        Collections.sort(matching, (s1, s2) -> Integer.compare(s2.getAlive(), s1.getAlive()));
        Arena server = matching.stream().findFirst().orElse(null);
        if (server != null && server.getAlive() == 0 && matching.size() > 1) {
            server = matching.get(ThreadLocalRandom.current().nextInt(matching.size()));
        }
        return server;
    }

    public static Arena findRandom(String groupId) {
        ArenaGroup group = GroupManager.get(groupId);
        return group != null ? findRandom(group) : null;
    }

    public static Map<String, List<Arena>> getAsMap(ArenaGroup group) {
        Map<String, List<Arena>> result = new HashMap<>();
        listArenas().stream()
                .filter(server -> !server.isPrivate() && server.getGroup().equals(group))
                .forEach(arena -> {
                    List<Arena> list = result.computeIfAbsent(arena.getName(), k -> new ArrayList<>());
                    if (arena.getState().canJoin() && arena.getAlive() < arena.getMaxPlayers()) {
                        list.add(arena);
                    }
                });
        return result;
    }

    public static Map<String, List<Arena>> getAsMap(String groupId) {
        ArenaGroup group = GroupManager.get(groupId);
        return group != null ? getAsMap(group) : new HashMap<>();
    }

    public static Arena getByWorldName(String name) {
        return servers.get(name);
    }

    public static Collection<Arena> listArenas() {
        return ImmutableList.copyOf(servers.values().stream()
                .filter(worldServer -> !worldServer.isPrivate())
                .collect(Collectors.toList()));
    }

    public Arena cloneServer(boolean temp, String newArena) {
        if (temp) {
            newArena += "_temp";
        }

        ConfigWrapper cu = ConfigWrapper.getConfig(newArena, "plugins/LostSkyWars/servers");
        cu.set("name", getName());
        cu.set("group", getGroup().getId());
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
        List<String> chestsList = new ArrayList<>();
        for (String chest : getConfig().listChests()) {
            chestsList.add(chest.replace(getConfig().getId(), newArena));
        }
        cu.set("chests", chestsList);
        List<String> balloons = new ArrayList<>();
        for (String balloon : getConfig().listBalloons()) {
            balloons.add(balloon.replace(getConfig().getId(), newArena));
        }
        cu.set("balloons", balloons);

        SkyWars.getInstance().getWorldLoader().cloneArenaWorld(this.getConfig().getId(), newArena);
        return Arena.loadArena(cu.getFile(), null, temp);
    }
}
