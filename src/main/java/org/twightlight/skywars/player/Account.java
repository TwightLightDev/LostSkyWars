package org.twightlight.skywars.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.GameArena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.commands.sw.SetLobbyCommand;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.database.player.CosmeticContainer;
import org.twightlight.skywars.database.player.SelectedContainer;
import org.twightlight.skywars.database.player.StatsContainer;
import org.twightlight.skywars.player.level.Level;
import org.twightlight.skywars.player.rank.Rank;
import org.twightlight.skywars.systems.scoreboard.LostScoreboard;
import org.twightlight.skywars.systems.scoreboard.ScoreboardScroller;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;
import org.twightlight.skywars.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("deprecation")
public class Account {

    private UUID id;
    private String name;
    private LostScoreboard scoreboard;

    private Arena arena;

    protected Map<String, StatsContainer> profile;
    protected Map<String, Map<String, StatsContainer>> statsPerGroup;
    protected Map<String, StatsContainer> cosmetics;
    protected Map<String, StatsContainer> selections;
    protected SelectedContainer selectedContainer;

    private Map<UUID, Long> lastHit = new HashMap<>();

    public Account(UUID id, String name) {
        this(id, name, false);
    }

    protected Account(UUID id, String name, boolean virtual) {
        this.id = id;
        this.name = name;
        this.statsPerGroup = new LinkedHashMap<>();

        if (!virtual) {
            this.profile = Database.getInstance().loadProfile(id, name);
            this.cosmetics = Database.getInstance().loadCosmetics(id, name);
            this.selections = Database.getInstance().loadSelections(id, name);
        } else {
            this.profile = buildDefaultProfile();
            this.cosmetics = buildDefaultCosmetics();
            this.selections = buildDefaultSelections();
        }

        this.selectedContainer = new SelectedContainer(this.selections);

        if (this.profile.get("leveling").get() == null) {
            this.profile.get("leveling").set("[]");
        }
        if (this.profile.get("deliveries").get() == null) {
            this.profile.get("deliveries").set("{}");
        }
    }

    private static Map<String, StatsContainer> buildDefaultProfile() {
        Map<String, StatsContainer> map = new LinkedHashMap<>();
        map.put("coins", new StatsContainer(0));
        map.put("souls", new StatsContainer(0));
        map.put("level", new StatsContainer(1));
        map.put("exp", new StatsContainer(0.0));
        map.put("max_souls", new StatsContainer(100));
        map.put("well_roll", new StatsContainer(1));
        map.put("souls_per_win", new StatsContainer(0));
        map.put("mystery_dusts", new StatsContainer(0));
        map.put("last_rank", new StatsContainer("&7"));
        map.put("deliveries", new StatsContainer("{}"));
        map.put("leveling", new StatsContainer("[]"));
        map.put("show_players", new StatsContainer(true));
        map.put("show_gore", new StatsContainer(true));
        return map;
    }

    private static Map<String, StatsContainer> buildDefaultCosmetics() {
        Map<String, StatsContainer> map = new LinkedHashMap<>();
        map.put("kits", new StatsContainer("{}"));
        map.put("perks", new StatsContainer("{}"));
        map.put("cages", new StatsContainer("{}"));
        map.put("death_cries", new StatsContainer("{}"));
        map.put("trails", new StatsContainer("{}"));
        map.put("balloons", new StatsContainer("{}"));
        map.put("kill_messages", new StatsContainer("{}"));
        map.put("kill_effects", new StatsContainer("{}"));
        map.put("sprays", new StatsContainer("{}"));
        map.put("victory_dances", new StatsContainer("{}"));
        map.put("titles", new StatsContainer("{}"));
        map.put("symbols", new StatsContainer("{}"));
        return map;
    }

