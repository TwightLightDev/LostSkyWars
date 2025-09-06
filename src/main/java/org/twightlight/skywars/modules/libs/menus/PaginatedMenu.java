package org.twightlight.skywars.modules.libs.menus;

import org.bukkit.entity.Player;

public class PaginatedMenu extends ModulesMenu {

    protected PaginatedMenu(int size) {
        super(size);
    }


    public void open(Player p, int page) {
        inv.clear();

        for (int i : contents.keySet()) {
            if (size * (page - 1) <= i && (size * page) > i)
                inv.setItem(i, getItem(i).getItem(p.getPlayer()));
        }

        p.openInventory(inv);
    }

    @Override
    public void open(Player p) {
        open(p, 1);
    }
}
