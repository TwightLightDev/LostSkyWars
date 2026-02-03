package org.twightlight.skywars.modules.quests.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.quests.User;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        if (User.getUser(e.getPlayer()) == null) {
            new User(e.getPlayer());
        }
    }
}
