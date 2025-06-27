package org.twightlight.skywars.modules.privategames.menus.prebuilds;

import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.privategames.menus.PGMenu;

public class MainMenu {
    public static void open(Player p) {
        PGMenu menu = PGMenu.createMenu(54);
        menu.open(p);
    }
}
