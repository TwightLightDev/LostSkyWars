package org.twightlight.skywars.modules.privategames.menus.submenus;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.privategames.User;
import org.twightlight.skywars.modules.privategames.menus.MainMenu;
import org.twightlight.skywars.modules.privategames.menus.utils.PGMenu;
import org.twightlight.skywars.utils.ItemBuilder;

public class HealthMultiply {
    public static void open(User p) {
        PGMenu menu = PGMenu.createMenu(36);
        Item back = new Item( (e) -> {
            MainMenu.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.ARROW).setName("&aBack").toItemStack();
        });

        Item a = new Item( (e) -> {
            p.getHealthMultiplySetting().setValue(1.0);
            open(p);
        }, (player) -> {
            if (p.getHealthMultiplySetting().getValue() == 1.0) {
                return new ItemBuilder(XMaterial.APPLE).setName("&a1.0x").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.APPLE).setName("&a1.0x").toItemStack();
        });

        Item b = new Item( (e) -> {
            p.getHealthMultiplySetting().setValue(2.0);
            open(p);
        }, (player) -> {
            if (p.getHealthMultiplySetting().getValue() == 2.0) {
                return new ItemBuilder(XMaterial.APPLE).setName("&a2.0x").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.APPLE).setName("&a2.0x").toItemStack();
        });

        Item c = new Item( (e) -> {
            p.getHealthMultiplySetting().setValue(3.0);
            open(p);
        }, (player) -> {
            if (p.getHealthMultiplySetting().getValue() == 3.0) {
                return new ItemBuilder(XMaterial.APPLE).setName("&a3.0x").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.APPLE).setName("&a3.0x").toItemStack();
        });


        menu.setItem(11, a);
        menu.setItem(13, b);
        menu.setItem(15, c);

        menu.setItem(27, back);
        menu.open(p.getPlayer());
    }
}
