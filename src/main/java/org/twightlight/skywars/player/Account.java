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
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsKit;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsCage;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.database.player.SelectedContainer;
import org.twightlight.skywars.database.player.StatsContainer;
import org.twightlight.skywars.player.level.Level;
import org.twightlight.skywars.player.rank.Rank;
import org.twightlight.skywars.player.ranked.Ranked;
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

    protected Map<String, StatsContainer> account, skywars, ranked;
    private Map<UUID, Long> lastHit = new HashMap<>();

    private static final String[] SKYWARS_JSON_FIELDS = {
            "deathcry", "trail", "killmessage", "spray", "ballons",
            "killeffect", "victorydance", "perks"
    };

    public Account(UUID id, String name) {
        this(id, name, false);
    }

    protected Account(UUID id, String name, boolean virtual) {
        this.id = id;
        this.name = name;

        if (!virtual) {
            this.account = Database.getInstance().loadStats(id, "lostedaccount", name);
            this.skywars = Database.getInstance().loadStats(id, "lostedskywars", name);
            this.ranked = Database.getInstance().loadStats(id, "ranked_lostedskywars", name);
        } else {
            this.account = buildDefaultAccountStats();
            this.skywars = buildDefaultSkyWarsStats();
            this.ranked = buildDefaultRankedStats();
        }

        if (this.account.get("leveling").get() == null) {
            this.account.get("leveling").set("[]");
        }

        for (String field : SKYWARS_JSON_FIELDS) {
            if (this.skywars.get(field).get() == null) {
                this.skywars.get(field).set("{}");
            }
        }
    }

    private static Map<String, StatsContainer> buildDefaultAccountStats() {
        Map<String, StatsContainer> map = new LinkedHashMap<>();
        map.put("lastRank", new StatsContainer("&7"));
        map.put("mysterydusts", new StatsContainer(0));
        map.put("sw_maxsouls", new StatsContainer(100));
        map.put("sw_wellroll", new StatsContainer(1));
        map.put("sw_soulswin", new StatsContainer(0));
        map.put("deliveries", new StatsContainer("{}"));
        map.put("leveling", new StatsContainer("[]"));
        map.put("players", new StatsContainer(true));
        map.put("gore", new StatsContainer(true));
        return map;
    }

    private static Map<String, StatsContainer> buildDefaultSkyWarsStats() {
        Map<String, StatsContainer> map = new LinkedHashMap<>();
        map.put("solokills", new StatsContainer(0));
        map.put("solowins", new StatsContainer(0));
        map.put("soloassists", new StatsContainer(0));
        map.put("solodeaths", new StatsContainer(0));
        map.put("solomelee", new StatsContainer(0));
        map.put("solobow", new StatsContainer(0));
        map.put("solomob", new StatsContainer(0));
        map.put("solovoid", new StatsContainer(0));
        map.put("soloplays", new StatsContainer(0));
        map.put("teamkills", new StatsContainer(0));
        map.put("teamwins", new StatsContainer(0));
        map.put("teamassists", new StatsContainer(0));
        map.put("teamdeaths", new StatsContainer(0));
        map.put("teammelee", new StatsContainer(0));
        map.put("teambow", new StatsContainer(0));
        map.put("teammob", new StatsContainer(0));
        map.put("teamvoid", new StatsContainer(0));
        map.put("teamplays", new StatsContainer(0));
        map.put("coins", new StatsContainer(0));
        map.put("souls", new StatsContainer(0));
        map.put("level", new StatsContainer(1));
        map.put("exp", new StatsContainer(0.0D));
        map.put("kits", new StatsContainer("{}"));
        map.put("perks", new StatsContainer("{}"));
        map.put("cages", new StatsContainer("{}"));
        map.put("deathcry", new StatsContainer("{}"));
        map.put("trail", new StatsContainer("{}"));
        map.put("killmessage", new StatsContainer("{}"));
        map.put("killeffect", new StatsContainer("{}"));
        map.put("spray", new StatsContainer("{}"));
        map.put("ballons", new StatsContainer("{}"));
        map.put("victorydance", new StatsContainer("{}"));
        map.put("title", new StatsContainer("{}"));
        map.put("selected", new StatsContainer("0:0:0 : 0"));
        map.put("lastSelected", new StatsContainer(0L));
        map.put("favorites", new StatsContainer("[]"));
        return map;
    }

    private static Map<String, StatsContainer> buildDefaultRankedStats() {
        Map<String, StatsContainer> map = new LinkedHashMap<>();
        map.put("kills", new StatsContainer(0));
        map.put("wins", new StatsContainer(0));
        map.put("assists", new StatsContainer(0));
        map.put("deaths", new StatsContainer(0));
        map.put("melee", new StatsContainer(0));
        map.put("bow", new StatsContainer(0));
        map.put("mob", new StatsContainer(0));
        map.put("void", new StatsContainer(0));
        map.put("plays", new StatsContainer(0));
        map.put("points", new StatsContainer(0));
        map.put("brave_points", new StatsContainer(0));
        return map;
    }

    @SuppressWarnings("unchecked")
    public void addLeveling(int level) {
        JSONArray array = this.account.get("leveling").getAsJsonArray();
        array.add(String.valueOf(level));
        this.account.get("leveling").set(array.toString());
    }

    @SuppressWarnings("unchecked")
    public void addFavoriteMap(String mapName) {
        JSONArray array = this.skywars.get("favorites").getAsJsonArray();
        array.add(mapName);
        this.skywars.get("favorites").set(array.toString());
    }

    public void removeFavoriteMap(String mapName) {
        JSONArray array = this.skywars.get("favorites").getAsJsonArray();
        array.remove(mapName);
        this.skywars.get("favorites").set(array.toString());
    }

    public void updateLastSelected() {
        this.skywars.get("lastSelected").set(TimeUtils.getExpireIn(1));
    }

    public boolean isLeveled(int level) {
        return this.account.get("leveling").getAsJsonArray().contains(String.valueOf(level));
    }

    public boolean isFavoriteMap(String mapName) {
        return this.skywars.get("favorites").getAsJsonArray().contains(mapName);
    }

    public boolean canSelectMap() {
        return this.skywars.get("lastSelected").getAsLong() < System.currentTimeMillis();
    }

    public void setArena(Arena arena) {
        this.arena = arena;
        this.lastHit.clear();
    }

    public void setHit(UUID id) {
        this.lastHit.put(id, System.currentTimeMillis() + 8000);
    }

    public void addMysteryDusts(int dusts) {
        if (SkyWars.lostboxes) {
            io.github.losteddev.boxes.api.LostBoxesAPI.addMysteryDusts(this.getPlayer(), dusts);
        }
    }

    public void addExp(double exp) {
        this.skywars.get("exp").addDouble(exp);
        Level current = Level.getByLevel(this.skywars.get("level").getAsInt());
        Level nextLevel = current.getNext();
        if (current.getExperienceUntil(this.getExp()) <= 0.0) {
            if (nextLevel != null) {
                this.skywars.get("level").addInt(1);
                this.skywars.get("exp").set(0.0D);
            }
        }
    }

    public void refreshPlayer() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }

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
        if (player == null) {
            return;
        }

        int slot = Language.lobby$hotbar$players$slot;
        if (slot >= 0 && slot < 9) {
            player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack(
                    "INK_SACK:" + (canSeePlayers() ? "10" : "8") + " : 1 : display="
                            + (canSeePlayers() ? Language.lobby$hotbar$players$name_v : Language.lobby$hotbar$players$name_i)));
        }
        player.updateInventory();

        Database.getInstance().listAccounts().forEach(account -> {
            Player other = account.getPlayer();
            if (other == null) {
                return;
            }

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

    public void reloadScoreboard() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }

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
                        line = line.replace("{kills}", getFormatted("solokills", "teamkills"));
                        line = line.replace("{wins}", getFormatted("solowins", "teamwins"));
                        line = line.replace("{solokills}", getFormatted("solokills"));
                        line = line.replace("{solowins}", getFormatted("solowins"));
                        line = line.replace("{teamkills}", getFormatted("teamkills"));
                        line = line.replace("{teamwins}", getFormatted("teamwins"));
                        line = line.replace("{rankedkills}", Ranked.getFormatted(Account.this, "kills"));
                        line = line.replace("{rankedwins}", Ranked.getFormatted(Account.this, "wins"));
                        line = line.replace("{rankedpoints}", Ranked.getFormatted(Account.this, "points"));
                        line = line.replace("{coins}", getFormatted("coins"));
                        line = line.replace("{souls}", getFormatted("souls"));
                        line = line.replace("{maxsouls}", StringUtils.formatNumber(account.get("sw_maxsouls").getAsInt()));
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
                                if (opponents.isEmpty()) {
                                    continue;
                                }

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

    public void addStat(String key) {
        this.addStat(key, 1);
    }

    public void addStat(String key, int amount) {
        if (SkyWars.vault && SkyWars.economy != null && key.equalsIgnoreCase("coins")) {
            ((net.milkbowl.vault.economy.Economy) SkyWars.economy).depositPlayer(this.getPlayer(), amount);
            return;
        }

        if (SkyWars.lostboxes && Rank.getRank(getPlayer()).receiveBox()
                && (key.endsWith("_plays") || key.equalsIgnoreCase("soloplays") || key.equalsIgnoreCase("teamplays"))) {
            io.github.losteddev.boxes.player.Account bc =
                    io.github.losteddev.boxes.database.Database.getInstance().getAccount(id);
            if (bc != null) {
                io.github.losteddev.boxes.api.box.Box box = io.github.losteddev.boxes.api.LostBoxesAPI.randomBox(7);
                bc.addBox(box);
                this.getPlayer().sendMessage(
                        Language.game$player$ingame$receive_box
                                .replace("{stars}", String.valueOf((int) box.getStars()))
                                .replace("{s}", box.getStars() >= 2.0 ? "s" : ""));
            }
        }

        this.skywars.get(key).addInt(amount);
    }

    public void removeStat(String key, int amount) {
        if (SkyWars.vault && SkyWars.economy != null && key.equalsIgnoreCase("coins")) {
            ((net.milkbowl.vault.economy.Economy) SkyWars.economy).withdrawPlayer(this.getPlayer(), amount);
            return;
        }

        this.skywars.get(key).removeInt(amount);
    }

    public void setCanSeePlayers(boolean flag) {
        this.account.get("players").set(flag);
    }

    public void setCanSeeBlood(boolean flag) {
        this.account.get("gore").set(flag);
    }

    public int getInt(String key) {
        if (SkyWars.vault && SkyWars.economy != null && key.equalsIgnoreCase("coins")) {
            return (int) ((net.milkbowl.vault.economy.Economy) SkyWars.economy).getBalance(this.getPlayer());
        }

        return skywars.get(key).getAsInt();
    }

    public String getString(String key) {
        return skywars.get(key).getAsString();
    }

    public String getFormatted(String... keys) {
        int amount = 0;
        for (String key : keys) {
            amount += this.getInt(key);
        }

        return StringUtils.formatNumber(amount);
    }

    public int getIntegers(String... keys) {
        int amount = 0;
        for (String key : keys) {
            amount += this.getInt(key);
        }

        return amount;
    }

    public Map<String, StatsContainer> getContainer(String field) {
        if (field.equals("skywars")) {
            return skywars;
        } else if (field.equals("ranked")) {
            return ranked;
        }

        return account;
    }

    public void setSelected(Cosmetic cosmetic) {
        this.setSelected(cosmetic, 1);
    }

    public void setSelected(Cosmetic cosmetic, int index) {
        this.setSelected(cosmetic.getServer(), cosmetic.getType(), index, cosmetic.getId());
    }

    public void setSelected(CosmeticServer server, CosmeticType type, int index, int id) {
        SelectedContainer container = getContainer(server.name().toLowerCase()).get("selected").getSelected(server);
        container.set(type, index, String.valueOf(id));
        getContainer(server.name().toLowerCase()).get("selected").set(container.build());
    }

    public Cosmetic getSelected(CosmeticServer server, CosmeticType type, int index) {
        Cosmetic c = Cosmetic.findFrom(server, type, index,
                getContainer(server.name().toLowerCase()).get("selected").getSelected(server).get(type, index));
        if (c != null) {
            if (c instanceof SkyWarsKit) {
                if (!((SkyWarsKit) c).has(this)) {
                    setSelected(server, type, index, 0);
                    return null;
                }
            } else if (c instanceof SkyWarsCage) {
                if (!c.has(this)) {
                    setSelected(server, type, index, 0);
                    return null;
                }
            }
        }

        return c;
    }

    public boolean hasSelected(Cosmetic cosmetic) {
        return hasSelected(cosmetic, 1);
    }

    public boolean hasSelected(Cosmetic cosmetic, int index) {
        Cosmetic c = this.getSelected(cosmetic.getServer(), cosmetic.getType(), index);
        return c != null && c.equals(cosmetic);
    }

    public boolean inLobby() {
        return arena == null;
    }

    public Arena getArena() {
        return arena;
    }

    public int getLevel() {
        return skywars.get("level").getAsInt();
    }

    public double getExp() {
        return skywars.get("exp").getAsDouble();
    }

    public int getMysteryDusts() {
        return account.get("mysterydusts").getAsInt();
    }

    public boolean canSeePlayers() {
        return account.get("players").getAsBoolean();
    }

    public boolean canSeeBlood() {
        return account.get("gore").getAsBoolean();
    }

    public void save() {
        Database.getInstance().saveStats(id, "lostedaccount", account);
        Database.getInstance().saveStats(id, "lostedskywars", skywars);
        Database.getInstance().saveStats(id, "ranked_lostedskywars", ranked);
    }

    public void destroy() {
        this.id = null;
        this.name = null;
        if (this.skywars != null) {
            this.skywars.clear();
            this.skywars = null;
        }
        if (this.account != null) {
            this.account.clear();
            this.account = null;
        }
        if (this.ranked != null) {
            this.ranked.clear();
            this.ranked = null;
        }
        this.scoreboard = null;
        this.lastHit.clear();
    }

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
