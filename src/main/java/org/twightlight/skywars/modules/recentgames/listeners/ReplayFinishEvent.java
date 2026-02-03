package org.twightlight.skywars.modules.recentgames.listeners;

import me.jumper251.replay.api.ReplaySessionFinishEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.recentgames.GameData;
import org.twightlight.skywars.modules.recentgames.User;

public class ReplayFinishEvent implements Listener {
    @EventHandler
    public void onReplayFinish(ReplaySessionFinishEvent e) {
        Player p = e.getPlayer();

        User user = User.getUser(p);
        if (user == null) return;

        GameData viewingGame = user.getViewingGame();

        if (viewingGame == null) return;

        World world = Bukkit.getWorld(viewingGame.getReplay().getWorldName());
        if (world == null || !world.getPlayers().isEmpty()) return;
        SkyWars.getInstance().getWorldLoader().deleteWorld(viewingGame.getReplay().getWorldName());
        user.setViewingGame(null);
    }
}
