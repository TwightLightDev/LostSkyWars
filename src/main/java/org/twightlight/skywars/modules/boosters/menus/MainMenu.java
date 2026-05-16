package org.twightlight.skywars.modules.boosters.menus;

import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterData;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.menus.submenus.Activating;
import org.twightlight.skywars.modules.boosters.menus.submenus.ConfirmMenu;
import org.twightlight.skywars.modules.boosters.menus.utils.BMenu;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.ServerUser;
import org.twightlight.skywars.utils.bukkit.ItemBuilder;
import org.twightlight.skywars.utils.string.StringUtils;
import org.twightlight.skywars.utils.player.TimeUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainMenu {
    public static void open(PlayerUser viewer) {
        BMenu menu = BMenu.createMenu(Boosters.getMenuConfig().getInt("mainmenu.size"), StringUtils.formatColors(Boosters.getMenuConfig().getString("mainmenu.name")));

        Item close = new Item((e) -> {
            e.getWhoClicked().closeInventory();
        }, (p) -> {
            return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.mainmenu.items.close").toItemStack();
        });

        menu.setItem(Boosters.getMenuConfig().getInt("mainmenu.items.close.slot"), close);

        Item personalBooster = new Item((e) -> {

            if (e.getClick().name().contains("RIGHT")) {
                ConfirmMenu.open(viewer, (e1) -> {
                    UUID owner = viewer.getActivatingStream().getOwner(0);
                    Booster booster1 = BoosterManager.getBoosters().get(viewer.getActivatingStream().getAsList().get(0).getBoosterID());
                    if (viewer.getUUID() == owner) {

                        if (viewer.getActivatingStream().remove(0)) {
                            viewer.sendMessage(Boosters.getLanguage().getList("messages.boosters.remove").stream().map((line) -> {
                                return line.replace("{booster}", Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1)));
                            }).collect(Collectors.toList()));
                        }
                        open(viewer);
                    }
                }, (e1) -> {
                    open(viewer);
                });
                return;
            }
            Activating.open(viewer, viewer, 1);
        }, (p) -> {
            if (viewer.hasBooster()) {
                BoosterData data = viewer.getActivatingStream().getAsList().get(0);
                String id = data.getBoosterID();
                Booster booster = BoosterManager.getBoosters().get(id);
                return new ItemBuilder(XMaterial.POTION).
                        setName(Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster)).replace("{color}", BoosterManager.getColor(booster)).replace("{amplifier}", BoosterManager.getAmplifierString(booster)).replace("{currency}", BoosterManager.getCurrencyString(booster))).
                        setLore(BoosterManager.replaceLore(Boosters.getLanguage().getYml().getStringList("boosters.display.item-lore"), Boosters.getLanguage().getYml().getStringList("boosters.status.in-activate").stream().map((line) -> {
                            return line.replace("{canremove}", (data.getOwner() == viewer.getUUID()) ? Boosters.getLanguage().getString("boosters.status.can-remove") : Boosters.getLanguage().getString("boosters.status.cannot-remove"));
                        }).collect(Collectors.toList())).stream().map(line -> {
                            return line.replace("{timeleft}", TimeUtils.getTimeUntil(viewer.getActivatingStream().getActivatingTimeLine().get(0), " Day", " Hour", " Minute", " Second")).replace("{owner}", data.getOwnerName());
                        }).collect(Collectors.toList())).
                        toItemStack();
            } else {
                return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.mainmenu.items.personal-booster.no-personal-booster").toItemStack();
            }
        });

        menu.setItem(Boosters.getMenuConfig().getInt("mainmenu.items.personal-booster.slot"), personalBooster);

        Item networkBooster = new Item((e) -> {
            if (e.getClick().name().contains("RIGHT")) {
                ConfirmMenu.open(viewer, (e1) -> {
                    UUID owner = ServerUser.getServerUser().getActivatingStream().getOwner(0);

                    if (viewer.getUUID() == owner) {
                        Booster booster1 = BoosterManager.getBoosters().get(ServerUser.getServerUser().getActivatingStream().getAsList().get(0).getBoosterID());

                        if (ServerUser.getServerUser().getActivatingStream().remove(0)) {
                            viewer.sendMessage(Boosters.getLanguage().getList("messages.boosters.remove").stream().map((line) -> {
                                return line.replace("{booster}", Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getDurationString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1)));
                            }).collect(Collectors.toList()));
                        }                        open(viewer);
                    }

                }, (e1) -> {
                    open(viewer);
                });
                return;
            }

            Activating.open(ServerUser.getServerUser(), viewer, 1);
        }, (p) -> {
            if (ServerUser.getServerUser().hasBooster()) {
                BoosterData data = ServerUser.getServerUser().getActivatingStream().getAsList().get(0);
                String id = data.getBoosterID();
                Booster booster = BoosterManager.getBoosters().get(id);
                return new ItemBuilder(XMaterial.POTION).
                        setName(Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getAmplifierString(booster)).replace("{color}", BoosterManager.getColor(booster)).replace("{amplifier}", BoosterManager.getAmplifierString(booster)).replace("{currency}", BoosterManager.getCurrencyString(booster))).
                        setLore(BoosterManager.replaceLore(Boosters.getLanguage().getYml().getStringList("boosters.display.item-lore"), Boosters.getLanguage().getYml().getStringList("boosters.status.in-activate").stream().map((line) -> {
                            return line.replace("{canremove}", (data.getOwner() == viewer.getUUID()) ? Boosters.getLanguage().getString("boosters.status.can-remove") : Boosters.getLanguage().getString("boosters.status.cannot-remove"));
                        }).collect(Collectors.toList())).stream().map(line -> {
                            return line.replace("{timeleft}", TimeUtils.getTimeUntil(ServerUser.getServerUser().getActivatingStream().getActivatingTimeLine().get(0), " Day", " Hour", " Minute", " Second")).replace("{owner}", data.getOwnerName());
                        }).collect(Collectors.toList())).
                        toItemStack();
            } else {
                return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.mainmenu.items.network-booster.no-network-booster").toItemStack();
            }
        });

        menu.setItem(Boosters.getMenuConfig().getInt("mainmenu.items.network-booster.slot"), networkBooster);

        if (Boosters.getMenuConfig().getYml().contains("boosters.mainmenu.custom-items")) {
            for (String key : Boosters.getMenuConfig().getYml().getConfigurationSection("boosters.mainmenu.custom-items").getKeys(false)) {
                int slot = Boosters.getMenuConfig().getInt("mainmenu.custom-items." + key + ".slot");

                Item ci = new Item((e) -> {
                    List<String> actions = Boosters.getMenuConfig().getList("mainmenu.custom-items." + key + ".actions");
                    for (String action : actions) {
                        String type1 = action.split(":")[0];
                        if (type1.equals("COMMAND")) {
                            viewer.getPlayer().performCommand(action.split(":")[1]);
                        }
                    }
                }, (player -> {
                    return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.mainmenu.custom-items." + key).toItemStack();
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
