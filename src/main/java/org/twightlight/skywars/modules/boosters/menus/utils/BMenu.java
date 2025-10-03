package org.twightlight.skywars.modules.boosters.menus.utils;

import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.api.menus.ModulesMenu;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;
import org.twightlight.skywars.utils.StringUtils;


public class BMenu extends ModulesMenu {

    private BMenu(int size) {
        super(size);
        inv = Bukkit.createInventory(new ModulesMenuHolder(this), size, StringUtils.formatColors("&7Boosters"));
    }

    public static BMenu createMenu(int size) {
        return new BMenu(size);
    }

}
