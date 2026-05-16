package org.twightlight.skywars.modules.boosters.menus.submenus;

import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterData;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.menus.utils.BMenu;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.User;
import org.twightlight.skywars.utils.bukkit.ItemBuilder;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Queue {

    private static final YamlWrapper config = Boosters.getMenuConfig();
    public static void open(User object, PlayerUser viewer, int page) {
        BMenu menu = BMenu.createMenu(config.getInt("queue.size"), StringUtils.formatColors(Boosters.getMenuConfig().getString("queue.name")));
        int i = 0;
        List<Integer> placeableSlots;

        if (object instanceof PlayerUser) {
            placeableSlots = Boosters.getMenuConfig().getList("queue.usableslots.personal").stream().map(Integer::parseInt).collect(Collectors.toList());
        } else {
            placeableSlots = Boosters.getMenuConfig().getList("queue.usableslots.network").stream().map(Integer::parseInt).collect(Collectors.toList());
        }

        List<String> boosters = object.getQueueStream().getAsList().stream().map((BoosterData::getBoosterID)).collect(Collectors.toList());
        List<BoosterData> dataList = object.getQueueStream().getAsList();
        for (Integer slot : placeableSlots) {
            int index = placeableSlots.size() * (page - 1) + i;
            Item item;

            if (boosters.size() > index) {
                String booster = boosters.get(index);

                Booster booster1 = BoosterManager.getBoosters().get(booster);
                UUID owner = object.getQueueStream().getOwner(index);
                BoosterData data = dataList.get(index);
                if (booster1 == null) {
                    continue;
                }
                item = new Item((e) -> {
                    if (e.getClick().name().contains("RIGHT")) {
                        ConfirmMenu.open(viewer, (e1) -> {
                            if (viewer.getUUID() == owner) {
                                if (object.getQueueStream().remove(index)) {
                                    viewer.sendMessage(Boosters.getLanguage().getList("messages.boosters.unqueue").stream().map((line) -> {
                                        return line.replace("{booster}", Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1)));
                                    }).collect(Collectors.toList()));
                                }
                                open(object, viewer, page);
                            }
                        }, (e1) -> {
                            open(object, viewer, page);
                        });
                    }
                }, (player) -> new ItemBuilder(XMaterial.matchXMaterial(Boosters.getMenuConfig().getString("queue.items.boosters.material")).orElse(XMaterial.BEDROCK)).
                        setName(Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1))).
                        setLore(BoosterManager.replaceLore(Boosters.getLanguage().getYml().getStringList("boosters.display.item-lore"), Boosters.getLanguage().getYml().getStringList("boosters.status.in-"+ (object instanceof PlayerUser ? "personal" : "network") +"-queue").stream().map((line) -> {
                            return line.replace("{canremove}", (data.getOwner() == viewer.getUUID()) ? Boosters.getLanguage().getString("boosters.status.can-remove") : Boosters.getLanguage().getString("boosters.status.cannot-remove")).replace("{pos}", index + 1 + "").replace("{owner}", data.getOwnerName());
                        }).collect(Collectors.toList()))).
                        toItemStack());
                menu.setItem(slot, item);

            } else {
                break;
            }


            i ++;
        }

        if (boosters.isEmpty()) {
            Item empty = new Item((e) -> {

            }, (player) -> ItemBuilder.parse(config.getYml(), "boosters.queue.items.empty-queue").toItemStack());
            menu.setItem(Boosters.getMenuConfig().getInt("queue.items.empty-queue.slot"), empty);

        }

        Item close = new Item((e) -> {
            Activating.open(object, viewer, 1);
        }, (player) -> ItemBuilder.parse(config.getYml(), "boosters.queue.items.back").toItemStack());
        menu.setItem(Boosters.getMenuConfig().getInt("queue.items.back.slot"), close);


        if (page > 1) {
            Item prev = new Item((e) -> {
                open(object, viewer, page - 1);
            }, (player) -> {
                return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.queue.items.previous-page").toItemStack();
            });

            menu.setItem(Boosters.getMenuConfig().getInt("queue.items.previous-page.slot"), prev);

        }
        if (boosters.size() > placeableSlots.size() * page) {
            Item next = new Item((e) -> {
                open(object, viewer, page + 1);
            }, (player) -> {
                return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.queue.items.next-page").toItemStack();
            });

            menu.setItem(Boosters.getMenuConfig().getInt("queue.items.next-page.slot"), next);
        }

        if (Boosters.getMenuConfig().getYml().contains("boosters.queue.custom-items")) {
            for (String key : Boosters.getMenuConfig().getYml().getConfigurationSection("boosters.queue.custom-items").getKeys(false)) {
                int slot = Boosters.getMenuConfig().getInt("queue.custom-items." + key + ".slot");

                Item ci = new Item((e) -> {
                    List<String> actions = Boosters.getMenuConfig().getList("queue.custom-items." + key + ".actions");
                    for (String action : actions) {
                        String type1 = action.split(":")[0];
                        if (type1.equals("COMMAND")) {
                            viewer.getPlayer().performCommand(action.split(":")[1]);
                        }
                    }
                }, (player -> {
                    return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.queue.custom-items." + key).toItemStack();
                }));
                menu.setItem(slot, ci);

            }
        }

        menu.open(viewer.getPlayer());

    }
}
