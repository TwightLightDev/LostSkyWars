package org.twightlight.skywars.modules.recentgames.menus;

import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.api.menus.ModulesMenu;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;
import org.twightlight.skywars.modules.recentgames.RecentGames;
import org.twightlight.skywars.utils.string.StringUtils;

public class RGMenu extends ModulesMenu {

    private RGMenu() {
        super(36);
        inv = Bukkit.createInventory(new ModulesMenuHolder(this),
                RecentGames.getMenuConfig().getInt("menu.size"),
                StringUtils.formatColors(RecentGames.getMenuConfig().getString("menu.name")));
    }

    public static RGMenu createMenu() {
        return new RGMenu();
    }
}
