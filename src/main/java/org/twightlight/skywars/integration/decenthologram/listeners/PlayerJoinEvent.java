package org.twightlight.skywars.integration.decenthologram.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.integration.decenthologram.User;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        if (User.getFromPlayer(e.getPlayer()) == null)
            new User(e.getPlayer());
    }
}
