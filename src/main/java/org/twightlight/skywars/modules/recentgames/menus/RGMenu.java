package org.twightlight.skywars.modules.recentgames.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.twightlight.skywars.modules.libs.menus.ModulesMenu;
import org.twightlight.skywars.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RGMenu extends ModulesMenu {

    private RGMenu() {
        super(36);
        inv = Bukkit.createInventory(new RGMenuHolder(this), 36, StringUtils.formatColors("&7Recent Games"));
    }

    public static RGMenu createMenu() {
        return new RGMenu();
    }
}
