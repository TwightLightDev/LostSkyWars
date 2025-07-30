package org.twightlight.skywars.hook.guilds.menus;

import me.leoo.guilds.bukkit.menu.MainMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.hook.GuildsHook;
import org.twightlight.skywars.hook.guilds.donation.Donator;
import org.twightlight.skywars.hook.guilds.menus.api.GMenu;
import org.twightlight.skywars.hook.guilds.menus.api.Item;
import org.twightlight.skywars.hook.guilds.shop.ShopMenu;
import org.twightlight.skywars.utils.ItemBuilder;
import org.twightlight.skywars.utils.TimeUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DonationMenu {

    public static void open(Donator donator) {
        Player player = Bukkit.getPlayer(donator.getUUID());
        GMenu menu = GMenu.createMenu(45);

        Item back = new Item((e) -> {
            new MainMenu(player).open();
        }, (p) -> {
            return new ItemBuilder(XMaterial.ARROW).setName("&aGo back").setLore(Arrays.asList("&7To Guild")).toItemStack();
        });
        menu.addContent(40, back);

        Item shop = new Item((e) -> {
            ShopMenu.open(donator);
        }, (p) -> {
            return new ItemBuilder(XMaterial.EMERALD).setName("&aGuild Shop").setLore(Arrays.asList("&eClick to browse!")).toItemStack();
        });
        menu.addContent(4, shop);

        Item donate = new Item((e) -> {
            donator.donate(100);
            menu.open(player);
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(SkyWars.getInstance(), () -> {
                ItemStack itemStack = menu.getItemStack(24);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(Arrays.asList("&7Keep generously donating to gain &bEXP.",
                        "&7The higher level, the better perk you",
                        "&7can have.",
                        "",
                        "&7Progress: " + getProgressBar(donator) + "&b " + donator.getLevel().getCurrentXP() * 100 / donator.getLevel().getRequiredXP() + "%" ,
                        "",
                        "&7Experience until next level: &b" + (donator.getLevel().getRequiredXP() - donator.getLevel().getCurrentXP()),
                        "",
                        "&b&lCurrent Perk:",
                        "&eMax daily donation: " + donator.getDonationLimit(),
                        "&eDonation Ratio: " + donator.getRatio(),
                        "",
                        "&a&lToday Donation:",
                        "&eDonated: " + donator.getDonationToday() + " &6Personal Coins",
                        "&eNext refresh: " + TimeUtils.getTimeUntil(donator.getNextRefresh())).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));
                itemStack.setItemMeta(meta);
            }, 0L, 20L);
            menu.setDynamicTask(task);
        }, (p) -> {
            return new ItemBuilder(XMaterial.GOLD_INGOT).setName("&6Donate 1x time").
                    setLore(Arrays.asList("&7Donate &6100 Personal Coins &7to your",
                            "&7guild. You can donate more using",
                            "&a/guilddonation donate <amount>&7.")).toItemStack();
        });
        menu.addContent(20, donate);

        Item level = new Item((e) -> {
            return;
        }, (p) -> {
            XMaterial material;
            String levelStr;
            if (donator.getLevel().getLevel() == Integer.MAX_VALUE) {
                levelStr = "max";
            } else {
                levelStr = String.valueOf(donator.getLevel().getLevel());
            }
            material = XMaterial.valueOf(GuildsHook.getLevelConfig().getString("level."+ levelStr +".material", "DIAMOND"));

            return new ItemBuilder(material).setName("&aDonator " + (donator.getLevel().getLevel() == Integer.MAX_VALUE ? "MAX" : "level " + donator.getLevel().getLevel())).
                    setLore(Arrays.asList("&7Keep generously donating to gain &bEXP.",
                            "&7The higher level, the better perk you",
                            "&7can have.",
                            "",
                            "&7Progress: " + getProgressBar(donator) + "&b " + donator.getLevel().getCurrentXP() * 100 / donator.getLevel().getRequiredXP() + "%" ,
                            "",
                            "&7Experience until next level: &b" + (donator.getLevel().getRequiredXP() - donator.getLevel().getCurrentXP()),
                            "",
                            "&b&lCurrent Perk:",
                            "&eMax daily donation: " + donator.getDonationLimit(),
                            "&eDonation Ratio: " + donator.getRatio(),
                            "",
                            "&a&lToday Donation:",
                            "&eDonated: " + donator.getDonationToday() + " &6Personal Coins",
                            "&eNext refresh: " + TimeUtils.getTimeUntil(donator.getNextRefresh()))).toItemStack();
        });
        menu.addContent(24, level);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(SkyWars.getInstance(), () -> {
            ItemStack itemStack = menu.getItemStack(24);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setLore(Arrays.asList("&7Keep generously donating to gain &bEXP.",
                    "&7The higher level, the better perk you",
                    "&7can have.",
                    "",
                    "&7Progress: " + getProgressBar(donator) + "&b " + donator.getLevel().getCurrentXP() * 100 / donator.getLevel().getRequiredXP() + "%" ,
                    "",
                    "&7Experience until next level: &b" + (donator.getLevel().getRequiredXP() - donator.getLevel().getCurrentXP()),
                    "",
                    "&b&lCurrent Perk:",
                    "&eMax daily donation: " + donator.getDonationLimit(),
                    "&eDonation Ratio: " + donator.getRatio(),
                    "",
                    "&a&lToday Donation:",
                    "&eDonated: " + donator.getDonationToday() + " &6Personal Coins",
                    "&eNext refresh: " + TimeUtils.getTimeUntil(donator.getNextRefresh())).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));
            itemStack.setItemMeta(meta);
        }, 0L, 20L);
        menu.setDynamicTask(task);

        menu.open(player);

    }

    private static String getProgressBar(Donator donator) {
        double current = donator.getLevel().getCurrentXP();
        double required = donator.getLevel().getRequiredXP();

        int barLength = 20;
        double ratio = required == 0 ? 0 : current / required;
        int filledBars = (int) Math.round(ratio * barLength);

        StringBuilder bar = new StringBuilder("&b");
        for (int i = 0; i < barLength; i++) {
            if (i < filledBars) {
                bar.append("■");
            } else {
                bar.append("&8■");
            }
        }
        return bar.toString();
    }
}
