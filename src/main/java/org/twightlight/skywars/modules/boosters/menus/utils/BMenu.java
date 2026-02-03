package org.twightlight.skywars.modules.boosters.menus.utils;

import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.api.menus.ModulesMenu;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.utils.StringUtils;


public class BMenu extends ModulesMenu {

    protected BMenu(int size, String name) {
        super(size);
        inv = Bukkit.createInventory(new ModulesMenuHolder(this), size, name);
    }

    public static BMenu createMenu(int size, String name) {
        return new BMenu(size, name);
    }

}
