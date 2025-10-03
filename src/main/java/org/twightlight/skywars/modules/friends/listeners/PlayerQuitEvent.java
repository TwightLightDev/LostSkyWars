package org.twightlight.skywars.modules.friends.listeners;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.friends.Friends;

public class PlayerQuitEvent implements Listener {
    private Friends module;

    public PlayerQuitEvent(Friends module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerQuitEvent e) {
        Player user = e.getPlayer();
        this.module.getUserManager().uncacheUser(user);

    }
}
