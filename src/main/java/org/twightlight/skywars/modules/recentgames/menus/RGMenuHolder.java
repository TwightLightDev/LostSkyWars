package org.twightlight.skywars.modules.recentgames.menus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.twightlight.skywars.modules.libs.menus.ModulesMenuHolder;

public class RGMenuHolder extends ModulesMenuHolder {
    private RGMenu menu;

    public RGMenuHolder(RGMenu menu) {
        super(menu);
        this.menu = menu;
    }

    public RGMenu getMenu() {
        return menu;
    }
}
