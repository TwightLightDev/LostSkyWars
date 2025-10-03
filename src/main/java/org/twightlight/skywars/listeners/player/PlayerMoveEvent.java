package org.twightlight.skywars.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerMoveEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityMove(org.bukkit.event.player.PlayerMoveEvent e) {
        if (e.getPlayer().hasMetadata("frozen")) {
            e.setCancelled(true);
        }
    }
}
