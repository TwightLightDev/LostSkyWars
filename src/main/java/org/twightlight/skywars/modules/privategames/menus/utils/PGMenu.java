package org.twightlight.skywars.modules.privategames.menus.utils;

import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.api.menus.ModulesMenu;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;
import org.twightlight.skywars.utils.string.StringUtils;

public class PGMenu extends ModulesMenu {

    private PGMenu(int size) {
        super(size);
        inv = Bukkit.createInventory(new ModulesMenuHolder(this), size, StringUtils.formatColors("&7Private Games"));
    }

    public static PGMenu createMenu(int size) {
        return new PGMenu(size);
    }

}
