package tk.kanaostore.losteddev.skywars.menu.shop.visual_cosmetics.cage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.visual_cosmetics.SkyWarsCage;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.ShopMenu;
import tk.kanaostore.losteddev.skywars.menu.api.PagedPlayerMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.Filter;
import tk.kanaostore.losteddev.skywars.menu.shop.Order;
import tk.kanaostore.losteddev.skywars.nms.Sound;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.setup.ChatSession;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CagesMenu extends PagedPlayerMenu {

    private static final ConfigMenu config = ConfigMenu.getByName("cells");

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
                    SkyWarsCage cage = cages.get(item);
                    if (evt.getSlot() == this.previousPage) {
                        this.openPrevious();
                    } else if (evt.getSlot() == this.nextPage) {
                        this.openNext();
                    } else if (item.isSimilar(filterItem)) {
                        if (evt.getClick().name().contains("RIGHT")) {
                            Order next = Order.next(order);
                            new CagesMenu(player, next, filter, searchQuery);
                        } else if (evt.getClick().name().contains("LEFT")) {
                            Filter next = Filter.next(filter);
                            new CagesMenu(player, order, next, searchQuery);
                        } else if (evt.getClick().name().contains("MIDDLE")) {
                            player.closeInventory();
                            ChatSession sessions = new ChatSession(player);
                            sessions.prompt(Arrays.asList(new String[] {"&aType the value you want: ", "&aType 'cancel' to cancel!"}), (input) -> {
                                if (input.equals("cancel")) {
                                    sessions.end();
                                    Bukkit.getScheduler().runTask(Main.getInstance(),
                                            () -> {
                                                new CagesMenu(player, order, filter, searchQuery);
                                            });
                                    return;
                                }
                                sessions.end();
                                Bukkit.getScheduler().runTask(Main.getInstance(),
                                        () -> {
                                            new CagesMenu(player, order, filter, input);
                                        });
                            });

                        }
                    } else if (cage != null) {
                        if (!cage.has(account) || !cage.hasByPermission(player)) {
                            Sound.ENDERMAN_TELEPORT.play(player, 1.0F, 1.0F);
                            return;
                        }

                        Sound.NOTE_PLING.play(player, 1.0F, 1.0F);
                        if (account.hasSelected(cage)) {
                            player.sendMessage(StringUtils.formatColors(config.getAsString("deselect").replace("{name}", cage.getRawName())));
                            account.setSelected(cage.getServer(), cage.getType(), 1, 0);
                            new CagesMenu(player);
                            return;
                        }

                        player.sendMessage(StringUtils.formatColors(config.getAsString("select").replace("{name}", cage.getRawName())));
                        account.setSelected(cage);
                        new CagesMenu(player);
                    } else {
                        ConfigAction action = actions.get(item);
                        if (action != null && !action.getType().equals("NOTHING")) {
                            if (action.getType().equals("OPEN")) {
                                String menu = action.getValue();
                                if (menu.equalsIgnoreCase("shop")) {
                                    new ShopMenu(player);
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

    private Map<ItemStack, SkyWarsCage> cages;
    private Map<ItemStack, ConfigAction> actions;
    private ItemStack filterItem;
    private Order order;
    private Filter filter;
    private String searchQuery;

    public CagesMenu(Player player) {
        this(player, Order.NONE, Filter.ALL, "");
    }

    public CagesMenu(Player player, Order order, Filter filter, String searchQuery) {
        super(player, config.getTitle(), config.getRows());
        this.cages = new HashMap<>();
        this.actions = new HashMap<>();
        this.previousPage = 45;
        this.nextPage = 53;
        this.order = order;
        this.filter = filter;
        this.searchQuery = searchQuery;
        this.previousStack = config.getAsString("previous-page");
        this.nextStack = config.getAsString("next-page");
        this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        List<ItemStack> items = new ArrayList<>();
        List<Cosmetic> cosmetics = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_CAGE);
        order.accept(cosmetics);
        cosmetics = filter.accept(cosmetics, player);
        cosmetics = cosmetics.stream().filter(cos -> cos.getRawName().toLowerCase().startsWith(searchQuery)).collect(Collectors.toList());
        for (Cosmetic c : cosmetics) {
            SkyWarsCage cage = (SkyWarsCage) c;
            String rarity = cage.getRarity().getName();
            boolean has = cage.has(account) && cage.hasByPermission(player);
            ItemStack icon = null;
            if (!has) {
                List<String> lore = new ArrayList<>();
                for (String string : config.getAsStringArray("description-locked")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", cage.getRawName()).replace("{rarity}", rarity));
                }
                icon = cage.getIcon("§c", lore.toArray(new String[lore.size()]));
            } else if (account.hasSelected(cage)) {
                List<String> lore = new ArrayList<>();
                for (String string : config.getAsStringArray("description-selected")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", cage.getRawName()).replace("{rarity}", rarity));
                }
                icon = cage.getIcon("§a", lore.toArray(new String[lore.size()]));
            } else {
                List<String> lore = new ArrayList<>();
                for (String string : config.getAsStringArray("description-unlocked")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", cage.getRawName()).replace("{rarity}", rarity));
                }
                icon = cage.getIcon("§a", lore.toArray(new String[lore.size()]));
            }

            items.add(icon);
            this.cages.put(icon, cage);
        }

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < (config.getRows() * 9)) {
                String stack = entry.getValue().getStack();

                // COINS
                stack = stack.replace("{coins}", StringUtils.formatNumber(account.getInt("coins")));

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
        this.cages.clear();
        this.cages = null;
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
