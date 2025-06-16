package org.twightlight.skywars.privategames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.game.SkyWarsGameEndEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerQuitEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.world.WorldServer;

public class ServerManagement implements Listener {

    @EventHandler
    public void onGameEnd(SkyWarsGameEndEvent e) {

        SkyWarsServer server1 = e.getServer();
        if (server1 instanceof WorldServer) {
            WorldServer<?> server = (WorldServer<?>) server1;
            if (server.isPrivate()) {
                WorldServer.removeArena(server);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(SkyWarsPlayerQuitEvent e) {

        SkyWarsServer server1 = e.getServer();
        if (server1 instanceof WorldServer) {
            WorldServer<?> server = (WorldServer<?>) server1;
            if (server.isPrivate() && server.getPlayers(false).isEmpty()) {
                WorldServer.removeArena(server);
            }
        }
    }
}
