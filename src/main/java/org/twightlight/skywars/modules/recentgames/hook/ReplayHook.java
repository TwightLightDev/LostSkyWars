package org.twightlight.skywars.modules.recentgames.hook;

import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.Replay;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.recentgames.GameData;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.modules.recentgames.User;

import java.util.HashMap;
import java.util.Map;

public class ReplayHook {
    private Map<Arena<?>, ReplayHolder> replaysMap = new HashMap<>();

    public ReplayAPI getReplayAPI() {
        return ReplayAPI.getInstance();
    }

    public Replay getReplay(Arena<?> server) {
        if (replaysMap.getOrDefault(server, null) != null) {
            return replaysMap.get(server).getReplay();
        }
        return null;
    }

    public ReplayHolder getReplayHolder(Arena<?> server) {
        return replaysMap.getOrDefault(server, null);
    }

    public void record(Arena<?> server) {
        Replay replay = getReplayAPI().recordReplay(server.getServerName() + "_" + System.nanoTime(), server.getInitialPlayers());
        replaysMap.put(server, new ReplayHolder(replay, server.getConfig().getWorldName()));
    }

    public void play(GameData data, User p) {
        ReplayData replayData = data.getReplay();

        SkyWars.getInstance().getWorldLoader().createArenaWorld(replayData.getArenaId(), replayData.getWorldName()).thenAccept((world) -> {
            getReplayAPI().playReplay(replayData.getReplayId(), p.getPlayer());
        });
        p.setViewingGame(data);
    }
}
