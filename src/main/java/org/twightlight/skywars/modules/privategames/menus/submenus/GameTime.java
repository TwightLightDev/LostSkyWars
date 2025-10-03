package org.twightlight.skywars.modules.privategames.menus.submenus;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.privategames.User;
import org.twightlight.skywars.modules.privategames.menus.MainMenu;
import org.twightlight.skywars.modules.privategames.menus.utils.PGMenu;
import org.twightlight.skywars.modules.privategames.settings.GameTimeSetting;
import org.twightlight.skywars.utils.ItemBuilder;

public class GameTime {
    public static void open(User p) {
        PGMenu menu = PGMenu.createMenu(36);
        Item back = new Item( (e) -> {
            MainMenu.open(p);
        }, (player) -> {
            return new ItemBuilder(XMaterial.ARROW).setName("&aBack").toItemStack();
        });

        Item a = new Item( (e) -> {
            p.getGameTimeSetting().setValue(GameTimeSetting.GameTime.DAY);
            open(p);
        }, (player) -> {
            if (p.getGameTimeSetting().getValue().equals(GameTimeSetting.GameTime.DAY.getName())) {
                return new ItemBuilder(XMaterial.CLOCK).setName("&aDay").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.CLOCK).setName("&aDay").toItemStack();
        });

        Item b = new Item( (e) -> {
            p.getGameTimeSetting().setValue(GameTimeSetting.GameTime.NOON);
            open(p);
        }, (player) -> {
            if (p.getGameTimeSetting().getValue().equals(GameTimeSetting.GameTime.NOON.getName())) {
                return new ItemBuilder(XMaterial.CLOCK).setName("&aNoon").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.CLOCK).setName("&aNoon").toItemStack();
        });

        Item c = new Item( (e) -> {
            p.getGameTimeSetting().setValue(GameTimeSetting.GameTime.AFTERNOON);
            open(p);
        }, (player) -> {
            if (p.getGameTimeSetting().getValue().equals(GameTimeSetting.GameTime.AFTERNOON.getName())) {
                return new ItemBuilder(XMaterial.CLOCK).setName("&aAfternoon").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.CLOCK).setName("&aAfternoon").toItemStack();
        });

        Item d = new Item( (e) -> {
            p.getGameTimeSetting().setValue(GameTimeSetting.GameTime.NIGHT);
            open(p);
        }, (player) -> {
            if (p.getGameTimeSetting().getValue().equals(GameTimeSetting.GameTime.NIGHT.getName())) {
                return new ItemBuilder(XMaterial.CLOCK).setName("&aNight").
                        addEnchant(Enchantment.PROTECTION_FALL, 1).
                        addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
            }
            return new ItemBuilder(XMaterial.CLOCK).setName("&aNight").toItemStack();
        });

        menu.setItem(10, a);
        menu.setItem(12, b);
        menu.setItem(14, c);
        menu.setItem(16, d);

        menu.setItem(27, back);
        menu.open(p.getPlayer());
    }
}
