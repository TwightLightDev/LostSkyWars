package org.twightlight.skywars.modules.privategames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerQuitEvent;
import org.twightlight.skywars.arena.ui.enums.SkyWarsState;
import org.twightlight.skywars.arena.Arena;

public class ServerManagement implements Listener {

    @EventHandler
    public void onPlayerQuit(SkyWarsPlayerQuitEvent e) {

        Arena server = e.getServer();
        if (server.getState() == SkyWarsState.WAITING) {
            if (server.isPrivate() && server.getPlayers(false).isEmpty()) {
                Arena.removeArena(server);
            }
        }
    }
}
