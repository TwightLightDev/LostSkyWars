package org.twightlight.skywars.modules.quests.menus.utils;

import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.api.menus.ModulesMenu;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;


public class QMenu extends ModulesMenu {

    protected QMenu(int size, String name) {
        super(size);
        inv = Bukkit.createInventory(new ModulesMenuHolder(this), size, name);
    }

    public static QMenu createMenu(int size, String name) {
        return new QMenu(size, name);
    }

}
