package org.twightlight.skywars.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.event.game.SkyWarsChestRefillEvent;
import org.twightlight.skywars.api.event.game.SkyWarsDoomEvent;
import org.twightlight.skywars.arena.ui.enums.SkyWarsState;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.ui.chest.SkyWarsChest;
import org.twightlight.skywars.arena.ui.enums.SkyWarsEvent;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.kit.Kit;
import org.twightlight.skywars.cosmetics.kit.KitManager;
import org.twightlight.skywars.cosmetics.group.CosmeticsGroup;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsVictoryDance;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.nms.enums.Sound;
import org.twightlight.skywars.player.Account;

import java.util.ArrayList;
import java.util.List;

public class Timer {

    private final Arena arena;
    private BukkitTask task;

    public Timer(Arena server) {
        this.arena = server;
        this.reset();
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void showKitActionBar() {
        ArenaGroup group = arena.getGroup();
        boolean noKits = group != null && group.hasTrait("no_kits");
        arena.getPlayers(true).forEach(player -> {
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account != null) {
                if (noKits) {
                    NMS.sendActionBar(player,
                            Language.game$broadcast$starting$selected_kit.replace("{kit}", "None"));
                } else {
                    CosmeticsGroup cGroup = group != null ? group.getCosmeticsGroup() : null;
                    String cosmeticsGroupId = cGroup != null ? cGroup.getId() : "solo";
                    int selectedKitId = account.getSelectedContainer().getSelectedKit(cosmeticsGroupId);
                    Kit kit = selectedKitId > 0 ? KitManager.getById(selectedKitId) : null;
                    if (kit != null) {
                        NMS.sendActionBar(player, Language.game$broadcast$starting$selected_kit.replace("{kit}", kit.getRawName()));
                    } else {
                        NMS.sendActionBar(player,
                                Language.game$broadcast$starting$selected_kit.replace("{kit}", Language.options$cosmetic$default_kit));
                    }
                }
            }
        });
    }

