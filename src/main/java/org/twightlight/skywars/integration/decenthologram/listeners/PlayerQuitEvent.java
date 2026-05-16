package org.twightlight.skywars.integration.decenthologram.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.integration.decenthologram.User;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerQuitEvent e) {
        User user = User.getFromPlayer(e.getPlayer());
        if (user == null) return;
        User.removeUser(user);
    }
}
