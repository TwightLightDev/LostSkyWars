package org.twightlight.skywars.fun.customitems.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.twightlight.skywars.fun.customitems.CustomItemsManager;
import org.twightlight.skywars.fun.customitems.FunItem;

public class DropAction implements Listener {
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        FunItem funItem = CustomItemsManager.getItem(e.getPlayer().getItemInHand());
        if (funItem != null) {
            funItem.dropAction(e.getPlayer(), e, e.getPlayer().getItemInHand());
        }
    }
}
