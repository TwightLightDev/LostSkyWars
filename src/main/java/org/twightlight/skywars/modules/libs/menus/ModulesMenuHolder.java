package org.twightlight.skywars.modules.libs.menus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class ModulesMenuHolder implements InventoryHolder {
    protected ModulesMenu menu;

    public ModulesMenuHolder(ModulesMenu menu) {
        this.menu = menu;
    }

    public ModulesMenu getMenu() {
        return menu;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
