package org.twightlight.skywars.modules.boosters.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerQuitEvent e) {
        if (PlayerUser.getFromUUID(e.getPlayer().getUniqueId()) != null) {
            PlayerUser.removeUser(PlayerUser.getFromUUID(e.getPlayer().getUniqueId()));
        }
    }
}
