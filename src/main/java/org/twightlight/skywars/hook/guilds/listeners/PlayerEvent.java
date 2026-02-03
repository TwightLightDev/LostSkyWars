package org.twightlight.skywars.hook.guilds.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.twightlight.skywars.hook.guilds.GuildsHook;
import org.twightlight.skywars.hook.guilds.donation.Donator;

public class PlayerEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        GuildsHook.getExternalDB().createPlayerData(e.getPlayer());
        new Donator(e.getPlayer());
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Donator.getFromUUID(e.getPlayer().getUniqueId()).saveData();
        Donator.removeDonator(e.getPlayer().getUniqueId());
    }
}
