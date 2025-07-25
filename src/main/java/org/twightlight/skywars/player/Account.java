package org.twightlight.skywars.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.cmd.sw.SetLobbyCommand;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsKit;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsCage;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.database.player.SelectedContainer;
import org.twightlight.skywars.database.player.StatsContainer;
import org.twightlight.skywars.level.Level;
import org.twightlight.skywars.rank.Rank;
import org.twightlight.skywars.ranked.Ranked;
import org.twightlight.skywars.scoreboard.LostScoreboard;
import org.twightlight.skywars.scoreboard.ScoreboardScroller;
import org.twightlight.skywars.ui.SkyWarsMode;
import org.twightlight.skywars.ui.SkyWarsType;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;
import org.twightlight.skywars.utils.TimeUtils;
import org.twightlight.skywars.world.type.DuelsServer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("deprecation")
public class Account {

    private UUID id;
    private String name;
    private LostScoreboard scoreboard;

    private SkyWarsServer server;

    private Map<String, StatsContainer> account, skywars, ranked;
    private Map<UUID, Long> lastHit = new HashMap<>();

    public Account(UUID id, String name) {
        this.id = id;
        this.name = name;

        this.account = Database.getInstance().loadStats(id, "lostedaccount", name);
        this.skywars = Database.getInstance().loadStats(id, "lostedskywars", name);
        this.ranked = Database.getInstance().loadStats(id, "ranked_lostedskywars", name);

        if (this.account.get("leveling").get() == null) {
            this.account.get("leveling").set("[]");
        }
        if (this.skywars.get("deathcry").get() == null) {
            this.skywars.get("deathcry").set("{}");
        }
        if (this.skywars.get("trail").get() == null) {
            this.skywars.get("trail").set("{}");
        }
        if (this.skywars.get("killmessage").get() == null) {
            this.skywars.get("killmessage").set("{}");
        }
        if (this.skywars.get("spray").get() == null) {
            this.skywars.get("spray").set("{}");
        }
        if (this.skywars.get("ballons").get() == null) {
            this.skywars.get("ballons").set("{}");
        }
        if (this.skywars.get("killeffect").get() == null) {
            this.skywars.get("killeffect").set("{}");
        }
        if (this.skywars.get("victorydance").get() == null) {
            this.skywars.get("victorydance").set("{}");
        }
        if (this.skywars.get("perks").get() == null) {
            this.skywars.get("perks").set("{}");
        }
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

    public void setServer(SkyWarsServer server) {
        this.server = server;
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
        Level current = Level.getByLevel(this.skywars.get("level").getAsInt()), nextLevel = current.getNext();
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
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(player.hasPermission("lostskywars.lobby.fly"));
            int slot = Language.lobby$hotbar$profile$slot;
            if (slot < 9 && slot > -1) {
                player.getInventory().setItem(slot,
                        BukkitUtils.putProfileOnSkull(player, BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : display=" + Language.lobby$hotbar$profile$name)));
            }

            slot = Language.lobby$hotbar$shop$slot;
            if (slot < 9 && slot > -1) {
                player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack("EMERALD : 1 : display=" + Language.lobby$hotbar$shop$name));
            }

            slot = Language.lobby$hotbar$players$slot;
            if (slot < 9 && slot > -1) {
                player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack(
                        "INK_SACK:" + (canSeePlayers() ? "10" : "8") + " : 1 : display=" + (canSeePlayers() ? Language.lobby$hotbar$players$name_v : Language.lobby$hotbar$players$name_i)));
            }

            Rank.getRank(player).apply(player);
            player.teleport(SetLobbyCommand.getSpawnLocation());
            if (Language.lobby$speed$enabled) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, Language.lobby$speed$level - 1));
            }
            if (Language.lobby$jump_boost$enabled) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, Language.lobby$jump_boost$level - 1));
            }
        } else if (server.getState().canJoin()) {
            player.setGameMode(GameMode.ADVENTURE);
            int slot = Language.game$hotbar$kits$slot;
            if (slot < 9 && slot > -1) {
                if (server.getType() != SkyWarsType.DUELS) {
                    player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack("BOW : 1 : display=" + Language.game$hotbar$kits$name));
                }
            }

            slot = Language.game$hotbar$quit$slot;
            if (slot < 9 && slot > -1) {
                player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack("BED : 1 : display=" + Language.game$hotbar$quit$name));
            }
        } else if (server.getState() == SkyWarsState.STARTING) {
            player.setGameMode(GameMode.ADVENTURE);
            int slot = Language.game$hotbar$kits$slot;
            if (slot < 9 && slot > -1) {
                if (server.getType() != SkyWarsType.DUELS) {
                    player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack("BOW : 1 : display=" + Language.game$hotbar$kits$name));
                }
            }
        } else if (server.isSpectator(player)) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.spigot().setCollidesWithEntities(false);

            int slot = Language.game$hotbar$compass$slot;
            if (slot < 9 && slot > -1) {
                player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack("COMPASS : 1 : display=" + Language.game$hotbar$compass$name));
            }

            slot = Language.game$hotbar$play_again$slot;
            if (slot < 9 && slot > -1) {
                player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack("PAPER : 1 : display=" + Language.game$hotbar$play_again$name));
            }

            slot = Language.game$hotbar$quit_spectator$slot;
            if (slot < 9 && slot > -1) {
                player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack("BED : 1 : display=" + Language.game$hotbar$quit_spectator$name));
            }
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }

        player.updateInventory();
    }

    public void refreshPlayers() {
        Player player = getPlayer();

        int slot = Language.lobby$hotbar$players$slot;
        if (slot < 9 && slot > -1) {
            player.getInventory().setItem(slot, BukkitUtils.deserializeItemStack(
                    "INK_SACK:" + (canSeePlayers() ? "10" : "8") + " : 1 : display=" + (canSeePlayers() ? Language.lobby$hotbar$players$name_v : Language.lobby$hotbar$players$name_i)));
        }
        player.updateInventory();

        Database.getInstance().listAccounts().forEach(account -> {
            Player players = account.getPlayer();

            if (account.inLobby()) {
                if (canSeePlayers()) {
                    player.showPlayer(players);
                } else {
                    player.hidePlayer(players);
                }

                if (account.canSeePlayers()) {
                    players.showPlayer(player);
                } else {
                    players.hidePlayer(player);
                }
            } else {
                player.hidePlayer(players);
                players.hidePlayer(player);
            }
        });
    }

    public String makeProgressBar(boolean utf8) {
        Level level = Level.getByLevel(this.getLevel());
        double currentExp = this.getExp();
        double needExp = level.getNext() == null ? 0.0 : level.getNext().getExp();
        StringBuilder progressBar = new StringBuilder();
        double percentage = currentExp >= needExp ? 100.0 : ((currentExp * 100.0) / needExp);

        boolean higher = false, hasColor = false;
        for (double d = (utf8 ? 10.0 : 2.5); d <= 100.0; d += (utf8 ? 10.0 : 2.5)) {
            if (!higher && percentage >= d) {
                progressBar.append((utf8 ? "§b" : "§3"));
                higher = true;
                hasColor = true;
            } else if ((higher || !hasColor) && percentage < d) {
                higher = false;
                hasColor = true;
                progressBar.append((utf8 ? "§7" : "§8"));
            }

            progressBar.append(percentage >= d ? (utf8 ? "■" : "|") : (utf8 ? "■" : "|"));
        }

        return progressBar.toString();
    }

    public void reloadScoreboard() {
        this.scoreboard = new LostScoreboard() {
            @Override
            public void update() {
                this.updateHealth();
                List<String> clone = new ArrayList<>(server == null ? Language.scoreboards$lines$lobby
                        : server.getState().canJoin() ? server.getType().equals(SkyWarsType.DUELS) ? Language.scoreboards$lines$waiting_duels : Language.scoreboards$lines$waiting
                        : server.getMode().equals(SkyWarsMode.SOLO)
                        ? server.getType().equals(SkyWarsType.DUELS) ? Language.scoreboards$lines$ingame_duels : Language.scoreboards$lines$ingame
                        : server.getType().equals(SkyWarsType.DUELS) ? Language.scoreboards$lines$ingame_duels_doubles : Language.scoreboards$lines$ingame_doubles);
                Collections.reverse(clone);
                for (int i = 0; i < clone.size(); i++) {
                    String line = clone.get(i);
                    if (SkyWars.placeholderapi) {
                        line = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(getPlayer(), line);
                    }

                    SkyWarsServer server = getServer();
                    if (server == null) {
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
                        line = line.replace("{world}", server.getName());
                        line = line.replace("{event}", server.getEvent());
                        line = line.replace("{mode}", server.getType().getColoredName());
                        line = line.replace("{map}", server.getName());
                        line = line.replace("{on}", String.valueOf(server.getAlive()));

                        //
                        if (server instanceof DuelsServer) {
                            DuelsServer duels = (DuelsServer) server;
                            line = line.replace("{timeLeft}", new SimpleDateFormat("mm:ss").format((server.getTimer()) * 1000));
                            line = line.replace("{kit}", "None");

                            if (line.contains("{opponent")) {
                                String opponents = duels.getOpponent(getPlayer());
                                if (opponents.isEmpty()) {
                                    continue;
                                }

                                line = line.replace("{opponent}", opponents.split("\n")[0]);
                                if (opponents.split("\n").length > 1) {
                                    line = line.replace("{opponent2}", opponents.split("\n")[1]);
                                }
                            }
                        }
                        //

                        line = line.replace("{teams}", String.valueOf(server.getAliveTeams().size()));
                        line = line.replace("{max}", String.valueOf(server.getMaxPlayers()));
                        line = line.replace("{replace}", server.getTimer() == (Language.game$countdown$start + 1) ? Language.scoreboard$replace$waiting
                                : Language.scoreboard$replace$starting.replace("{time}", String.valueOf(server.getTimer())));
                        line = line.replace("{kills}", String.valueOf(server.getKills(getPlayer())));
                    }
                    this.add(i + 1, line);
                }
            }
        }.to(this.getPlayer()).scroller(new ScoreboardScroller(Language.scoreboards$animation$title)).build();
        this.scoreboard.update();
        if (!this.inLobby()) {
            this.scoreboard.health();
            if (this.server.getState() == SkyWarsState.INGAME) {
                this.scoreboard.healthTab();
            }
        }
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

        if (SkyWars.lostboxes && Rank.getRank(getPlayer()).receiveBox() && (key.equalsIgnoreCase("soloplays") || key.equalsIgnoreCase("teamplays"))) {
            io.github.losteddev.boxes.player.Account bc = io.github.losteddev.boxes.database.Database.getInstance().getAccount(id);
            if (bc != null) {
                io.github.losteddev.boxes.api.box.Box box = io.github.losteddev.boxes.api.LostBoxesAPI.randomBox(7);
                bc.addBox(box);
                this.getPlayer()
                        .sendMessage(Language.game$player$ingame$receive_box.replace("{stars}", String.valueOf((int) box.getStars())).replace("{s}", box.getStars() >= 2.0 ? "s" : ""));
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

    public Map<String, StatsContainer> getContainers(String field) {
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
        SelectedContainer container = getContainers(server.name().toLowerCase()).get("selected").getSelected(server);
        container.set(type, index, String.valueOf(id));
        getContainers(server.name().toLowerCase()).get("selected").set(container.build());
    }

    public Cosmetic getSelected(CosmeticServer server, CosmeticType type, int index) {
        Cosmetic c = Cosmetic.findFrom(server, type, index, getContainers(server.name().toLowerCase()).get("selected").getSelected(server).get(type, index));
        if (c != null) {
            if (c instanceof SkyWarsKit) {
                if (!c.has(this, index) || !((SkyWarsKit) c).hasByPermission(getPlayer())) {
                    setSelected(server, type, index, 0);
                    return null;
                }
            } else if (c instanceof SkyWarsCage) {
                if (!c.has(this) || !((SkyWarsCage) c).hasByPermission(getPlayer())) {
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
        return server == null;
    }

    public SkyWarsServer getServer() {
        return server;
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
        this.skywars.clear();
        this.skywars = null;
        this.scoreboard = null;
    }

    public List<Account> getLastHitters() {
        List<String> order = new ArrayList<>(lastHit.size());
        long start = System.currentTimeMillis();
        for (Entry<UUID, Long> entry : lastHit.entrySet()) {
            if (entry.getValue() > start) {
                order.add(entry.getKey() + " " + entry.getValue());
            }
        }

        order.sort((arg0, arg1) -> Long.compare(Long.parseLong(arg1.split(" ")[1]), Long.parseLong(arg0.split(" ")[1])));
        List<Account> result = new ArrayList<>(order.size());
        Account account = null;
        for (String player : order) {
            if ((account = Database.getInstance().getAccount(UUID.fromString(player.split(" ")[0]))) != null) {
                result.add(account);
            }
        }

        order.clear();
        account = null;
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
        return Bukkit.getPlayer(id);
    }
}
