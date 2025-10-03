package org.twightlight.skywars.modules.api.menus;

public class PaginatedMenu extends ModulesMenu {

    protected PaginatedMenu(int size) {
        super(size);
    }

    public void setContents(int page) {
        clear();
    }
}
