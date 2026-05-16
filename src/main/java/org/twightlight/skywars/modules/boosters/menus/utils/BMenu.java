package org.twightlight.skywars.modules.boosters.menus.utils;

import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.api.menus.ModulesMenu;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;


public class BMenu extends ModulesMenu {

    protected BMenu(int size, String name) {
        super(size);
        inv = Bukkit.createInventory(new ModulesMenuHolder(this), size, name);
    }

    public static BMenu createMenu(int size, String name) {
        return new BMenu(size, name);
    }

}
