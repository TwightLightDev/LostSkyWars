package org.twightlight.skywars.hook.decenthologram.listeners;

import eu.decentsoftware.holograms.event.DecentHologramsReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.hook.DecentHologramsHook;
import org.twightlight.skywars.hook.decenthologram.User;

public class HologramReloadEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(DecentHologramsReloadEvent e) {
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            DecentHologramsHook.loadLeaderboards();
            User.getUsers().forEach(User::loadLeaderboards);
        }, 20L);
    }
}
