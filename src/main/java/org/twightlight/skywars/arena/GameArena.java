package org.twightlight.skywars.arena;

import me.leoo.guilds.bukkit.manager.GuildsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.event.game.SkyWarsGameEndEvent;
import org.twightlight.skywars.api.event.game.SkyWarsGameStartEvent;
import org.twightlight.skywars.api.event.player.*;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent.SkyWarsDeathCause;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.ui.chest.SkyWarsChest;
import org.twightlight.skywars.arena.ui.interfaces.ScanCallback;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsKit;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.sprays.Spray;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsCage;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsDeathCry;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.nms.Sound;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.CurrencyManager;
import org.twightlight.skywars.player.rank.Rank;
import org.twightlight.skywars.utils.FontUtils;
import org.twightlight.skywars.utils.PlayerUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GameArena extends Arena {

    private List<UUID> players;
    private List<UUID> spectators;
    private Map<UUID, Integer> kills;
    private Map<UUID, CurrencyManager> dataContainer;
    private Map<UUID, String> opponents;

    public GameArena(String yaml, ScanCallback callback, boolean isPrivate) {
        super(yaml, callback, isPrivate);
        this.kills = new HashMap<>();
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.dataContainer = new HashMap<>();
        this.opponents = new HashMap<>();
    }

    private void recordKillStats(Account killerAccount, String statSuffix) {
        if (!isPrivate && group.hasTrait("has_stats")) {
            killerAccount.addStat(group.getId(), statSuffix);
        }
        if (!isPrivate && group.hasTrait("has_elo")) {
            int eloPerKill = group.getRewardInt("elo-per-kill");
            int killCount = getKills(killerAccount.getPlayer());
            int eloAmount = (int) (eloPerKill + killCount * eloPerKill * 0.05);
            killerAccount.addStat(group.getId(), "elo", eloAmount);
        }
    }

    private void recordDeathStats(Account account) {
        if (!isPrivate && group.hasTrait("has_stats")) {
            account.addStat(group.getId(), "deaths");
            account.addStat(group.getId(), "plays");
        }
    }

    private void giveKillRewards(Player killer, Account killerAccount) {
        int coinsPerKill = group.getRewardInt("coins-per-kill");
        double expPerKill = group.getReward("exp-per-kill");
        dataContainer.get(killer.getUniqueId()).addCoins(coinsPerKill, SkyWarsPlayerCoinEarnEvent.CoinSource.KILL);
        dataContainer.get(killer.getUniqueId()).addXp(expPerKill, SkyWarsPlayerXpGainEvent.XpSource.KILL);
        if (killerAccount.getSouls() < killerAccount.getMaxSouls()) {
            dataContainer.get(killer.getUniqueId()).addSouls(1);
        }
    }

    private void givePlayRewards(Player player) {
        int coinsPerPlay = group.getRewardInt("coins-per-play");
        double expPerPlay = group.getReward("exp-per-play");
        dataContainer.get(player.getUniqueId()).addCoins(coinsPerPlay, SkyWarsPlayerCoinEarnEvent.CoinSource.PLAY);
        dataContainer.get(player.getUniqueId()).addXp(expPerPlay, SkyWarsPlayerXpGainEvent.XpSource.PLAY);
    }

    public void killLeave(Account account, Account ack, boolean byMob) {
        Player player = account.getPlayer();
        Player killer = ack != null ? ack.getPlayer() : null;
        if (killer != null && player.equals(killer)) killer = null;

        SkyWarsDeathCause cause;
        String killMessage;
        if (killer == null) {
            if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == DamageCause.VOID) {
                cause = SkyWarsDeathCause.SUICIDE_VOID;
                killMessage = PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$suicide$void);
            } else {
                if (!byMob) {
                    cause = SkyWarsDeathCause.SUICIDE;
                    killMessage = PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$suicide$normal);
                } else {
                    cause = SkyWarsDeathCause.KILLED_MOB;
                    killMessage = PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$killed$mob);
                }
            }
        } else {
            addKills(killer);
            recordKillStats(ack, "kills");
            if (byMob) {
                cause = SkyWarsDeathCause.KILLED_MOB;
                recordKillStats(ack, "mob_kills");
                killMessage = PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$mob);
            } else if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == DamageCause.VOID) {
                cause = SkyWarsDeathCause.KILLED_VOID;
                recordKillStats(ack, "void_kills");
                killMessage = PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$void);
            } else if (player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent
                    && ((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof Arrow) {
                cause = SkyWarsDeathCause.KILLED_BOW;
                recordKillStats(ack, "bow_kills");
                killMessage = PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$bow);
            } else {
                cause = SkyWarsDeathCause.KILLED_MELEE;
                recordKillStats(ack, "melee_kills");
                killMessage = PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$normal);
            }
            giveKillRewards(killer, ack);
        }

        recordDeathStats(account);
        givePlayRewards(player);

        int deathCryId = account.getSelectedContainer().getGlobalSelection("death_cry");
        if (deathCryId > 0) {
            Cosmetic cry = Cosmetic.findFrom(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_DEATHCRY, 1, String.valueOf(deathCryId));
            if (cry != null && cry instanceof SkyWarsDeathCry) {
                ((SkyWarsDeathCry) cry).getSound().play(player.getLocation(), ((SkyWarsDeathCry) cry).getVolume(), ((SkyWarsDeathCry) cry).getPitch());
            }
        }

        SkyWarsPlayerDeathEvent deathEvent = new SkyWarsPlayerDeathEvent(this, player, killer, cause, killMessage);
        Bukkit.getPluginManager().callEvent(deathEvent);
        broadcast(deathEvent.getKillMessage());
        this.broadcastAction(Language.game$broadcast$ingame$action_bar$remaining.replace("{alive}", String.valueOf(this.getAlive())));
        this.updateTags();
        this.check();
    }

    @Override
    public void kill(Account account, Account ack, boolean byMob) {
        Player player = account.getPlayer();
        SkyWarsTeam team = getTeam(player);

        if (!isAlive(player) || team == null) {
            account.refreshPlayer();
            return;
        }

        Player killer = ack != null ? ack.getPlayer() : null;
        if (killer != null && player.equals(killer)) killer = null;

        SkyWarsDeathCause cause;
        String killMessage;
        if (killer == null) {
            if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == DamageCause.VOID) {
                cause = SkyWarsDeathCause.SUICIDE_VOID;
                killMessage = PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$suicide$void);
            } else {
                if (!byMob) {
                    cause = SkyWarsDeathCause.SUICIDE;
                    killMessage = PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$suicide$normal);
                } else {
                    cause = SkyWarsDeathCause.KILLED_MOB;
                    killMessage = PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$killed$mob);
                }
            }
        } else {
            addKills(killer);
            recordKillStats(ack, "kills");
            if (byMob) {
                cause = SkyWarsDeathCause.KILLED_MOB;
                recordKillStats(ack, "mob_kills");
                killMessage = PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$mob);
            } else if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == DamageCause.VOID) {
                cause = SkyWarsDeathCause.KILLED_VOID;
                recordKillStats(ack, "void_kills");
                killMessage = PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$void);
            } else if (player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent
                    && ((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof Arrow) {
                cause = SkyWarsDeathCause.KILLED_BOW;
                recordKillStats(ack, "bow_kills");
                killMessage = PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$bow);
            } else {
                cause = SkyWarsDeathCause.KILLED_MELEE;
                recordKillStats(ack, "melee_kills");
                killMessage = PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$normal);
            }
            giveKillRewards(killer, ack);
        }

        recordDeathStats(account);
        givePlayRewards(player);

        Location returns = team.getLocation();
        Location dieLocation = player.getLocation();
        team.removeMember(player);
        players.remove(player.getUniqueId());
        spectators.add(player.getUniqueId());

        for (Player p : getPlayers(true)) {
            if (isSpectator(p)) {
                player.showPlayer(p);
            } else {
                p.hidePlayer(player);
            }
        }

        final Player killerFinal = killer;
        Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> {
            player.teleport(returns);
            account.refreshPlayer();
            NMS.sendTitle(player,
                    killerFinal != null ? PlayerUtils.replaceAll(killerFinal, Language.game$player$ingame$titles$die$up_killed) : Language.game$player$ingame$titles$die$up,
                    killerFinal != null ? PlayerUtils.replaceAll(killerFinal, Language.game$player$ingame$titles$die$bottom_killed) : Language.game$player$ingame$titles$die$bottom,
                    20, 60, 20);
            if (SkyWars.guilds && GuildsManager.getByPlayer(player) != null) {
                int gxp = dataContainer.get(player.getUniqueId()).getGxpEarned();
                GuildsManager.getByPlayer(player).getLevel().addXp(gxp);
            }
            for (String line : Language.game$player$ingame$reward_summary) {
                line = line.replace("{totalCoins}", "" + dataContainer.get(player.getUniqueId()).getCoinsEarned());
                line = line.replace("{totalExp}", "" + dataContainer.get(player.getUniqueId()).getXpEarned());
                if (line.contains("{totalGExp}")) {
                    if (SkyWars.guilds && GuildsManager.getByPlayer(player) != null) {
                        line = line.replace("{totalGExp}", "" + dataContainer.get(player.getUniqueId()).getGxpEarned());
                    } else {
                        continue;
                    }
                }
                line = line.replace("{totalSouls}", "" + dataContainer.get(player.getUniqueId()).getSoulsEarned());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
        }, 3);

        int deathCryId = account.getSelectedContainer().getGlobalSelection("death_cry");
        if (deathCryId > 0) {
            Cosmetic cry = Cosmetic.findFrom(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_DEATHCRY, 1, String.valueOf(deathCryId));
            if (cry != null && cry instanceof SkyWarsDeathCry) {
                ((SkyWarsDeathCry) cry).getSound().play(dieLocation, ((SkyWarsDeathCry) cry).getVolume(), ((SkyWarsDeathCry) cry).getPitch());
            }
        }

        SkyWarsPlayerDeathEvent deathEvent = new SkyWarsPlayerDeathEvent(this, player, killer, cause, killMessage);
        Bukkit.getPluginManager().callEvent(deathEvent);
        broadcast(deathEvent.getKillMessage());
        this.broadcastAction(Language.game$broadcast$ingame$action_bar$remaining.replace("{alive}", String.valueOf(this.getAlive())));
        this.updateTags();
        this.check();
    }

    @Override
    public void spectate(Account account, Player target) {
        Player player = account.getPlayer();
        account.setArena(this);
        spectators.add(player.getUniqueId());
        account.refreshPlayer();
        player.teleport(target.getLocation());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getWorld().equals(player.getWorld())) {
                player.hidePlayer(p);
                p.hidePlayer(player);
                continue;
            }
            if (!isSpectator(p)) {
                player.showPlayer(p);
                p.hidePlayer(player);
            } else {
                player.showPlayer(p);
                p.showPlayer(player);
            }
        }
        Bukkit.getPluginManager().callEvent(new SkyWarsPlayerWatchEvent(this, player, target));
        this.updateTags();
    }

    @Override
    public void connect(Account account, String... skipParty) {
        Player player = account.getPlayer();
        if (player == null || !getState().canJoin() || players.size() >= getMaxPlayers()) return;
        if (account.getArena() != null && account.getArena().equals(this)) return;

        if (skipParty.length == 0) {
            if (SkyWars.lostparties) {
                io.github.losteddev.parties.api.Party party = io.github.losteddev.parties.api.Party.getPartyByMember(player);
                if (party != null) {
                    if (!party.getOwnerName().equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Language.lobby$connecting$party$not_leader);
                        return;
                    }
                    if (party.online() + players.size() > getMaxPlayers()) return;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> party.getPlayers(false).forEach(member -> {
                        Account accounts = Database.getInstance().getAccount(member.getUniqueId());
                        if (accounts != null) connect(accounts, "");
                    }), 5L);
                }
            }
        }

        SkyWarsTeam team = getAvailableTeam(player);
        if (team == null) return;

        if (account.getArena() != null) {
            account.getArena().disconnect(account, account.getArena().isSpectator(player) ? "-play" : "");
        }

        players.add(player.getUniqueId());
        account.setArena(this);

        int cageId = account.getSelectedContainer().getGlobalSelection("cage");
        if (cageId > 0) {
            Cosmetic cosmetic = Cosmetic.findFrom(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_CAGE, 1, String.valueOf(cageId));
            if (cosmetic != null && cosmetic instanceof SkyWarsCage && cosmetic.has(account)) {
                ((SkyWarsCage) cosmetic).apply(account.getPlayer(), team.getLocation());
            } else {
                SkyWarsCage.defaultCage(team.getLocation(), false);
            }
        } else {
            SkyWarsCage.defaultCage(team.getLocation(), false);
        }
        team.setCageOwner(account.getUniqueId());

        player.teleport(this.getConfig().hasWaitingLobby() ? this.getConfig().getWaitingLocation() : team.getLocation().add(0, 1, 0));
        account.reloadScoreboard();
        account.refreshPlayer();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getWorld().equals(player.getWorld())) {
                player.hidePlayer(p);
                p.hidePlayer(player);
                continue;
            }
            if (isSpectator(p)) {
                player.hidePlayer(p);
                p.showPlayer(player);
            } else {
                player.showPlayer(p);
                p.showPlayer(player);
            }
        }

        Bukkit.getPluginManager().callEvent(new SkyWarsPlayerJoinEvent(this, player));
        this.updateTags();
        NMS.sendTitle(player,
                Language.game$player$ingame$title$join$up.replace("{type_color}", StringUtils.getFirstColor(group.getColoredName())).replace("{type}", group.getStrippedName()),
                Language.game$player$ingame$title$join$down.replace("{type_color}", StringUtils.getFirstColor(group.getColoredName())).replace("{type}", group.getStrippedName()));
        this.broadcast(PlayerUtils.replaceAll(player,
                Language.game$broadcast$starting$join.replace("{on}", String.valueOf(this.getOnline())).replace("{max}", String.valueOf(this.getMaxPlayers()))));
        if (getTimer() > Language.game$countdown$full && this.getOnline() == this.getMaxPlayers()) {
            this.setTimer(Language.game$countdown$full);
        }
    }

    @Override
    public void disconnect(Account account) {
        this.disconnect(account, "");
    }

    @Override
    public void disconnect(Account account, String options) {
        Player player = account.getPlayer();
        if (!account.getArena().equals(this)) return;

        SkyWarsTeam team = getTeam(player);
        if (team != null) {
            if (this.getState().canJoin()) team.destroy();
            team.removeMember(player);
        }

        boolean alive = players.contains(player.getUniqueId());
        players.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());

        if (options.equals("-quit")) {
            if (this.getState().canJoin()) {
                this.broadcast(PlayerUtils.replaceAll(player,
                        Language.game$broadcast$starting$left.replace("{on}", String.valueOf(this.getOnline())).replace("{max}", String.valueOf(this.getMaxPlayers()))));
            }
            if (alive && state == SkyWarsState.INGAME) {
                List<Account> hitters = account.getLastHitters();
                Account killer = hitters.size() > 0 ? hitters.get(0) : null;
                this.killLeave(account, killer, false);
                for (Account hitter : hitters) {
                    if (hitter != null && (killer == null || !hitter.equals(killer))
                            && (hitter.getArena() != null && hitter.getArena().equals(this))
                            && hitter.getPlayer() != null && !this.isSpectator(hitter.getPlayer())) {
                        if (!isPrivate() && group.hasTrait("has_stats")) hitter.addStat(group.getId(), "assists");
                    }
                }
            }
            account.setArena(null);
            this.check();
            return;
        }

        if (options.equalsIgnoreCase("-play")) {
            account.setArena(null);
            this.updateTags();
            this.check();
            return;
        }

        if (alive && state == SkyWarsState.INGAME) {
            List<Account> hitters = account.getLastHitters();
            Account killer = hitters.size() > 0 ? hitters.get(0) : null;
            this.killLeave(account, killer, false);
            for (Account hitter : hitters) {
                if (hitter != null && (killer == null || !hitter.equals(killer))
                        && (hitter.getArena() != null && hitter.getArena().equals(this))
                        && hitter.getPlayer() != null && !this.isSpectator(hitter.getPlayer())) {
                    if (!isPrivate() && group.hasTrait("has_stats")) hitter.addStat(group.getId(), "assists");
                }
            }
        }

        account.setArena(null);
        this.updateTags();
        account.reloadScoreboard();
        account.refreshPlayer();
        account.refreshPlayers();
        if (this.getState().canJoin()) {
            this.broadcast(PlayerUtils.replaceAll(player,
                    Language.game$broadcast$starting$left.replace("{on}", String.valueOf(this.getOnline())).replace("{max}", String.valueOf(this.getMaxPlayers()))));
        }
        Bukkit.getPluginManager().callEvent(new SkyWarsPlayerQuitEvent(this, player));
        this.check();
    }

    @Override
    public void start() {
        if (this.getConfig().hasWaitingLobby()) {
            if (this.getState() == SkyWarsState.WAITING) {
                this.setState(SkyWarsState.STARTING);
                this.timerTask.switchTask();
                for (Player player : getPlayers(false)) {
                    Account account = Database.getInstance().getAccount(player.getUniqueId());
                    if (account == null) {
                        player.kickPlayer("clSKY WARS\n \ncError.");
                    } else {
                        account.reloadScoreboard();
                        account.refreshPlayer();
                        player.teleport(this.getTeam(player).getLocation().add(0, 1, 0));
                    }
                }
                return;
            }
        }
        this.setState(SkyWarsState.INGAME);
        applyPrivateSettings();
        this.timerTask.switchTask();

        for (Entity e : config.getWorldCube().getEntities()) {
            if (e instanceof ItemFrame) {
                Spray.createSpray((ItemFrame) e);
            }
        }

        List<String> sb = new ArrayList<>();
        String tutorial = group.getTutorial();

        if (group.hasTrait("opponents_tracking")) {
            StringBuilder opponentNames = new StringBuilder();
            List<Player> playerList = getPlayers(false);
            for (int i = 0; i < playerList.size(); i++) {
                if (i > 0) opponentNames.append("7, ");
                opponentNames.append(playerList.get(i).getDisplayName());
            }
            tutorial = tutorial.replace("{opponents}", opponentNames.toString());
            tutorial = tutorial.replace("{s}", playerList.size() > 2 ? "s" : "");

            for (Player player : playerList) {
                StringBuilder oppStr = new StringBuilder();
                for (Player other : playerList) {
                    if (!other.equals(player)) {
                        if (oppStr.length() > 0) oppStr.append("\n");
                        oppStr.append(other.getDisplayName());
                    }
                }
                opponents.put(player.getUniqueId(), oppStr.toString());
            }
        }

        for (String line : tutorial.split("\n")) {
            if (line.startsWith("{centered}")) {
                line = FontUtils.center(line.replace("{centered}", ""));
            }
            sb.add(line);
        }
        this.broadcast(StringUtils.join(sb, "\n"));

        this.broadcastTitle(
                Language.game$broadcast$started$title.replace("{type_color}", StringUtils.getFirstColor(group.getColoredName()))
                        .replace("{type}", group.getStrippedName().toUpperCase()),
                Language.game$broadcast$started$subtitle.replace("{type_color}", StringUtils.getFirstColor(group.getColoredName()))
                        .replace("{type}", group.getStrippedName().toUpperCase()));
        sb.clear();

        teams.forEach(SkyWarsTeam::destroy);
        chests.forEach(SkyWarsChest::fill);

        for (Player player : getPlayers(false)) {
            kills.put(player.getUniqueId(), 0);
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null) {
                player.kickPlayer("clSKY WARS\n \ncError.");
            } else {
                account.reloadScoreboard();
                account.refreshPlayer();
                dataContainer.put(player.getUniqueId(), new CurrencyManager(account));

                int kitId = account.getSelectedContainer().getSelectedKit(group.getId());
                if (kitId > 0) {
                    Cosmetic cosmeticKit = Cosmetic.findFrom(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KIT, 1, String.valueOf(kitId));
                    if (cosmeticKit != null && cosmeticKit instanceof SkyWarsKit && ((SkyWarsKit) cosmeticKit).has(account)) {
                        ((SkyWarsKit) cosmeticKit).apply(player);
                    } else {
                        applyDefaultKit(player);
                    }
                } else {
                    applyDefaultKit(player);
                }

                player.setNoDamageTicks(80);
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 1));
                Sound.PORTAL_TRIGGER.play(player, 1.0F, 1.0F);
            }
        }

        this.setInitialPlayers(getPlayers(false));
        this.startTime = System.nanoTime();
        this.startTimeMillis = System.currentTimeMillis();
        Bukkit.getPluginManager().callEvent(new SkyWarsGameStartEvent(this));
        this.updateTags();
        this.check();
    }

    private void applyDefaultKit(Player player) {
        if (Language.options$game$default_kit && !group.hasTrait("no_default_kit")) {
            player.getInventory().addItem(
                    new ItemStack(Material.matchMaterial("WOOD_PICKAXE")),
                    new ItemStack(Material.matchMaterial("WOOD_AXE")),
                    new ItemStack(Material.matchMaterial("WOOD_SPADE")));
        }
    }

    private void check() {
        if (this.getState() != SkyWarsState.INGAME) return;

        List<SkyWarsTeam> aliveTeams = this.getAliveTeams();
        if (aliveTeams.size() <= 1) {
            if (aliveTeams.size() == 0) {
                this.stop(null);
                Bukkit.getPluginManager().callEvent(new SkyWarsGameEndEvent(this, null));
                return;
            }

            this.setState(SkyWarsState.ENDED);
            SkyWarsTeam winner = aliveTeams.get(0);
            Player winnerPlayer = winner.getMembers().get(0);

            this.getPlayers(true).forEach(p -> {
                boolean loser = !winner.hasMember(p);
                if (loser) {
                    NMS.sendTitle(p, Language.game$player$ingame$titles$loser$up,
                            PlayerUtils.replaceAll(winnerPlayer, Language.game$player$ingame$titles$loser$bottom), 20, 80, 20);
                } else {
                    NMS.sendTitle(p, Language.game$player$ingame$titles$winner$up,
                            Language.game$player$ingame$titles$winner$bottom, 20, 80, 20);
                }
                Database.getInstance().getAccount(p.getUniqueId()).getScoreboard().update();
            });

            this.stop(winner);

            for (Player wPlayer : winner.getMembers()) {
                if (this.isAlive(wPlayer)) {
                    this.players.remove(wPlayer.getUniqueId());
                    this.spectators.add(wPlayer.getUniqueId());
                    Account wAccount = Database.getInstance().getAccount(wPlayer.getUniqueId());
                    wAccount.refreshPlayer();

                    if (!isPrivate && group.hasTrait("has_stats")) {
                        wAccount.addStat(group.getId(), "wins");
                        wAccount.addStat(group.getId(), "plays");
                    }
                    if (!isPrivate && group.hasTrait("has_elo")) {
                        int eloPerWin = group.getRewardInt("elo-per-win");
                        wAccount.addStat(group.getId(), "elo", eloPerWin);
                    }

                    int coinsPerWin = group.getRewardInt("coins-per-win");
                    int coinsPerPlay = group.getRewardInt("coins-per-play");
                    double expPerWin = group.getReward("exp-per-win");
                    double expPerPlay = group.getReward("exp-per-play");

                    dataContainer.get(wPlayer.getUniqueId()).addCoins(coinsPerPlay, SkyWarsPlayerCoinEarnEvent.CoinSource.PLAY);
                    dataContainer.get(wPlayer.getUniqueId()).addXp(expPerPlay, SkyWarsPlayerXpGainEvent.XpSource.PLAY);
                    dataContainer.get(wPlayer.getUniqueId()).addCoins(coinsPerWin, SkyWarsPlayerCoinEarnEvent.CoinSource.WIN);
                    dataContainer.get(wPlayer.getUniqueId()).addXp(expPerWin, SkyWarsPlayerXpGainEvent.XpSource.WIN);

                    for (int i = 0; i < wAccount.getSoulsPerWin(); i++) {
                        if (wAccount.getSouls() < wAccount.getMaxSouls()) {
                            dataContainer.get(wPlayer.getUniqueId()).addSouls(1);
                        }
                    }

                    if (SkyWars.guilds && GuildsManager.getByPlayer(wPlayer) != null) {
                        int gxp = dataContainer.get(wPlayer.getUniqueId()).getGxpEarned();
                        GuildsManager.getByPlayer(wPlayer).getLevel().addXp(gxp);
                    }

                    final Player fWinner = wPlayer;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> {
                        for (String line : Language.game$player$ingame$reward_summary) {
                            line = line.replace("{totalCoins}", "" + dataContainer.get(fWinner.getUniqueId()).getCoinsEarned());
                            line = line.replace("{totalExp}", "" + dataContainer.get(fWinner.getUniqueId()).getXpEarned());
                            if (line.contains("{totalGExp}")) {
                                if (SkyWars.guilds && GuildsManager.getByPlayer(fWinner) != null) {
                                    line = line.replace("{totalGExp}", "" + dataContainer.get(fWinner.getUniqueId()).getGxpEarned());
                                } else {
                                    continue;
                                }
                            }
                            line = line.replace("{totalSouls}", "" + dataContainer.get(fWinner.getUniqueId()).getSoulsEarned());
                            fWinner.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                    }, 20);
                }
            }

            Bukkit.getPluginManager().callEvent(new SkyWarsGameEndEvent(this, winner));
        }
    }

    @Override
    public void stop(SkyWarsTeam winner) {
        this.setState(SkyWarsState.ENDED);

        List<String> sb = new ArrayList<>();
        List<UUID> keys = kills.keySet().stream()
                .sorted(Comparator.comparing(parent -> kills.get(parent), Comparator.reverseOrder()))
                .collect(Collectors.toList());
        while (keys.size() < 3) {
            keys.add(null);
        }

        Player winnerPlayer = (winner != null && !winner.getMembers().isEmpty()) ? winner.getMembers().get(0) : null;

        for (String line : Language.game$player$ingame$leader_board$template.split("\n")) {
            line = line.replace("{winner}", winnerPlayer == null ? "7None" : winnerPlayer.getDisplayName());
            line = line.replace("{top1}", keys.get(0) != null ? Bukkit.getPlayer(keys.get(0)).getDisplayName() : "7None");
            line = line.replace("{top2}", keys.get(1) != null ? Bukkit.getPlayer(keys.get(1)).getDisplayName() : "7None");
            line = line.replace("{top3}", keys.get(2) != null ? Bukkit.getPlayer(keys.get(2)).getDisplayName() : "7None");
            line = line.replace("{kills_top1}", String.valueOf(this.getKills(keys.get(0))));
            line = line.replace("{kills_top2}", String.valueOf(this.getKills(keys.get(1))));
            line = line.replace("{kills_top3}", String.valueOf(this.getKills(keys.get(2))));
            if (line.startsWith("{centered}")) {
                line = FontUtils.center(line.replace("{centered}", ""));
            }
            sb.add(line);
        }
        this.broadcast(StringUtils.join(sb, "\n"));
        sb.clear();

        Player[] winners = (winner != null) ? winner.getMembers().toArray(new Player[0]) : new Player[0];
        this.getTimerTask().switchTask(winners);
    }

    @Override
    public void reset() {
        this.kills.clear();
        this.players.clear();
        this.spectators.clear();
        this.dataContainer.clear();
        this.opponents.clear();
        this.getTimerTask().cancel();
        this.initialPlayers.clear();
        Collections.shuffle(teamcolors);
        this.teams.forEach(SkyWarsTeam::reset);
        this.chests.forEach(SkyWarsChest::destroy);
        RollBackManager.rollBack(this);
    }

    @Override
    public void broadcast(String message) {
        this.broadcast(message, true);
    }

    @Override
    public void broadcastAction(String message) {
        getPlayers(true).forEach(player -> NMS.sendActionBar(player, message));
    }

    @Override
    public void broadcastTitle(String title, String subtitle) {
        getPlayers(true).forEach(player -> NMS.sendTitle(player, title, subtitle, 0, 60, 0));
    }

    @Override
    public void broadcast(String message, boolean includeSpectators) {
        getPlayers(includeSpectators).forEach(player -> player.sendMessage(StringUtils.formatColors(message)));
    }

    @Override
    public void updateScoreboards() {
        getPlayers(true).forEach(player -> {
            if (this.getState() != SkyWarsState.WAITING && this.getState() != SkyWarsState.STARTING
                    && !this.getConfig().getWorldCube().contains(player.getLocation())) {
                if (this.isSpectator(player)) {
                    player.teleport(this.getConfig().getWorldCube().getCenterLocation());
                } else if (player.getLocation().getY() > 1) {
                    NMS.sendTitle(player, Language.game$player$ingame$titles$border$up,
                            Language.game$player$ingame$titles$border$bottom, 0, 20, 0);
                    player.damage(1.0D);
                }
            }
            Database.getInstance().getAccount(player.getUniqueId()).getScoreboard().update();
        });
    }

    private void updateTags() {
        if (this.getState().canJoin()) {
            for (Player player : getPlayers(true)) {
                Scoreboard scoreboard = player.getScoreboard();
                for (Player other : getPlayers(true)) {
                    Team team = scoreboard.getTeam(other.getUniqueId().toString().replace("-", "").substring(0, 16));
                    if (team == null) {
                        team = scoreboard.registerNewTeam(other.getUniqueId().toString().replace("-", "").substring(0, 16));
                    }
                    team.setPrefix(StringUtils.getLastColor(Rank.getRank(other).getPrefix()));
                    if (!team.hasEntry(other.getName())) team.addEntry(other.getName());
                }
            }
            return;
        }

        for (Player player : getPlayers(true)) {
            Scoreboard scoreboard = player.getScoreboard();
            for (Player other : getPlayers(true)) {
                if (isSpectator(other)) {
                    Team team = scoreboard.getTeam(other.getUniqueId().toString().replace("-", "").substring(0, 16));
                    if (team != null) team.unregister();
                    team = scoreboard.getTeam("spec");
                    if (team == null) {
                        team = scoreboard.registerNewTeam("spec");
                        team.setPrefix("7");
                    }
                    if (!team.hasEntry(other.getName())) team.addEntry(other.getName());
                } else {
                    Team team = scoreboard.getTeam(other.getUniqueId().toString().replace("-", "").substring(0, 16));
                    if (team == null) {
                        SkyWarsTeam st = getTeam(other);
                        team = scoreboard.registerNewTeam(other.getUniqueId().toString().replace("-", "").substring(0, 16));
                        team.setPrefix(st.hasMember(player) ? "a" : "c");
                        team.addEntry(other.getName());
                    }
                }
            }
        }
    }

    public String getOpponent(Player player) {
        String opp = opponents.get(player.getUniqueId());
        return opp != null ? opp : "";
    }

    @Override
    public String getServerName() {
        return config.getId();
    }

    @Override
    public boolean isAlive(Player player) {
        return players.contains(player.getUniqueId());
    }

    @Override
    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public void addKills(Player player) {
        kills.put(player.getUniqueId(), getKills(player) + 1);
    }

    @Override
    public int getKills(Player player) {
        return kills.get(player.getUniqueId()) != null ? kills.get(player.getUniqueId()) : 0;
    }

    private int getKills(UUID uuid) {
        return kills.get(uuid) != null ? kills.get(uuid) : 0;
    }

    @Override
    public int getOnline() {
        return players.size() + spectators.size();
    }

    @Override
    public int getAlive() {
        return players.size();
    }

    @Override
    public List<Player> getPlayers(boolean includeSpectators) {
        List<Player> result = new ArrayList<>(includeSpectators ? this.spectators.size() + this.players.size() : this.players.size());
        this.players.stream().filter(id -> Bukkit.getPlayer(id) != null).forEach(id -> result.add(Bukkit.getPlayer(id)));
        if (includeSpectators) {
            this.spectators.stream().filter(id -> Bukkit.getPlayer(id) != null).forEach(id -> result.add(Bukkit.getPlayer(id)));
        }
        return result;
    }
}