    private static Map<String, StatsContainer> buildDefaultSelections() {
        Map<String, StatsContainer> map = new LinkedHashMap<>();
        map.put("kit", new StatsContainer("{}"));
        map.put("perk", new StatsContainer("{}"));
        map.put("cage", new StatsContainer(0));
        map.put("death_cry", new StatsContainer(0));
        map.put("trail", new StatsContainer(0));
        map.put("balloon", new StatsContainer(0));
        map.put("kill_message", new StatsContainer(0));
        map.put("kill_effect", new StatsContainer(0));
        map.put("spray", new StatsContainer(0));
        map.put("victory_dance", new StatsContainer(0));
        map.put("title", new StatsContainer(0));
        map.put("symbol", new StatsContainer(0));
        map.put("last_selected", new StatsContainer(0L));
        map.put("favorites", new StatsContainer("[]"));
        return map;
    }

    public Map<String, StatsContainer> getStatsForGroup(String groupId) {
        Map<String, StatsContainer> stats = statsPerGroup.get(groupId);
        if (stats == null) {
            stats = Database.getInstance().loadStats(id, groupId, name);
            statsPerGroup.put(groupId, stats);
        }
        return stats;
    }

    public void addStat(String groupId, String statName) {
        addStat(groupId, statName, 1);
    }

    public void addStat(String groupId, String statName, int amount) {
        Map<String, StatsContainer> stats = getStatsForGroup(groupId);
        StatsContainer container = stats.get(statName);
        if (container != null) {
            container.addInt(amount);
        }
    }

    public void removeStat(String groupId, String statName, int amount) {
        Map<String, StatsContainer> stats = getStatsForGroup(groupId);
        StatsContainer container = stats.get(statName);
        if (container != null) {
            for (int i = 0; i < amount; i++) {
                if (container.getAsInt() <= 0) break;
                container.removeInt(1);
            }
        }
    }

    public int getStat(String groupId, String statName) {
        Map<String, StatsContainer> stats = getStatsForGroup(groupId);
        StatsContainer container = stats.get(statName);
        return container != null ? container.getAsInt() : 0;
    }

    public String getStatFormatted(String groupId, String statName) {
        return StringUtils.formatNumber(getStat(groupId, statName));
    }

    public int getStatAcrossGroups(String statName, String... groupIds) {
        int total = 0;
        for (String groupId : groupIds) {
            total += getStat(groupId, statName);
        }
        return total;
    }

    public String getStatAcrossGroupsFormatted(String statName, String... groupIds) {
        return StringUtils.formatNumber(getStatAcrossGroups(statName, groupIds));
    }

    // =========================================================================
    // PROFILE (COINS, SOULS, LEVEL, EXP, ETC.)
    // =========================================================================

    public void addCoins(int amount) {
        if (SkyWars.vault && SkyWars.economy != null) {
            ((net.milkbowl.vault.economy.Economy) SkyWars.economy).depositPlayer(this.getPlayer(), amount);
            return;
        }
        this.profile.get("coins").addInt(amount);
    }

    public void removeCoins(int amount) {
        if (SkyWars.vault && SkyWars.economy != null) {
            ((net.milkbowl.vault.economy.Economy) SkyWars.economy).withdrawPlayer(this.getPlayer(), amount);
            return;
        }
        this.profile.get("coins").removeInt(amount);
    }

    public int getCoins() {
        if (SkyWars.vault && SkyWars.economy != null) {
            return (int) ((net.milkbowl.vault.economy.Economy) SkyWars.economy).getBalance(this.getPlayer());
        }
        return this.profile.get("coins").getAsInt();
    }

    public String getCoinsFormatted() {
        return StringUtils.formatNumber(getCoins());
    }

    public void addSouls(int amount) {
        this.profile.get("souls").addInt(amount);
    }

    public void removeSouls(int amount) {
        this.profile.get("souls").removeInt(amount);
    }

    public int getSouls() {
        return this.profile.get("souls").getAsInt();
    }

    public String getSoulsFormatted() {
        return StringUtils.formatNumber(getSouls());
    }

    public int getMaxSouls() {
        return this.profile.get("max_souls").getAsInt();
    }

    public int getWellRoll() {
        return this.profile.get("well_roll").getAsInt();
    }

    public int getSoulsPerWin() {
        return this.profile.get("souls_per_win").getAsInt();
    }

