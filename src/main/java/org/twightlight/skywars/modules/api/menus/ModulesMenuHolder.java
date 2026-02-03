package org.twightlight.skywars.modules.api.menus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ModulesMenuHolder implements InventoryHolder {
    protected ModulesMenu menu;

    public ModulesMenuHolder(ModulesMenu menu) {
        this.menu = menu;
    }

    public ModulesMenu getMenu() {
        return menu;
    }

    @Override
    public Inventory getInventory() {
        return menu.inv;
    }

    public void remove() {
        menu.remove();
        menu = null;
    }
}
