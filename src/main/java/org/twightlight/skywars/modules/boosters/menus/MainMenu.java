package org.twightlight.skywars.modules.boosters.menus;

import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.boosters.User;
import org.twightlight.skywars.modules.boosters.menus.utils.BMenu;
import org.twightlight.skywars.modules.libs.menus.Item;
import org.twightlight.skywars.utils.ItemBuilder;

public class MainMenu {
    public static void open(User p) {
        BMenu menu = BMenu.createMenu(36);

        Item back = new Item( (e) -> {
            p.getPlayer().closeInventory();
        }, (player) -> {
            return new ItemBuilder(XMaterial.BARRIER).setName("&aBack").toItemStack();
        });
        menu.addContent(31, back);
        menu.open(p.getPlayer());
    }
}
