package org.twightlight.skywars.modules.boosters.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (PlayerUser.getFromUUID(e.getPlayer().getUniqueId()) == null) {
            new PlayerUser(e.getPlayer());
        }
    }
}
