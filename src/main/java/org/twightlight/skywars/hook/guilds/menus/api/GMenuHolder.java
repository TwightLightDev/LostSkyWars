package org.twightlight.skywars.hook.guilds.menus.api;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GMenuHolder implements InventoryHolder {
    private GMenu menu;

    public GMenuHolder(GMenu menu) {
        this.menu = menu;
    }

    public GMenu getMenu() {
        return menu;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
