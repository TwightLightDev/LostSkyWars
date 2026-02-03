package org.twightlight.skywars.modules.api.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;

public class InventoryCloseEvent implements Listener {
    @EventHandler
    public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof ModulesMenuHolder) {
            ((ModulesMenuHolder) e.getInventory().getHolder()).remove();
        }
    }
}
