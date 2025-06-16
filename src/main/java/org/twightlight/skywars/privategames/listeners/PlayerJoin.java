package org.twightlight.skywars.privategames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.twightlight.skywars.privategames.PrivateGames;
import org.twightlight.skywars.privategames.PrivateGamesUser;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (PrivateGames.getStorage().getUser(e.getPlayer()) == null) {
            new PrivateGamesUser(e.getPlayer());
        }
    }
}
