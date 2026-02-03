package org.twightlight.skywars.modules.lobbysettings.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.lobbysettings.User;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerQuitEvent e) {
        User user = User.getFromUUID(e.getPlayer().getUniqueId());
        if (user == null) return;
        User.removeUser(user);
    }
}
