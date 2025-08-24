package org.twightlight.skywars.modules.lobbysettings.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;

public class PlayerJoinEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player p = e.getPlayer();
        LobbySettings.getDatabase().createPlayerData(p);
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> new User(p).enable(), 10L);
    }
}