    public int getMysteryDusts() {
        return this.profile.get("mystery_dusts").getAsInt();
    }

    public void addMysteryDusts(int dusts) {
        if (SkyWars.lostboxes) {
            io.github.losteddev.boxes.api.LostBoxesAPI.addMysteryDusts(this.getPlayer(), dusts);
        }
    }

    public void addExp(double exp) {
        this.profile.get("exp").addDouble(exp);
        Level current = Level.getByLevel(this.getLevel());
        Level nextLevel = current.getNext();
        if (current.getExperienceUntil(this.getExp()) <= 0.0) {
            if (nextLevel != null) {
                this.profile.get("level").addInt(1);
                this.profile.get("exp").set(0.0D);
            }
        }
    }

    public int getLevel() {
        return this.profile.get("level").getAsInt();
    }

    public double getExp() {
        return this.profile.get("exp").getAsDouble();
    }

    public String getLastRank() {
        return this.profile.get("last_rank").getAsString();
    }

    public boolean canSeePlayers() {
        return this.profile.get("show_players").getAsBoolean();
    }

    public void setCanSeePlayers(boolean flag) {
        this.profile.get("show_players").set(flag);
    }

    public boolean canSeeBlood() {
        return this.profile.get("show_gore").getAsBoolean();
    }

    public void setCanSeeBlood(boolean flag) {
        this.profile.get("show_gore").set(flag);
    }

    public Map<String, StatsContainer> getProfile() {
        return profile;
    }

    // =========================================================================
    // LEVELING
    // =========================================================================

    @SuppressWarnings("unchecked")
    public void addLeveling(int level) {
        JSONArray array = this.profile.get("leveling").getAsJsonArray();
        array.add(String.valueOf(level));
        this.profile.get("leveling").set(array.toString());
    }

    public boolean isLeveled(int level) {
        return this.profile.get("leveling").getAsJsonArray().contains(String.valueOf(level));
    }

    // =========================================================================
    // FAVORITES & MAP SELECTION
    // =========================================================================

    @SuppressWarnings("unchecked")
    public void addFavoriteMap(String mapName) {
        JSONArray array = this.selectedContainer.getFavoritesJson().equals("[]") ? new JSONArray() : parseJsonArray(this.selectedContainer.getFavoritesJson());
        array.add(mapName);
        this.selectedContainer.setFavoritesJson(array.toString());
    }

    public void removeFavoriteMap(String mapName) {
        JSONArray array = parseJsonArray(this.selectedContainer.getFavoritesJson());
        array.remove(mapName);
        this.selectedContainer.setFavoritesJson(array.toString());
    }

    public boolean isFavoriteMap(String mapName) {
        return parseJsonArray(this.selectedContainer.getFavoritesJson()).contains(mapName);
    }

    public void updateLastSelected() {
        this.selectedContainer.setLastSelected(TimeUtils.getExpireIn(1));
    }

    public boolean canSelectMap() {
        return this.selectedContainer.getLastSelected() < System.currentTimeMillis();
    }

    private JSONArray parseJsonArray(String json) {
        try {
            return (JSONArray) new org.json.simple.parser.JSONParser().parse(json);
        } catch (Exception ex) {
            return new JSONArray();
        }
    }

    // =========================================================================
    // COSMETIC OWNERSHIP
    // =========================================================================

    public Map<String, StatsContainer> getCosmeticsMap() {
        return cosmetics;
    }

    public CosmeticContainer getCosmeticContainer(String column) {
        StatsContainer container = cosmetics.get(column);
        if (container == null) return new CosmeticContainer(new StatsContainer("{}"), "{}");
        return new CosmeticContainer(container, container.getAsString());
    }

    // =========================================================================
    // SELECTIONS
    // =========================================================================

    public SelectedContainer getSelectedContainer() {
        return selectedContainer;
    }

    public Map<String, StatsContainer> getSelectionsMap() {
        return selections;
    }

    // =========================================================================
    // ARENA
    // =========================================================================

    public void setArena(Arena arena) {
        this.arena = arena;
        this.lastHit.clear();
    }

