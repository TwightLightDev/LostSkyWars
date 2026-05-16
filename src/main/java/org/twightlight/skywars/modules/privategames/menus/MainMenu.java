package org.twightlight.skywars.modules.privategames.menus;

import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.privategames.User;
import org.twightlight.skywars.modules.privategames.menus.submenus.GameSpeed;
import org.twightlight.skywars.modules.privategames.menus.submenus.GameTime;
import org.twightlight.skywars.modules.privategames.menus.submenus.HealthMultiply;
import org.twightlight.skywars.modules.privategames.menus.utils.PGMenu;
import org.twightlight.skywars.utils.bukkit.ItemBuilder;

public class MainMenu {
    public static void open(User p) {
        PGMenu menu = PGMenu.createMenu(36);

        Item back = new Item( (e) -> {
            p.getPlayer().closeInventory();
        }, (player) -> {
            return new ItemBuilder(XMaterial.BARRIER).setName("&aBack").toItemStack();
        });
        menu.setItem(31, back);
        Item gameSpeed = new Item( (e) -> {
            GameSpeed.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.CLOCK).setName("&aSet Game Speed").setLore("&bCurrent Speed: " + p.getGameSpeedSetting().getValue(), "", "&eClick to change!").toItemStack();
        });
        menu.setItem(10, gameSpeed);
        Item gameTime = new Item( (e) -> {
            GameTime.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.CLOCK).setName("&aSet Game Time").setLore("&bCurrent Time: " + p.getGameTimeSetting().getValue(), "", "&eClick to change!").toItemStack();
        });
        menu.setItem(12, gameTime);
        Item instantKill = new Item( (e) -> {
            if (p.getInstantKillSetting().getValue() == 1) {
                p.getInstantKillSetting().setValue(0);
            } else {
                p.getInstantKillSetting().setValue(1);
            }
            MainMenu.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.DIAMOND_SWORD).setName("&aSet Instant Kill").setLore(p.getInstantKillSetting().getValue() == 1 ? "&aEnable" : "&cDisable", "", "&eClick to toggle!").toItemStack();
        });
        menu.setItem(14, instantKill);
        Item healthMultiply = new Item( (e) -> {
            HealthMultiply.open(p);
            HealthMultiply.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.APPLE).setName("&aSet Health Multiply").setLore("&bCurrent Multiply: " + p.getHealthMultiplySetting().getValue(), "", "&eClick to change!").toItemStack();
        });
        menu.setItem(16, healthMultiply);
        menu.open(p.getPlayer());
    }
}
