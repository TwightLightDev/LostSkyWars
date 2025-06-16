package org.twightlight.skywars.world;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.api.event.game.SkyWarsChestRefillEvent;
import org.twightlight.skywars.api.event.game.SkyWarsDoomEvent;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.nms.Sound;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.ui.SkyWarsChest;
import org.twightlight.skywars.ui.SkyWarsEvent;
import org.twightlight.skywars.ui.SkyWarsType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldTimer {

    private final WorldServer<?> server;
    private BukkitTask task;

    public WorldTimer(WorldServer<?> server) {
        this.server = server;
        this.reset();
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void reset() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        this.task = new BukkitRunnable() {

            @Override
            public void run() {
                if (server.getTimer() == 0) {
                    server.start();
                    return;
                }

                server.getPlayers(true).forEach(player -> {
                    Account account = Database.getInstance().getAccount(player.getUniqueId());
                    if (account != null) {
                        Cosmetic c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KIT, server.getType().getIndex());
                        if (c != null) {
                            NMS.sendActionBar(player, Language.game$broadcast$starting$selected_kit.replace("{kit}", c.getRawName()));
                        } else {
                            NMS.sendActionBar(player,
                                    Language.game$broadcast$starting$selected_kit.replace("{kit}", server.getType().equals(SkyWarsType.DUELS) ? "None" : Language.options$cosmetic$default_kit));
                        }
                    }
                });
                if (server.getOnline() < server.getConfig().getMinPlayers()) {
                    if (server.getTimer() != (Language.game$countdown$start + 1)) {
                        server.setTimer(Language.game$countdown$start + 1);
                    }

                    server.updateScoreboards();
                    return;
                }

                if (server.getOnline() < 1) {
                    return;
                }

                if (server.getTimer() == (Language.game$countdown$start + 1)) {
                    server.setTimer(server.getTimer() - 1);
                }

                if (server.getTimer() == 10 || (server.getTimer() <= 5 && server.getTimer() > 0)) {
                    server.getPlayers(true).forEach(player -> Sound.CLICK.play(player, 1.0F, 1.0F));
                    server.broadcast(Language.game$broadcast$starting$start.replace("{s}", server.getTimer() > 1 ? "s" : "").replace("{time}",
                            (server.getTimer() <= 5 ? "§c" : server.getTimer() <= 10 ? "§6" : "§a") + server.getTimer()));

                    if (server.getTimer() <= 5) {
                        server.broadcastTitle(Language.game$broadcast$starting$title.replace("{time}", String.valueOf(server.getTimer())),
                                Language.game$broadcast$starting$subtitle.replace("{time}", String.valueOf(server.getTimer())));
                    }
                }

                server.updateScoreboards();
                server.setTimer(server.getTimer() - 1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void switchTask(Player... winners) {

        if (task != null) {
            task.cancel();
            task = null;
        }

        if (server.getState() == SkyWarsState.STARTING) {
            for (Entity entity : server.getWorld().getEntities()) {
                if (entity instanceof Player || entity instanceof ItemFrame) {
                    continue;
                }

                entity.remove();
            }
            server.setTimer(10);
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (server.getTimer() == 0) {
                        server.start();
                        return;
                    }

                    server.getPlayers(true).forEach(player -> {
                        Account account = Database.getInstance().getAccount(player.getUniqueId());
                        if (account != null) {
                            Cosmetic c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KIT, server.getType().getIndex());
                            if (c != null) {
                                NMS.sendActionBar(player, Language.game$broadcast$starting$selected_kit.replace("{kit}", c.getRawName()));
                            } else {
                                NMS.sendActionBar(player,
                                        Language.game$broadcast$starting$selected_kit.replace("{kit}", server.getType().equals(SkyWarsType.DUELS) ? "None" : Language.options$cosmetic$default_kit));
                            }
                        }
                    });

                    if (server.getTimer() == 10 || (server.getTimer() <= 5 && server.getTimer() > 0)) {
                        server.broadcast(Language.game$broadcast$starting$cage.replace("{s}", server.getTimer() > 1 ? "s" : "").replace("{time}",
                                (server.getTimer() <= 5 ? "§c" : server.getTimer() <= 10 ? "§6" : "§a") + server.getTimer()));
                    }

                    server.updateScoreboards();
                    server.setTimer(server.getTimer() - 1);
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        } else if (server.getState() == SkyWarsState.INGAME) {
            for (Entity entity : server.getWorld().getEntities()) {
                if (entity instanceof Player || entity instanceof ItemFrame) {
                    continue;
                }

                entity.remove();
            }
            List<Integer> timeline = new ArrayList<>(server.getTimeline().keySet());
            server.setTimer(server.getType().equals(SkyWarsType.DUELS) ? Language.game$countdown$game_duels : timeline.get(0));
            server.getConfig().removeWaitingLobby();
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    int eventTime = server.getEventTime(false);
                    if (server.getTimer() == eventTime) {
                        if (server.getTimer() == 0) {
                            server.stop(null);
                            return;
                        }

                        if (!server.getType().equals(SkyWarsType.DUELS)) {
                            if (server.getTimeline().get(eventTime) == SkyWarsEvent.Refill) {
                                server.chests.forEach(SkyWarsChest::refill);
                                server.getPlayers(false).forEach(player -> {
                                    Sound.CHEST_OPEN.play(player, 1.0F, 1.0F);
                                    NMS.sendTitle(player, Language.game$player$ingame$titles$refill$up, Language.game$player$ingame$titles$refill$bottom, 10, 60, 10);
                                });
                                Bukkit.getPluginManager().callEvent(new SkyWarsChestRefillEvent(server));
                                if (server.getNextEventTime() == 0) {
                                    server.chests.forEach(SkyWarsChest::destroy);
                                }
                            } else if (server.getTimeline().get(eventTime) == SkyWarsEvent.Doom) {
                                Location loc = server.getConfig().getWorldCube().getCenterLocation();
                                loc.setY(server.getConfig().getWorldCube().getYmax());
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
                                }.runTaskTimer(Main.getInstance(), 0L, 1L);


                                for (Player p : server.getPlayers(false)) {

                                    server.getPlayers(false).forEach(player -> {
                                        player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&cSudden Death"), "");
                                            });
                                        Sound.ENDERDRAGON_GROWL.play(p, 20.0F, 5.0F);
                                }
                                Bukkit.getPluginManager().callEvent(new SkyWarsDoomEvent(server));
                            }

                        }
                    }

                    server.updateScoreboards();
                    server.chests.forEach(SkyWarsChest::update);
                    server.setTimer(server.getTimer() - 1);
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        } else if (server.getState() == SkyWarsState.ENDED) {
            server.setTimer(10);
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (server.getTimer() <= 0) {
                        for (Player player : server.getWorld().getPlayers()) {
                            Account account = Database.getInstance().getAccount(player.getUniqueId());
                            if (account == null) {
                                continue;
                            }

                            if (Core.MODE == CoreMode.MULTI_ARENA) {
                                account.setServer(null);
                                account.reloadScoreboard();
                                account.refreshPlayer();
                                account.refreshPlayers();
                            } else {
                                CoreLobbies.writeLobby(player);
                            }
                        }

                        if (Core.MODE == CoreMode.MULTI_ARENA) {
                            server.reset();
                        } else {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart"), 40);
                        }
                        return;
                    }

                    for (Player player : winners) {
                        if (player != null && player.getWorld() != null && player.getWorld().equals(server.getWorld())) {
                            Firework fire = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                            FireworkMeta meta = fire.getFireworkMeta();
                            int random = new Random().nextInt(5) + 1;
                            Color color = random == 1 ? Color.BLUE : random == 2 ? Color.RED : random == 3 ? Color.GREEN : random == 4 ? Color.MAROON : Color.ORANGE;
                            meta.addEffect(FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.STAR).build());
                            meta.setPower(1);
                            fire.setFireworkMeta(meta);
                        }
                    }

                    server.setTimer(server.getTimer() - 1);
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
    }
}
