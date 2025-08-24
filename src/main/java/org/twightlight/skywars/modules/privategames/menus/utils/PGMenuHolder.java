package org.twightlight.skywars.modules.privategames.menus.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.twightlight.skywars.modules.libs.menus.ModulesMenuHolder;

public class PGMenuHolder extends ModulesMenuHolder {
    PGMenu menu;

    public PGMenuHolder(PGMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public PGMenu getMenu() {
        return menu;
    }
}
