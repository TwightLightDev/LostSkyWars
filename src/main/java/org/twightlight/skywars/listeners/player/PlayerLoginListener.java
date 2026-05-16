package org.twightlight.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.player.Account;

public class PlayerLoginListener extends Listeners {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent evt) {
        Player player = evt.getPlayer();

        try {
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                Account account = Database.getInstance().unloadOfflineAccount(player.getUniqueId());
                if (account != null) {
                    account.save();
                    account.destroy();
                }
            }
            Database.getInstance().loadAccount(player.getUniqueId(), player.getName());
        } catch (Exception ex) {
            evt.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    "§c§lSKYWARS\n \n§cCould not load your account.");
            LOGGER.log(Level.SEVERE, "Could not loadAccount(\"" + player.getName() + "\"): ", ex);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoginMonitor(PlayerLoginEvent evt) {
        if (evt.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            Database.getInstance().unloadAccount(evt.getPlayer().getUniqueId());
        }
    }
}
