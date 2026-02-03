package org.twightlight.skywars.modules.boosters.menus.submenus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterData;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.menus.MainMenu;
import org.twightlight.skywars.modules.boosters.menus.utils.BMenu;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.ServerUser;
import org.twightlight.skywars.modules.boosters.users.User;
import org.twightlight.skywars.utils.ItemBuilder;
import org.twightlight.skywars.utils.StringUtils;
import org.twightlight.skywars.utils.TimeUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Activating {
    private static final YamlWrapper config = Boosters.getMenuConfig();
    public static void open(User object, PlayerUser viewer, int page) {
        BMenu menu = BMenu.createMenu(config.getInt("activating.size"), StringUtils.formatColors(Boosters.getMenuConfig().getString("activating.name")));
        int i = 0;
        List<Integer> placeableSlots;

        if (object instanceof PlayerUser) {
            placeableSlots = Boosters.getMenuConfig().getList("activating.usableslots.personal").stream().map(Integer::parseInt).collect(Collectors.toList());
        } else {
            placeableSlots = Boosters.getMenuConfig().getList("activating.usableslots.network").stream().map(Integer::parseInt).collect(Collectors.toList());
        }

        int maxSlot = object instanceof PlayerUser ? viewer.getMaxCap("active") : 1;
        int availableSlot = object instanceof PlayerUser ? viewer.getCap("active") : 1;

        List<String> boosters = object.getActivatingStream().getAsList().stream().map((BoosterData::getBoosterID)).collect(Collectors.toList());
        for (Integer slot : placeableSlots) {
            int index = placeableSlots.size() * (page - 1) + i;
            Item item;

            if (boosters.size() > index) {
                String booster = boosters.get(index);

                Booster booster1 = BoosterManager.getBoosters().get(booster);
                UUID owner = object.getActivatingStream().getOwner(index);
                BoosterData data = object.getActivatingStream().getAsList().get(index);
                if (booster1 == null) {
                    continue;
                }
                long expiryTime = object.getActivatingStream().getActivatingTimeLine().get(index);
                item = new Item((e) -> {
                    if (e.getClick().name().contains("RIGHT")) {
                        ConfirmMenu.open(viewer, (e1) -> {
                            if (viewer.getUUID() == owner) {
                                if (object.getActivatingStream().remove(index)) {
                                    viewer.sendMessage(Boosters.getLanguage().getList("messages.boosters.deactivate").stream().map((line) -> {
                                        return line.replace("{booster}", Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1)));
                                    }).collect(Collectors.toList()));
                                }
                                open(object, viewer, page);
                            }
                        }, (e1) -> {
                            open(object, viewer, page);
                        });
                    }
                }, (player) -> new ItemBuilder(XMaterial.matchXMaterial(Boosters.getMenuConfig().getString("activating.items.boosters.material")).orElse(XMaterial.BEDROCK)).
                        setName(Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1))).
                        setLore(BoosterManager.replaceLore(Boosters.getLanguage().getYml().getStringList("boosters.display.item-lore"), Boosters.getLanguage().getYml().getStringList("boosters.status.in-activate").stream().map((line) -> {
                            return line.replace("{canremove}", (data.getOwner() == viewer.getUUID()) ? Boosters.getLanguage().getString("boosters.status.can-remove") : Boosters.getLanguage().getString("boosters.status.cannot-remove")).
                                    replace("{timeleft}", TimeUtils.getTimeUntil(expiryTime, " Day", " Hour", " Minute", " Second")).
                                    replace("{owner}", data.getOwnerName());
                        }).collect(Collectors.toList()))).
                        toItemStack());
            } else if (availableSlot > index) {
                item = new Item((e) -> {
                }, (player) -> ItemBuilder.parse(config.getYml(), "boosters.activating.items.slot-empty").toItemStack());
            } else if (index < maxSlot) {
                item = new Item((e) -> {
                }, (player) -> ItemBuilder.parse(config.getYml(), "boosters.activating.items.slot-locked").toItemStack());
            } else {
                item = new Item((e) -> {}, (player) -> new ItemStack(Material.AIR));
            }

            menu.setItem(slot, item);

            i ++;
        }

        Item close = new Item((e) -> {
            MainMenu.open(viewer);
        }, (player) -> ItemBuilder.parse(config.getYml(), "boosters.activating.items.back").toItemStack());
        menu.setItem(Boosters.getMenuConfig().getInt("activating.items.back.slot"), close);


        Item storage = new Item((e) -> {
            if (object instanceof PlayerUser) {
                Storage.open(viewer, Booster.BoosterType.PERSONAL, 1);
            } else if (object instanceof ServerUser) {
                Storage.open(viewer, Booster.BoosterType.NETWORK, 1);
            }
        }, (player) -> ItemBuilder.parse(config.getYml(), "boosters.activating.items.storage").toItemStack());
        menu.setItem(Boosters.getMenuConfig().getInt("activating.items.storage.slot"), storage);

        Item queue = new Item((e) -> {
            if (object instanceof PlayerUser) {
                Queue.open(object, viewer, 1);
            } else if (object instanceof ServerUser) {
                Queue.open(object, viewer, 1);
            }
        }, (player) -> ItemBuilder.parse(config.getYml(), "boosters.activating.items.queue").toItemStack());
        menu.setItem(Boosters.getMenuConfig().getInt("activating.items.queue.slot"), queue);


        if (page > 1) {
            Item prev = new Item((e) -> {
                open(object, viewer, page - 1);
            }, (player) -> {
                return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.activating.items.previous-page").toItemStack();
            });

            menu.setItem(Boosters.getMenuConfig().getInt("activating.items.previous-page.slot"), prev);

        }
        if (maxSlot > placeableSlots.size() * page) {
            Item next = new Item((e) -> {
                open(object, viewer, page + 1);
            }, (player) -> {
                return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.activating.items.next-page").toItemStack();
            });

            menu.setItem(Boosters.getMenuConfig().getInt("activating.items.next-page.slot"), next);
        }
        if (Boosters.getMenuConfig().getYml().contains("boosters.activating.custom-items")) {
            for (String key : Boosters.getMenuConfig().getYml().getConfigurationSection("boosters.activating.custom-items").getKeys(false)) {
                int slot = Boosters.getMenuConfig().getInt("activating.custom-items." + key + ".slot");

                Item ci = new Item((e) -> {
                    List<String> actions = Boosters.getMenuConfig().getList("activating.custom-items." + key + ".actions");
                    for (String action : actions) {
                        String type = action.split(":")[0];
                        if (type.equals("COMMAND")) {
                            viewer.getPlayer().performCommand(action.split(":")[1]);
                        }
                    }
                }, (player -> {
                    return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.activating.custom-items." + key).toItemStack();
                }));
                menu.setItem(slot, ci);

            }
        }


        menu.open(viewer.getPlayer());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (viewer.getPlayer() == null || !viewer.getPlayer().isOnline()) {
                    cancel();
                    return;
                }
                if (viewer.getPlayer().getOpenInventory().getTopInventory() == null) {
                    cancel();
                    return;
                }
                if (viewer.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof ModulesMenuHolder) {
                    ModulesMenuHolder holder = (ModulesMenuHolder) viewer.getPlayer().getOpenInventory().getTopInventory().getHolder();
                    if (!menu.equals(holder.getMenu())) {
                        cancel();
                        return;
                    }
                }

                menu.update(viewer.getPlayer(), menu.getSlotsList());
            }
        }.runTaskTimer(SkyWars.getInstance(), 20L, 20L);
    }
}
