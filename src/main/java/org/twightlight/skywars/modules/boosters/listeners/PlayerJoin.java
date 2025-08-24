package org.twightlight.skywars.modules.boosters.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.User;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (User.getFromUUID(e.getPlayer().getUniqueId()) == null) {
            new User(e.getPlayer());
        }
    }
}
