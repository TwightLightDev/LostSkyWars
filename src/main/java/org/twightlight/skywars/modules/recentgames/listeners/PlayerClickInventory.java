package org.twightlight.skywars.modules.recentgames.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.twightlight.skywars.modules.recentgames.menus.RGMenu;
import org.twightlight.skywars.modules.recentgames.menus.RGMenuHolder;

public class PlayerClickInventory implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof RGMenuHolder) {
            e.setCancelled(true);
            RGMenuHolder holder = (RGMenuHolder) e.getInventory().getHolder();
            RGMenu menu = holder.getMenu();

            if (menu.hasItem(e.getRawSlot()) && e.getCurrentItem().getType() != Material.AIR) {
                menu.getItem(e.getRawSlot()).getExecutable().execute(e);
            }
        }
    }
}
