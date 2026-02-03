package org.twightlight.skywars.modules.quests.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.quests.User;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerQuitEvent e) {
        User user = User.getUser(e.getPlayer());
        if (user == null) return;
        user.save();
        User.removeUser(user);
    }
}
