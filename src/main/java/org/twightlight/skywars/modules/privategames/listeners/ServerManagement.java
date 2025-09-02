package org.twightlight.skywars.modules.privategames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerQuitEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.Arena;

public class ServerManagement implements Listener {

    @EventHandler
    public void onPlayerQuit(SkyWarsPlayerQuitEvent e) {

        SkyWarsServer server1 = e.getServer();
        if (server1 instanceof Arena && server1.getState() == SkyWarsState.WAITING) {
            Arena<?> server = (Arena<?>) server1;
            if (server.isPrivate() && server.getPlayers(false).isEmpty()) {
                Arena.removeArena(server);
            }
        }
    }
}
