package org.twightlight.skywars.menu.shop.ingamecosmetics.trail;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.Filter;
import org.twightlight.skywars.menu.shop.ingamecosmetics.Order;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends PlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("confirmbuy");
    private static final MenuConfig config1 = MenuConfig.getByName("projectiletrail");

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory().equals(getInventory())) {
            evt.setCancelled(true);

            if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
                ItemStack item = evt.getCurrentItem();
                Account account = Database.getInstance().getAccount(player.getUniqueId());
                if (account == null) {
                    player.closeInventory();
                    return;
                }

                if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
                    ConfigAction action = map.get(item);
                    if (action != null && !action.getType().equals("NOTHING")) {
                        if (action.getType().equals("OPEN")) {
                            String menu = action.getValue();
                            if (menu.equalsIgnoreCase("shop")) {
                                try {
                                    Constructor<?> constructor = returns.getConstructor(Player.class, Order.class, Filter.class, String.class);
                                    constructor.newInstance(player, order, filter, searchQuery);
                                } catch (ReflectiveOperationException ex) {
                                    ex.printStackTrace();
                                }
                            } else if (menu.equalsIgnoreCase("buy")) {
                                if (account.getInt("coins") < cosmetic.getCoins()) {
                                    try {
                                        Constructor<?> constructor = returns.getConstructor(Player.class, Order.class, Filter.class, String.class);
                                        constructor.newInstance(player, order, filter, searchQuery);
                                    } catch (ReflectiveOperationException ex) {
                                        ex.printStackTrace();
                                    }
                                    return;
                                }

                                account.removeStat("coins", cosmetic.getCoins());
                                cosmetic.give(account);
                                player.sendMessage(StringUtils.formatColors(config.getAsString("buy").replace("{name}", cosmetic.getRawName())));
                                player.sendMessage(StringUtils.formatColors(config1.getAsString("select").replace("{name}", cosmetic.getRawName())));
                                account.setSelected(cosmetic);
                                player.closeInventory();
                            }
                        } else {
                            player.closeInventory();
                            action.send(player);
                        }
                    }
                }
            }
        }
    }

    private Cosmetic cosmetic;
    private Class<?> returns;
    private Map<ItemStack, ConfigAction> map = new HashMap<>();

    private Order order;
    private Filter filter;
    private String searchQuery;

    public ConfirmMenu(Player player, Cosmetic cosmetic, Class<?> returns, Order order, Filter filter, String searchQuery) {
        super(player, config.getTitle(), config.getRows());
        this.cosmetic = cosmetic;
        this.returns = returns;
        this.order = order;
        this.filter = filter;
        this.searchQuery = searchQuery;

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                stack = stack.replace("{name}", cosmetic.getRawName());
                stack = stack.replace("{price}", StringUtils.formatNumber(cosmetic.getCoins()));

                this.setItem(entry.getKey(), BukkitUtils.deserializeItemStack(stack));
                this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
            }
        }

        this.open();
        this.register();
    }

    public void cancel() {
        map.clear();
        map = null;
        cosmetic = null;
        returns = null;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        if (evt.getPlayer().equals(player)) {
            this.cancel();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent evt) {
        if (evt.getPlayer().equals(player) && evt.getInventory().equals(this.getInventory())) {
            this.cancel();
        }
    }
}
