package org.twightlight.skywars.modules.recentgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.recentgames.User;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerQuitEvent e) {
        User user = User.getUser(e.getPlayer());
        if (user == null) return;
        User.removeUser(user);
    }
}