    public void reset() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        this.task = new BukkitRunnable() {

            @Override
            public void run() {
                if (arena.getTimer() == 0) {
                    arena.start();
                    return;
                }

                showKitActionBar();

                if (arena.getOnline() < arena.getConfig().getMinPlayers()) {
                    if (arena.getTimer() != (Language.game$countdown$start + 1)) {
                        arena.setTimer(Language.game$countdown$start + 1);
                    }

                    arena.updateScoreboards();
                    return;
                }

                if (arena.getOnline() < 1) {
                    return;
                }

                if (arena.getTimer() == (Language.game$countdown$start + 1)) {
                    arena.setTimer(arena.getTimer() - 1);
                }

                if (arena.getTimer() == 10 || (arena.getTimer() <= 5 && arena.getTimer() > 0)) {
                    arena.getPlayers(true).forEach(player -> Sound.CLICK.play(player, 1.0F, 1.0F));
                    arena.broadcast(Language.game$broadcast$starting$start.replace("{s}", arena.getTimer() > 1 ? "s" : "").replace("{time}",
                            (arena.getTimer() <= 5 ? "§c" : arena.getTimer() <= 10 ? "§6" : "§a") + arena.getTimer()));

                    if (arena.getTimer() <= 5) {
                        arena.broadcastTitle(Language.game$broadcast$starting$title.replace("{time}", String.valueOf(arena.getTimer())),
                                Language.game$broadcast$starting$subtitle.replace("{time}", String.valueOf(arena.getTimer())));
                    }
                }

                arena.updateScoreboards();
                arena.setTimer(arena.getTimer() - 1);
            }
        }.runTaskTimer(SkyWars.getInstance(), 0, 20);
    }

    public void switchTask(Player... winners) {

        if (task != null) {
            task.cancel();
            task = null;
        }

        if (arena.getState() == SkyWarsState.STARTING) {
            for (Entity entity : arena.getWorld().getEntities()) {
                if (entity instanceof Player || entity instanceof ItemFrame) {
                    continue;
                }
                entity.remove();
            }
            arena.setTimer(10);
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (arena.getTimer() == 0) {
                        arena.start();
                        return;
                    }

                    showKitActionBar();

                    if (arena.getTimer() == 10 || (arena.getTimer() <= 5 && arena.getTimer() > 0)) {
                        arena.broadcast(Language.game$broadcast$starting$cage.replace("{s}", arena.getTimer() > 1 ? "s" : "").replace("{time}",
                                (arena.getTimer() <= 5 ? "§c" : arena.getTimer() <= 10 ? "§6" : "§a") + arena.getTimer()));
                    }

                    arena.updateScoreboards();
                    arena.setTimer(arena.getTimer() - 1);
                }
            }.runTaskTimer(SkyWars.getInstance(), 0, 20);
        } else if (arena.getState() == SkyWarsState.INGAME) {
            for (Entity entity : arena.getWorld().getEntities()) {
                if (entity instanceof Player || entity instanceof ItemFrame) {
                    continue;
                }
                entity.remove();
            }
            List<Integer> timeline = new ArrayList<>(arena.getTimeline().keySet());
            arena.setTimer(timeline.get(0));
            arena.getConfig().removeWaitingLobby();
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    int eventTime = arena.getEventTime(false);
                    if (arena.getTimer() == eventTime) {
                        if (arena.getTimer() == 0) {
                            arena.stop(null);
                            return;
                        }

                        if (arena.getTimeline().get(eventTime) == SkyWarsEvent.Refill) {
                            arena.chests.forEach(SkyWarsChest::fill);
                            arena.getPlayers(false).forEach(player -> {
                                Sound.CHEST_OPEN.play(player, 1.0F, 1.0F);
                                NMS.sendTitle(player, Language.game$player$ingame$titles$refill$up, Language.game$player$ingame$titles$refill$bottom, 10, 60, 10);
                            });
                            Bukkit.getPluginManager().callEvent(new SkyWarsChestRefillEvent(arena));
                            if (arena.getEventTime(true) == 0) {
                                arena.chests.forEach(SkyWarsChest::destroy);
                            }
                        } else if (arena.getTimeline().get(eventTime) == SkyWarsEvent.Doom) {
                            Location loc = arena.getConfig().getWorldCube().getCenterLocation();
                            loc.setY(arena.getConfig().getWorldCube().getYmax());
                            Entity enderDragon = loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (enderDragon.isDead()) {
                                        cancel();
                                        return;
                                    }

                                    Location dragonLoc = enderDragon.getLocation();
                                    int radius = 3;

                                    for (int x = -radius; x <= radius; x++) {
                                        for (int y = -radius; y <= radius; y++) {
                                            for (int z = -radius; z <= radius; z++) {
                                                Location checkLoc = dragonLoc.clone().add(x, y, z);
                                                Material blockType = checkLoc.getBlock().getType();

                                                if (blockType != Material.AIR && blockType.isSolid()) {
                                                    checkLoc.getBlock().setType(Material.AIR);
                                                }
                                            }
                                        }
                                    }
                                }
                            }.runTaskTimer(SkyWars.getInstance(), 0L, 1L);

                            for (Player p : arena.getPlayers(false)) {
                                arena.getPlayers(false).forEach(player -> {
                                    player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&cSudden Death"), "");
                                });
                                Sound.ENDERDRAGON_GROWL.play(p, 20.0F, 5.0F);
                            }
                            Bukkit.getPluginManager().callEvent(new SkyWarsDoomEvent(arena));
                        }

                    }

                    arena.updateScoreboards();
                    arena.chests.forEach(SkyWarsChest::update);
                    arena.setTimer(arena.getTimer() - 1);
                }
            }.runTaskTimer(SkyWars.getInstance(), 0, 20);
        } else if (arena.getState() == SkyWarsState.ENDED) {
            arena.setTimer(10);
            for (Player player : winners) {
                Account account = Database.getInstance().getAccount(player.getUniqueId());
                if (account == null) {
                    return;
                }
                int selectedVDId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.VICTORY_DANCE.getSelectionColumn());
                VisualCosmetic cos = selectedVDId > 0 ? VisualCosmetic.findByTypeAndId(VisualCosmeticType.VICTORY_DANCE, selectedVDId) : null;
                if (cos instanceof SkyWarsVictoryDance) {
                    SkyWarsVictoryDance cos1 = (SkyWarsVictoryDance) cos;
                    cos1.execute(player);
                }
            }
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (arena.getTimer() <= 0) {
                        for (Player player : arena.getPlayers(true)) {
                            Account account = Database.getInstance().getAccount(player.getUniqueId());
                            if (account == null) {
                                continue;
                            }

                            if (Core.MODE == CoreMode.MULTI_ARENA) {
                                account.setArena(null);
                                account.reloadScoreboard();
                                account.refreshPlayer();
                                account.refreshPlayers();
                            } else {
                                CoreLobbies.writeLobby(player);
                            }
                        }

                        if (Core.MODE == CoreMode.MULTI_ARENA) {
                            arena.reset();
                        } else {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart"), 40);
                        }
                        return;
                    }

                    arena.setTimer(arena.getTimer() - 1);
                }
            }.runTaskTimer(SkyWars.getInstance(), 0, 20);
        }
    }
}
