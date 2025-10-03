package org.twightlight.skywars.modules.boosters.menus;

import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.menus.utils.BMenu;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.utils.ItemBuilder;

public class MainMenu {
    public static void open(PlayerUser p) {
        BMenu menu = BMenu.createMenu(Boosters.getBoostersConfig().getInt("menus.size.mainmenu"));

        Item back = new Item( (e) -> {
            p.getPlayer().closeInventory();
        }, (player) -> {
            return new ItemBuilder(XMaterial.BARRIER).setName("&cClose").toItemStack();
        });

        menu.setItem(31, back);


        menu.open(p.getPlayer());
    }
}
