package org.twightlight.skywars.modules.privategames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.privategames.PrivateGames;
import org.twightlight.skywars.modules.privategames.User;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerQuitEvent e) {

        User user = PrivateGames.getStorage().getUser(e.getPlayer());
        if (user == null) return;
        PrivateGames.getStorage().removeUser(user);

    }
}
