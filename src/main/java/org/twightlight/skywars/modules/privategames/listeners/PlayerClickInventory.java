package org.twightlight.skywars.modules.privategames.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.twightlight.skywars.modules.api.menus.ModulesMenu;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;

public class PlayerClickInventory implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof ModulesMenuHolder) {
            e.setCancelled(true);
            ModulesMenuHolder holder = (ModulesMenuHolder) e.getInventory().getHolder();
            ModulesMenu menu = holder.getMenu();

            if (menu.hasItem(e.getRawSlot()) && e.getCurrentItem().getType() != Material.AIR) {
                menu.getItem(e.getRawSlot()).getExecutable().execute(e);
            }
        }
    }
}
