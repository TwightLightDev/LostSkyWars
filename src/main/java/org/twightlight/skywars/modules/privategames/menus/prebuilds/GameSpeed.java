package org.twightlight.skywars.modules.privategames.menus.prebuilds;

import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.privategames.menus.PGMenu;

public class GameSpeed {
    public static void open(Player p) {
        PGMenu menu = PGMenu.createMenu(36);
        menu.open(p);
    }
}