    public void setHit(UUID id) {
        this.lastHit.put(id, System.currentTimeMillis() + 8000);
    }

    public boolean inLobby() {
        return arena == null;
    }

    public Arena getArena() {
        return arena;
    }

    // =========================================================================
    // PLAYER REFRESH / UI
    // =========================================================================

    public void refreshPlayer() {
        Player player = getPlayer();
        if (player == null) return;

        player.setMaxHealth(20.0);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setExhaustion(0.0f);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setAllowFlight(false);
        player.closeInventory();
        player.spigot().setCollidesWithEntities(true);
        for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        if (inLobby()) {
            setupLobbyInventory(player);
        } else if (arena.getState().canJoin()) {
            setupWaitingInventory(player);
        } else if (arena.getState() == SkyWarsState.STARTING) {
            setupStartingInventory(player);
        } else if (arena.isSpectator(player)) {
            setupSpectatorInventory(player);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }
        player.updateInventory();
    }

    private void setupLobbyInventory(Player player) {
        player.setGameMode(GameMode.ADVENTURE);

        int slot = Language.lobby$hotbar$profile$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot,
                    BukkitUtils.putProfileOnSkull(player, BukkitUtils.deserializeItemStack(
                            "SKULL_ITEM:3 : 1 : display=" + Language.lobby$hotbar$profile$name)));
        }
        slot = Language.lobby$hotbar$shop$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot,
                    BukkitUtils.deserializeItemStack("EMERALD : 1 : display=" + Language.lobby$hotbar$shop$name));
        }
        slot = Language.lobby$hotbar$players$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack(
                    "INK_SACK:" + (canSeePlayers() ? "10" : "8") + " : 1 : display="
                            + (canSeePlayers() ? Language.lobby$hotbar$players$name_v : Language.lobby$hotbar$players$name_i)));
        }
        Rank.getRank(player).apply(player);
        player.teleport(SetLobbyCommand.getSpawnLocation());

        if (Language.lobby$speed$enabled) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, Language.lobby$speed$level - 1));
        }
        if (Language.lobby$jump_boost$enabled) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, Language.lobby$jump_boost$level - 1));
        }
    }

    private void setupWaitingInventory(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        ArenaGroup group = arena.getGroup();
        boolean isDuels = group != null && group.hasTrait("no_kits");

        int slot = Language.game$hotbar$kits$slot;
        if (slot >= 0 && slot < 9 && !isDuels) {
            player.getInventory().setItem(slot,
                    BukkitUtils.deserializeItemStack("BOW : 1 : display=" + Language.game$hotbar$kits$name));
        }
        slot = Language.game$hotbar$quit$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot,
                    BukkitUtils.deserializeItemStack("BED : 1 : display=" + Language.game$hotbar$quit$name));
        }
    }

    private void setupStartingInventory(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        ArenaGroup group = arena.getGroup();
        boolean isDuels = group != null && group.hasTrait("no_kits");

        int slot = Language.game$hotbar$kits$slot;
        if (slot >= 0 && slot < 9 && !isDuels) {
            player.getInventory().setItem(slot,
                    BukkitUtils.deserializeItemStack("BOW : 1 : display=" + Language.game$hotbar$kits$name));
        }
    }

    private void setupSpectatorInventory(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.spigot().setCollidesWithEntities(false);

        int slot = Language.game$hotbar$compass$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot,
                    BukkitUtils.deserializeItemStack("COMPASS : 1 : display=" + Language.game$hotbar$compass$name));
        }
        slot = Language.game$hotbar$play_again$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot,
                    BukkitUtils.deserializeItemStack("PAPER : 1 : display=" + Language.game$hotbar$play_again$name));
        }
        slot = Language.game$hotbar$quit_spectator$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot,
                    BukkitUtils.deserializeItemStack("BED : 1 : display=" + Language.game$hotbar$quit_spectator$name));
        }
    }

    public void refreshPlayers() {
        Player player = getPlayer();
        if (player == null) return;

        int slot = Language.lobby$hotbar$players$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack(
                    "INK_SACK:" + (canSeePlayers() ? "10" : "8") + " : 1 : display="
                            + (canSeePlayers() ? Language.lobby$hotbar$players$name_v : Language.lobby$hotbar$players$name_i)));
        }
        player.updateInventory();

        Database.getInstance().listAccounts().forEach(account -> {
            Player other = account.getPlayer();
            if (other == null) return;
            if (account.inLobby()) {
                if (canSeePlayers()) {
                    player.showPlayer(other);
                } else {
                    player.hidePlayer(other);
                }
                if (account.canSeePlayers()) {
                    other.showPlayer(player);
                } else {
                    other.hidePlayer(player);
                }
            } else {
                player.hidePlayer(other);
                other.hidePlayer(player);
            }
        });
    }

    // =========================================================================
    // PROGRESS BAR
    // =========================================================================

    public String makeProgressBar(boolean utf8) {
        Level level = Level.getByLevel(this.getLevel());
        double currentExp = this.getExp();
        double needExp = level.getNext() == null ? 0.0 : level.getNext().getExp();
        StringBuilder progressBar = new StringBuilder();
        double percentage = needExp <= 0.0 ? 100.0 : ((currentExp * 100.0) / needExp);

        double step = utf8 ? 10.0 : 2.5;
        String filledColor = utf8 ? "b" : "3";
        String emptyColor = utf8 ? "7" : "8";
        String symbol = utf8 ? "" : "|";

        boolean lastWasFilled = false;
        boolean hasColor = false;

        for (double d = step; d <= 100.0; d += step) {
            boolean filled = percentage >= d;
            if (filled && !lastWasFilled) {
                progressBar.append(filledColor);
                lastWasFilled = true;
                hasColor = true;
            } else if (!filled && (lastWasFilled || !hasColor)) {
                progressBar.append(emptyColor);
                lastWasFilled = false;
                hasColor = true;
            }
            progressBar.append(symbol);
        }
        return progressBar.toString();
    }

    // =========================================================================
    // SCOREBOARD
    // =========================================================================

    public void reloadScoreboard() {
        Player player = getPlayer();
        if (player == null) return;

        this.scoreboard = new LostScoreboard() {
            @Override
            public void update() {
                ArenaGroup group = arena != null ? arena.getGroup() : null;
                boolean hasOpponents = group != null && group.hasTrait("opponents_tracking");

                List<String> clone;
                if (arena == null) {
                    clone = new ArrayList<>(Language.scoreboards$lines$lobby);
                } else if (arena.getState().canJoin()) {
                    clone = new ArrayList<>(group.getScoreboardWaiting());
                } else {
                    clone = new ArrayList<>(group.getScoreboardIngame());
                }
                Collections.reverse(clone);

                for (int i = 0; i < clone.size(); i++) {
                    String line = clone.get(i);

                    if (SkyWars.placeholderapi) {
                        line = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(getPlayer(), line);
                    }

                    Arena currentServer = getArena();

                    if (currentServer == null) {
                        line = line.replace("{level}", Level.getByLevel(getLevel()).getLevel(Account.this));
                        line = line.replace("{kills}", getStatAcrossGroupsFormatted("kills", "solo", "doubles"));
                        line = line.replace("{wins}", getStatAcrossGroupsFormatted("wins", "solo", "doubles"));
                        line = line.replace("{solokills}", getStatFormatted("solo", "kills"));
                        line = line.replace("{solowins}", getStatFormatted("solo", "wins"));
                        line = line.replace("{teamkills}", getStatFormatted("doubles", "kills"));
                        line = line.replace("{teamwins}", getStatFormatted("doubles", "wins"));
                        line = line.replace("{rankedkills}", getStatFormatted("ranked_solo", "kills"));
                        line = line.replace("{rankedwins}", getStatFormatted("ranked_solo", "wins"));
                        line = line.replace("{rankedpoints}", getStatFormatted("ranked_solo", "elo"));
                        line = line.replace("{coins}", getCoinsFormatted());
                        line = line.replace("{souls}", getSoulsFormatted());
                        line = line.replace("{maxsouls}", StringUtils.formatNumber(getMaxSouls()));
                    } else {
                        line = line.replace("{date}", new SimpleDateFormat("MM/dd/yy").format(System.currentTimeMillis()));
                        line = line.replace("{world}", currentServer.isPrivate()
                                ? ChatColor.translateAlternateColorCodes('&', "&7[P]")
                                : currentServer.getName());
                        line = line.replace("{event}", currentServer.getEvent());
                        line = line.replace("{mode}", currentServer.getGroup().getDisplay());
                        line = line.replace("{map}", currentServer.getName());
                        line = line.replace("{on}", String.valueOf(currentServer.getAlive()));

                        if (hasOpponents && currentServer instanceof GameArena) {
                            GameArena gameArena = (GameArena) currentServer;
                            line = line.replace("{timeLeft}", new SimpleDateFormat("mm:ss").format((currentServer.getTimer()) * 1000));
                            line = line.replace("{kit}", "None");

                            if (line.contains("{opponent")) {
                                String opponents = gameArena.getOpponent(getPlayer());
                                if (opponents.isEmpty()) continue;
                                String[] parts = opponents.split("\n");
                                line = line.replace("{opponent}", parts[0]);
                                if (parts.length > 1) {
                                    line = line.replace("{opponent2}", parts[1]);
                                }
                            }
                        }

                        line = line.replace("{teams}", String.valueOf(currentServer.getAliveTeams().size()));
                        line = line.replace("{max}", String.valueOf(currentServer.getMaxPlayers()));
                        line = line.replace("{replace}", currentServer.getTimer() == (Language.game$countdown$start + 1)
                                ? Language.scoreboard$replace$waiting
                                : Language.scoreboard$replace$starting.replace("{time}", String.valueOf(currentServer.getTimer())));
                        line = line.replace("{kills}", String.valueOf(currentServer.getKills(getPlayer())));
                    }
                    this.add(i + 1, line);
                }
            }
        }.to(player).scroller(new ScoreboardScroller(Language.scoreboards$animation$title)).build();

        this.scoreboard.update();
        this.scoreboard.scroll();
    }

    // =========================================================================
    // SAVE / DESTROY
    // =========================================================================

    public void save() {
        Database.getInstance().saveProfile(id, profile);
        for (Map.Entry<String, Map<String, StatsContainer>> entry : statsPerGroup.entrySet()) {
            Database.getInstance().saveStats(id, entry.getKey(), entry.getValue());
        }
        Database.getInstance().saveCosmetics(id, cosmetics);
        Database.getInstance().saveSelections(id, selections);
    }

    public void destroy() {
        this.id = null;
        this.name = null;
        if (this.profile != null) {
            this.profile.clear();
            this.profile = null;
        }
        if (this.statsPerGroup != null) {
            this.statsPerGroup.clear();
            this.statsPerGroup = null;
        }
        if (this.cosmetics != null) {
            this.cosmetics.clear();
            this.cosmetics = null;
        }
        if (this.selections != null) {
            this.selections.clear();
            this.selections = null;
        }
        this.selectedContainer = null;
        this.scoreboard = null;
        this.lastHit.clear();
    }

    // =========================================================================
    // LAST HITTERS
    // =========================================================================

    public List<Account> getLastHitters() {
        long now = System.currentTimeMillis();
        List<Map.Entry<UUID, Long>> validEntries = new ArrayList<>();
        for (Entry<UUID, Long> entry : lastHit.entrySet()) {
            if (entry.getValue() > now) {
                validEntries.add(entry);
            }
        }
        validEntries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        List<Account> result = new ArrayList<>(validEntries.size());
        for (Map.Entry<UUID, Long> entry : validEntries) {
            Account acc = Database.getInstance().getAccount(entry.getKey());
            if (acc != null) {
                result.add(acc);
            }
        }
        return result;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    public LostScoreboard getScoreboard() {
        return scoreboard;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return id;
    }

    public Player getPlayer() {
        return id != null ? Bukkit.getPlayer(id) : null;
    }
}
