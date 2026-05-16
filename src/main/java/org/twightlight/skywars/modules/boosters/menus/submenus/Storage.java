package org.twightlight.skywars.modules.boosters.menus.submenus;

import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.menus.utils.BMenu;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.ServerUser;
import org.twightlight.skywars.utils.bukkit.ItemBuilder;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Storage {

    public static void open(PlayerUser p, Booster.BoosterType type, int page) {
        List<String> boosters;

        BMenu menu = BMenu.createMenu(Boosters.getMenuConfig().getInt("storage.size"), StringUtils.formatColors(Boosters.getMenuConfig().getString("storage.name")));
        if (type == Booster.BoosterType.PERSONAL) {
            boosters = p.getPersonalStorage();
        } else {
            boosters = p.getNetworkStorage();
        }
        int i = 0;

        List<Integer> placeableSlots = Boosters.getMenuConfig().getList("storage.usableslots").stream().map(Integer::parseInt).collect(Collectors.toList());

        for (Integer slot : placeableSlots) {
            int index = placeableSlots.size() * (page - 1) + i;

            if (boosters.size() <= index) break;

            String booster = boosters.get(index);

            Booster booster1 = BoosterManager.getBoosters().get(booster);
            if (booster1 == null) {
                continue;
            }
            menu.setItem(slot, new Item((e) -> {

                ConfirmMenu.open(p, (e1) -> {
                    if (p.addToQueue(p.getUUID(), booster)) {
                        p.removeBooster(booster);
                        open(p, type, page);
                    }
                }, (e1) -> {
                    open(p, type, page);
                });

            }, (player) -> new ItemBuilder(XMaterial.matchXMaterial(Boosters.getMenuConfig().getString("storage.items.boosters.material")).orElse(XMaterial.BEDROCK)).
                    setName(Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1))).
                    setLore(BoosterManager.replaceLore(Boosters.getLanguage().getYml().getStringList("boosters.display.item-lore"), Boosters.getLanguage().getYml().getStringList("boosters.status.in-storage" + (type == Booster.BoosterType.PERSONAL ? !p.getActivatingStream().isFull() ? "" : "-queue" : !ServerUser.getServerUser().getActivatingStream().isFull() ? "" : "-queue")))).
                    toItemStack()));

            i ++;
        }

        Item close = new Item((e) -> {
            Activating.open(type == Booster.BoosterType.PERSONAL ? p : ServerUser.getServerUser(), p, 1);
        }, (player) -> {
            return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.storage.items.back").toItemStack();
        });

        menu.setItem(Boosters.getMenuConfig().getInt("storage.items.back.slot"), close);


        if (page > 1) {
            Item prev = new Item((e) -> {
                Storage.open(p, type, page - 1);
            }, (player) -> {
                return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.storage.items.previous-page").toItemStack();
            });

            menu.setItem(Boosters.getMenuConfig().getInt("storage.items.previous-page.slot"), prev);

        }

        if (boosters.size() > placeableSlots.size() * page) {
            Item next = new Item((e) -> {
                Storage.open(p, type, page + 1);
            }, (player) -> {
                return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.storage.items.next-page").toItemStack();
            });

            menu.setItem(Boosters.getMenuConfig().getInt("storage.items.next-page.slot"), next);
        }

        if (Boosters.getMenuConfig().getYml().contains("boosters.storage.custom-items")) {
            for (String key : Boosters.getMenuConfig().getYml().getConfigurationSection("boosters.storage.custom-items").getKeys(false)) {
                int slot = Boosters.getMenuConfig().getInt("storage.custom-items." + key + ".slot");

                Item ci = new Item((e) -> {
                    List<String> actions = Boosters.getMenuConfig().getList("storage.custom-items." + key + ".actions");
                    for (String action : actions) {
                        String type1 = action.split(":")[0];
                        if (type1.equals("COMMAND")) {
                            p.getPlayer().performCommand(action.split(":")[1]);
                        }
                    }
                }, (player -> {
                    return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.storage.custom-items." + key).toItemStack();
                }));
                menu.setItem(slot, ci);

            }
        }

        menu.open(p.getPlayer());
    }
}
