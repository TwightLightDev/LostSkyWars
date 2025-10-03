package org.twightlight.skywars.hook.decenthologram.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.hook.decenthologram.User;
import org.twightlight.skywars.modules.recentgames.RecentGames;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        if (User.getFromPlayer(e.getPlayer()) == null)
            new User(e.getPlayer());
    }
}
