package org.twightlight.skywars.modules.recentgames.listeners;

import me.jumper251.replay.replaysystem.recording.PlayerWatcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.modules.recentgames.RecentGames;
import org.twightlight.skywars.world.WorldServer;

import java.util.HashMap;

public class PlayerDeathEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(SkyWarsPlayerDeathEvent e) {
        SkyWarsServer server1 = e.getServer();
        if (server1 instanceof WorldServer) {
            WorldServer<?> server = (WorldServer<?>) server1;
            if (!server.isPrivate() && RecentGames.hasReplayHook() && RecentGames.getReplayHook().getReplay(server) != null) {
                HashMap<String, PlayerWatcher> watchers = RecentGames.getReplayHook().getReplay(server).getData().getWatchers();
                watchers.remove(e.getPlayer().getName());
                RecentGames.getReplayHook().getReplay(server).getData().setWatchers(watchers);
            }
        }
    }
}
