package org.twightlight.skywars.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.commands.sw.BuildCommand;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;

@SuppressWarnings("deprecation")
public class PlayerRestListener implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent evt) {
        Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
        if (account != null) {
            SkyWarsServer server = account.getArena();
            if (server == null) {
                evt.setCancelled(true);
            } else {
                if (server.getState() != SkyWarsState.INGAME || server.isSpectator(evt.getPlayer())) {
                    evt.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
        Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
        if (account != null) {
            SkyWarsServer server = account.getArena();
            if (server == null) {
                evt.setCancelled(true);
            } else {
                if (server.getState() != SkyWarsState.INGAME || server.isSpectator(evt.getPlayer())) {
                    evt.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
        if (account != null) {
            SkyWarsServer server = account.getArena();
            if (server == null) {
                evt.setCancelled(!BuildCommand.isBuilder(evt.getPlayer()));
            } else {
                if (server.getState() != SkyWarsState.INGAME || server.isSpectator(evt.getPlayer())) {
                    evt.setCancelled(true);
                } else {
                    Arena<?> ws = (Arena<?>) server;
                    if (ws.getConfig().isBalloon(BukkitUtils.serializeLocation(evt.getBlock().getLocation()))) {
                        evt.setCancelled(true);
                    } else if (!ws.getConfig().getWorldCube().contains(evt.getBlock().getLocation())) {
                        evt.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent evt) {
        Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
        if (account != null) {
            SkyWarsServer server = account.getArena();
            if (server == null) {
                evt.setCancelled(!BuildCommand.isBuilder(evt.getPlayer()));
            } else {
                if (server.getState() != SkyWarsState.INGAME || server.isSpectator(evt.getPlayer())) {
                    evt.setCancelled(true);
                } else if (!((Arena<?>) server).getConfig().getWorldCube().contains(evt.getBlock().getLocation())) {
                    evt.setCancelled(true);
                }
            }
        }
    }
}
