package org.twightlight.skywars.modules.boosters.menus.utils;

import org.twightlight.skywars.modules.libs.menus.ModulesMenuHolder;

public class BMenuHolder extends ModulesMenuHolder {
    BMenu menu;
    public BMenuHolder(BMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public BMenu getMenu() {
        return menu;
    }

}
