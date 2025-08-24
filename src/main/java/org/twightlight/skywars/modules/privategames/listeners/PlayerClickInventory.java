package org.twightlight.skywars.modules.privategames.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.twightlight.skywars.modules.libs.menus.ModulesMenu;
import org.twightlight.skywars.modules.libs.menus.ModulesMenuHolder;
import org.twightlight.skywars.modules.privategames.menus.utils.PGMenu;
import org.twightlight.skywars.modules.privategames.menus.utils.PGMenuHolder;

public class PlayerClickInventory implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof PGMenuHolder) {
            e.setCancelled(true);
            PGMenuHolder holder = (PGMenuHolder) e.getInventory().getHolder();
            PGMenu menu = holder.getMenu();

            if (menu.hasItem(e.getRawSlot()) && e.getCurrentItem().getType() != Material.AIR) {
                menu.getItem(e.getRawSlot()).getExecutable().execute(e);
            }
        }
    }
}
