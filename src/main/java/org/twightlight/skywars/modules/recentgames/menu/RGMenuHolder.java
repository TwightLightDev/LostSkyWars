package org.twightlight.skywars.modules.recentgames.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class RGMenuHolder implements InventoryHolder {
    private RGMenu menu;

    public RGMenuHolder(RGMenu menu) {
        this.menu = menu;
    }

    public RGMenu getMenu() {
        return menu;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
