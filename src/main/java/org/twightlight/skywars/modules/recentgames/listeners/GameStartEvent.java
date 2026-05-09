package org.twightlight.skywars.modules.recentgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.game.SkyWarsGameStartEvent;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.modules.recentgames.RecentGames;

public class GameStartEvent implements Listener {
    @EventHandler
    public void onGameStart(SkyWarsGameStartEvent e) {
        Arena server = e.getServer();
            if (!server.isPrivate() && RecentGames.hasReplayHook()) {
                RecentGames.getReplayHook().record(server);
            }

    }
}
