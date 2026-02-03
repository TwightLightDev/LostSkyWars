package org.twightlight.skywars.modules.recentgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.game.SkyWarsGameEndEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.modules.recentgames.GameData;
import org.twightlight.skywars.modules.recentgames.RecentGames;
import org.twightlight.skywars.modules.recentgames.User;
import org.twightlight.skywars.modules.recentgames.hook.ReplayData;

public class GameEndEvent implements Listener {

    @EventHandler
    public void onGameEnd(SkyWarsGameEndEvent e) {

        SkyWarsServer server1 = e.getServer();
        if (server1 instanceof Arena) {
            Arena<?> server = (Arena<?>) server1;
            if (!server.isPrivate()) {
                for (Player p : server.getInitialPlayers()) {
                    final ReplayData data1;
                    if (RecentGames.getReplayHook().getReplay(server) == null) {
                        data1 = null;
                    } else {
                        data1 = new ReplayData(server.getServerName(), RecentGames.getReplayHook().getReplay(server).getId(), RecentGames.getReplayHook().getReplayHolder(server).getWorldName());
                    }
                    GameData data = GameData.createGameData(p, server, e.getWinnerTeam().getMembers(), data1);
                    User user = User.getUser(p);
                    if (user != null) {
                        user.addGame(data, 10);
                    }
                }
                if (RecentGames.getReplayHook().getReplay(server) != null) {
                    RecentGames.getReplayHook().getReplay(server).getRecorder().stop(true);
                }
            }
        }
    }
}
