package org.twightlight.skywars.modules.boosters.menus.submenus;

import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.api.menus.ModulesMenu;

public class Queue extends ModulesMenu {
    private Queue(PlayerUser p) {
        super(Boosters.getBoostersConfig().getInt("menus.size.queue"));
    }
}
