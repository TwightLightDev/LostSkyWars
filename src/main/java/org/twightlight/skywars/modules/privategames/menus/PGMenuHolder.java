package org.twightlight.skywars.modules.privategames.menus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.twightlight.skywars.modules.recentgames.menu.RGMenu;

public class PGMenuHolder implements InventoryHolder {
    private PGMenu menu;

    public PGMenuHolder(PGMenu menu) {
        this.menu = menu;
    }

    public PGMenu getMenu() {
        return menu;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
