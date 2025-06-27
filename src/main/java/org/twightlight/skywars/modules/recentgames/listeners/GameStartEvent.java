package org.twightlight.skywars.modules.recentgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.game.SkyWarsGameStartEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.modules.recentgames.RecentGames;
import org.twightlight.skywars.world.WorldServer;

public class GameStartEvent implements Listener {
    @EventHandler
    public void onGameStart(SkyWarsGameStartEvent e) {
        SkyWarsServer server1 = e.getServer();
        if (server1 instanceof WorldServer) {
            WorldServer<?> server = (WorldServer<?>) server1;
            if (!server.isPrivate() && RecentGames.hasReplayHook()) {
                RecentGames.getReplayHook().record(server);
            }
        }
    }
}
