package org.twightlight.skywars.hook.guilds.shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.hook.guilds.donation.Donator;
import org.twightlight.skywars.hook.guilds.menus.DonationMenu;
import org.twightlight.skywars.hook.guilds.menus.api.GMenu;
import org.twightlight.skywars.hook.guilds.menus.api.Item;
import org.twightlight.skywars.utils.ItemBuilder;

public class ShopMenu {
    public static void open(Donator donator) {
        Player player = Bukkit.getPlayer(donator.getUUID());
        GMenu menu = GMenu.createMenu(45);

        Item back = new Item((e) -> {
            DonationMenu.open(donator);
        }, (p) -> {
            return new ItemBuilder(XMaterial.ARROW).setName("&aGo back").toItemStack();
        });
        menu.addContent(40, back);

        menu.open(player);
    }
}
