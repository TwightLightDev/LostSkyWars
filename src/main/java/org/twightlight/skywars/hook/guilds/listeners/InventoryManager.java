package org.twightlight.skywars.hook.guilds.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.twightlight.skywars.hook.guilds.menus.api.GMenu;
import org.twightlight.skywars.hook.guilds.menus.api.GMenuHolder;

public class InventoryManager implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof GMenuHolder) {
            e.setCancelled(true);
            GMenuHolder holder = (GMenuHolder) e.getInventory().getHolder();
            GMenu menu = holder.getMenu();

            if (menu.hasItem(e.getRawSlot()) && e.getCurrentItem().getType() != Material.AIR) {
                menu.getItem(e.getRawSlot()).getExecutable().execute(e);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof GMenuHolder) {
            GMenuHolder holder = (GMenuHolder) e.getInventory().getHolder();
            GMenu menu = holder.getMenu();
            menu.getDynamicTask().cancel();
        }
    }
}
