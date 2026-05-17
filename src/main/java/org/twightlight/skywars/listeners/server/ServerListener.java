package org.twightlight.skywars.listeners.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.arena.ui.enums.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.bungee.core.Core;
import org.twightlight.skywars.bungee.core.CoreMode;

public class ServerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerListPing(ServerListPingEvent evt) {
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            if (Language.lobby$motd$enabled) {
                evt.setMotd(Language.lobby$motd$header + "\n" + Language.lobby$motd$footer);
            }
        } else {
            if (Core.MODE == CoreMode.LOBBY) {
                evt.setMotd("LOBBY; ");
            } else {
                Arena server = Arena.listArenas().stream().findFirst().orElse(null);
                if (server == null) {
                    evt.setMotd("");
                    return;
                }

                evt.setMotd("ARENA; " + server.getConfig().getMapName() + "; " + server.getState().name());
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent evt) {
        Arena server = Arena.getByWorldName(evt.getBlock().getWorld().getName());
        if (server == null) {
            evt.setCancelled(true);
        } else if (server.getState() != SkyWarsState.INGAME) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent evt) {
        Arena server = Arena.getByWorldName(evt.getBlock().getWorld().getName());
        if (server == null) {
            evt.setCancelled(true);
        } else if (server.getState() != SkyWarsState.INGAME) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent evt) {
        Arena server = Arena.getByWorldName(evt.getEntity().getWorld().getName());
        if (server == null) {
            evt.setCancelled(true);
        } else if (server.getState() != SkyWarsState.INGAME) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent evt) {
        evt.setCancelled(evt.toWeatherState());
    }
}
