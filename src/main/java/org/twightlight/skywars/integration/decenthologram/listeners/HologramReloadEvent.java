package org.twightlight.skywars.integration.decenthologram.listeners;

import eu.decentsoftware.holograms.event.DecentHologramsReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.integration.decenthologram.DecentHologramsIntegration;
import org.twightlight.skywars.integration.decenthologram.User;

public class HologramReloadEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(DecentHologramsReloadEvent e) {
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            DecentHologramsIntegration.loadLeaderboards();
            User.getUsers().forEach(User::loadLeaderboards);
        }, 20L);
    }
}
