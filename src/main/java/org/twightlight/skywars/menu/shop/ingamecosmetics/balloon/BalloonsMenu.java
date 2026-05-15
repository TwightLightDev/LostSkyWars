package org.twightlight.skywars.menu.shop.ingamecosmetics.balloon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsBalloon;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PagedPlayerMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.CosmeticsMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.Filter;
import org.twightlight.skywars.menu.shop.ingamecosmetics.Order;
import org.twightlight.skywars.nms.Sound;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.setup.ChatSession;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BalloonsMenu extends PagedPlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("balloon");

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory().equals(getCurrentInventory())) {
            evt.setCancelled(true);

            if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
                ItemStack item = evt.getCurrentItem();
                Account account = Database.getInstance().getAccount(player.getUniqueId());
                if (account == null) {
                    player.closeInventory();
                    return;
                }

                if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
                    SkyWarsBalloon balloon = balloons.get(item);
                    if (evt.getSlot() == this.previousPage) {
                        this.openPrevious();
                    } else if (evt.getSlot() == this.nextPage) {
                        this.openNext();
                    } else if (item.isSimilar(filterItem)) {
                        if (evt.getClick().name().contains("RIGHT")) {
                            Order next = Order.next(order);
                            new BalloonsMenu(player, next, filter, searchQuery);
                        } else if (evt.getClick().name().contains("LEFT")) {
                            Filter next = Filter.next(filter);
                            new BalloonsMenu(player, order, next, searchQuery);
                        } else if (evt.getClick().name().contains("MIDDLE")) {
                            player.closeInventory();
                            ChatSession sessions = new ChatSession(player);
                            sessions.prompt(Arrays.asList(new String[] {"&aType the value you want: ", "&aType 'cancel' to cancel!"}), (input) -> {
                                if (input.equals("cancel")) {
                                    sessions.end();
                                    Bukkit.getScheduler().runTask(SkyWars.getInstance(),
                                            () -> {
                                                new BalloonsMenu(player, order, filter, searchQuery);
                                            });
                                    return;
                                }
                                sessions.end();
                                Bukkit.getScheduler().runTask(SkyWars.getInstance(),
                                        () -> {
                                            new BalloonsMenu(player, order, filter, input);
                                        });
                            });

                        }
                    } else if (balloon != null) {
                        if (evt.getClick().name().contains("RIGHT")) {
                            balloon.playPreview(player, 100L, BalloonsMenu.class, order, filter, searchQuery);
                            return;
                        }

                        if (!balloon.has(account)) {
                            Sound.ENDERMAN_TELEPORT.play(player, 1.0F, 1.0F);
                            if (!balloon.canBeSold()) {
                                player.sendMessage(StringUtils.formatColors(config.getAsString("unavailable").replace("{name}", balloon.getRawName())));
                                return;
                            }

                            if (account.getCoins() < balloon.getCoins()) {
                                player.sendMessage(StringUtils.formatColors(config.getAsString("enoughcoins").replace("{name}", balloon.getRawName())));
                                return;
                            }

                            new ConfirmMenu(player, balloon, BalloonsMenu.class, order, filter, searchQuery);
                            return;
                        }

                        Sound.NOTE_PLING.play(player, 1.0F, 1.0F);
                        if (account.hasSelected(balloon)) {
                            player.sendMessage(StringUtils.formatColors(config.getAsString("deselect").replace("{name}", balloon.getRawName())));
                            account.setSelected(balloon.getServer(), balloon.getType(), 1, 0);
                            new BalloonsMenu(player, order, filter, searchQuery);
                            return;
                        }

                        player.sendMessage(StringUtils.formatColors(config.getAsString("select").replace("{name}", balloon.getRawName())));
                        account.setSelected(balloon);
                        new BalloonsMenu(player, order, filter, searchQuery);
                    } else {
                        ConfigAction action = actions.get(item);
                        if (action != null && !action.getType().equals("NOTHING")) {
                            if (action.getType().equals("OPEN")) {
                                String menu = action.getValue();
                                if (menu.equalsIgnoreCase("shop")) {
                                    new CosmeticsMenu(player);
                                } else if (menu.equalsIgnoreCase("closeinv")) {
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
    }

    private Map<ItemStack, SkyWarsBalloon> balloons;
    private Map<ItemStack, ConfigAction> actions;
    private ItemStack filterItem;
    private Order order;
    private Filter filter;
    private String searchQuery;

    public BalloonsMenu(Player player) {
        this(player, Order.NONE, Filter.ALL, "");
    }

    public BalloonsMenu(Player player, Order order, Filter filter, String searchQuery) {
        super(player, config.getTitle(), config.getRows());
        this.balloons = new HashMap<>();
        this.actions = new HashMap<>();
        this.order = order;
        this.filter = filter;
        this.searchQuery = searchQuery;
        this.previousPage = config.getAsInt("previous-slot");
        this.nextPage = config.getAsInt("next-slot");
        this.previousStack = config.getAsString("previous-page");
        this.nextStack = config.getAsString("next-page");
        this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        List<ItemStack> items = new ArrayList<>();
        List<Cosmetic> cosmetics = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_BALLOON);
        order.accept(cosmetics);
        cosmetics = filter.accept(cosmetics, player);
        cosmetics = cosmetics.stream().filter(cos -> cos.getRawName().toLowerCase().startsWith(searchQuery)).collect(Collectors.toList());
        for (Cosmetic c : cosmetics) {
            SkyWarsBalloon balloon = (SkyWarsBalloon) c;
            String rarity = balloon.getRarity().getName();
            boolean has = balloon.has(account);
            ItemStack icon = null;
            if (!has) {
                List<String> lore = new ArrayList<>();
                for (String string : config
                        .getAsStringArray(balloon.canBeSold() ? account.getCoins() < balloon.getCoins() ? "description-enoughcoins" : "description-purchase" : "description-unavailable")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", balloon.getRawName()).replace("{rarity}", rarity).replace("{price}", StringUtils.formatNumber(balloon.getCoins())));
                }
                icon = balloon.getIcon("§c", lore.toArray(new String[lore.size()]));
            } else if (account.hasSelected(balloon)) {
                List<String> lore = new ArrayList<>();
                for (String string : config.getAsStringArray("description-selected")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", balloon.getRawName()).replace("{rarity}", rarity));
                }
                icon = balloon.getIcon("§a", lore.toArray(new String[lore.size()]));
            } else {
                List<String> lore = new ArrayList<>();
                for (String string : config.getAsStringArray("description-unlocked")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", balloon.getRawName()).replace("{rarity}", rarity));
                }
                icon = balloon.getIcon("§a", lore.toArray(new String[lore.size()]));
            }

            items.add(icon);
            this.balloons.put(icon, balloon);
        }


        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < (config.getRows() * 9)) {
                String stack = entry.getValue().getStack();

                // COINS
                stack = stack.replace("{coins}", StringUtils.formatNumber(account.getCoins()));

                ItemStack item = BukkitUtils.deserializeItemStack(stack);
                this.removeSlotsWith(item, entry.getKey());
                this.actions.put(item, entry.getValue().getAction());
            }
        }

        List<String> lore = getLore(order, filter);

        filterItem = BukkitUtils.createItem(Material.HOPPER, "", 1, 0, "&bDisplaying query", lore, false);
        this.removeSlotsWith(filterItem, 50);


        this.setItems(items);

        this.open();
        this.register();
    }

    private static List<String> getLore(Order order, Filter filter) {
        List<String> lore = new ArrayList<>();
        lore.add("&e&lFilter");
        lore.add("{color}➤ ALL".replace("{color}", filter == Filter.ALL ? "&a" : "&7"));
        lore.add("{color}➤ OWNED ONLY".replace("{color}", filter == Filter.OWNED ? "&a" : "&7"));
        lore.add("{color}➤ NOT OWNED ONLY".replace("{color}", filter == Filter.NOT_OWNED ? "&a" : "&7"));
        lore.add("");
        lore.add("&e&lOrder");
        lore.add("{color}➤ NONE".replace("{color}", order == Order.NONE ? "&a" : "&7"));
        lore.add("{color}➤ ALPHABETICALLY".replace("{color}", order == Order.FROM_A_TO_Z ? "&a" : "&7"));
        lore.add("{color}➤ REVERSED ALPHABETICALLY".replace("{color}", order == Order.FROM_Z_TO_A ? "&a" : "&7"));
        lore.add("{color}➤ RARITY".replace("{color}", order == Order.RARITY ? "&a" : "&7"));
        lore.add("{color}➤ REVERSED RARITY".replace("{color}", order == Order.RARITY_REVERSED ? "&a" : "&7"));
        lore.add("");
        lore.add("&eLeft-click to switch Filter.");
        lore.add("&eRight-click to switch Order.");
        lore.add("&eMiddle-click to search.");
        return lore;
    }

    public void cancel() {
        HandlerList.unregisterAll(this);
        this.balloons.clear();
        this.balloons = null;
        this.actions.clear();
        this.actions = null;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        if (evt.getPlayer().equals(player)) {
            this.cancel();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent evt) {
        if (evt.getPlayer().equals(player) && evt.getInventory().equals(getCurrentInventory())) {
            this.cancel();
        }
    }
}
