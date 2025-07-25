package org.twightlight.skywars.modules.privategames.menus.submenus;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.privategames.PrivateGamesUser;
import org.twightlight.skywars.modules.privategames.menus.MainMenu;
import org.twightlight.skywars.modules.privategames.menus.utils.Item;
import org.twightlight.skywars.modules.privategames.menus.utils.PGMenu;
import org.twightlight.skywars.utils.ItemBuilder;

public class GameSpeed {
    public static void open(PrivateGamesUser p) {
        PGMenu menu = PGMenu.createMenu(36);

        Item back = new Item( (e) -> {
            MainMenu.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.ARROW).setName("&aBack").toItemStack();
        });

        Item a = new Item( (e) -> {
            p.getGameSpeedSetting().setValue(0.5);
            open(p);
        }, (player) -> {
            if (p.getGameSpeedSetting().getValue() == 0.5) {
                return new ItemBuilder(XMaterial.CLOCK).setName("&a0.5x").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.CLOCK).setName("&a0.5x").toItemStack();
        });

        Item b = new Item( (e) -> {
            p.getGameSpeedSetting().setValue(1.0);
            open(p);
        }, (player) -> {
            if (p.getGameSpeedSetting().getValue() == 1.0) {
                return new ItemBuilder(XMaterial.CLOCK).setName("&a1.0x").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.CLOCK).setName("&a1.0x").toItemStack();
        });

        Item c = new Item( (e) -> {
            p.getGameSpeedSetting().setValue(2.0);
            open(p);
        }, (player) -> {
            if (p.getGameSpeedSetting().getValue() == 2.0) {
                return new ItemBuilder(XMaterial.CLOCK).setName("&a2.0x").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.CLOCK).setName("&a2.0x").toItemStack();
        });

        Item d = new Item( (e) -> {
            p.getGameSpeedSetting().setValue(3.0);
            open(p);
        }, (player) -> {
            if (p.getGameSpeedSetting().getValue() == 3.0) {
                return new ItemBuilder(XMaterial.CLOCK).setName("&a3.0x").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.CLOCK).setName("&a3.0x").toItemStack();
        });

        menu.addContent(10, a);
        menu.addContent(12, b);
        menu.addContent(14, c);
        menu.addContent(16, d);

        menu.addContent(27, back);
        menu.open(p);
    }
}
