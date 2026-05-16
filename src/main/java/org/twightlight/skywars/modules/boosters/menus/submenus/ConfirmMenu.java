package org.twightlight.skywars.modules.boosters.menus.submenus;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.menus.utils.BMenu;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.utils.bukkit.ItemBuilder;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.List;
import java.util.function.Consumer;

public class ConfirmMenu {
    private static final YamlWrapper config = Boosters.getMenuConfig();

    public static void open(PlayerUser playerUser, Consumer<InventoryClickEvent> confirm, Consumer<InventoryClickEvent> cancel) {
        BMenu menu = BMenu.createMenu(config.getInt("confirm.size"), StringUtils.formatColors(Boosters.getMenuConfig().getString("confirm.name")));

        Item confirmItem = new Item(confirm::accept, (player) -> ItemBuilder.parse(config.getYml(), "boosters.confirm.items.confirm").toItemStack());

        menu.setItem(Boosters.getMenuConfig().getInt("confirm.items.confirm.slot"), confirmItem);

        Item cancelItem = new Item(cancel::accept, (player) -> ItemBuilder.parse(config.getYml(), "boosters.confirm.items.cancel").toItemStack());

        menu.setItem(Boosters.getMenuConfig().getInt("confirm.items.cancel.slot"), cancelItem);

        if (Boosters.getMenuConfig().getYml().contains("boosters.confirm.custom-items")) {
            for (String key : Boosters.getMenuConfig().getYml().getConfigurationSection("boosters.confirm.custom-items").getKeys(false)) {
                int slot = Boosters.getMenuConfig().getInt("confirm.custom-items." + key + ".slot");

                Item ci = new Item((e) -> {
                    List<String> actions = Boosters.getMenuConfig().getList("confirm.custom-items." + key + ".actions");
                    for (String action : actions) {
                        String type1 = action.split(":")[0];
                        if (type1.equals("COMMAND")) {
                            playerUser.getPlayer().performCommand(action.split(":")[1]);
                        }
                    }
                }, (player -> {
                    return ItemBuilder.parse(Boosters.getMenuConfig().getYml(), "boosters.confirm.custom-items." + key).toItemStack();
                }));
                menu.setItem(slot, ci);

            }
        }

        menu.open(playerUser.getPlayer());
    }

}
