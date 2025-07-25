package org.twightlight.skywars.modules.privategames.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.privategames.PrivateGamesUser;
import org.twightlight.skywars.modules.privategames.menus.submenus.GameSpeed;
import org.twightlight.skywars.modules.privategames.menus.submenus.GameTime;
import org.twightlight.skywars.modules.privategames.menus.utils.Item;
import org.twightlight.skywars.modules.privategames.menus.utils.PGMenu;
import org.twightlight.skywars.utils.ItemBuilder;

public class MainMenu {
    public static void open(PrivateGamesUser p) {
        PGMenu menu = PGMenu.createMenu(54);

        Item gamespeed = new Item( (e) -> {
            GameSpeed.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.CLOCK).setName("&aSet Game Speed").setLore("&bCurrent Speed: " + p.getGameSpeedSetting().getValue(), "", "&eClick to change!").toItemStack();
        });
        menu.addContent(10, gamespeed);
        Item gametime = new Item( (e) -> {
            GameTime.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.CLOCK).setName("&aSet Game Time").setLore("&bCurrent Time: " + p.getGameTimeSetting().getValue(), "", "&eClick to change!").toItemStack();
        });
        menu.addContent(12, gametime);
        menu.open(p);
    }
}
