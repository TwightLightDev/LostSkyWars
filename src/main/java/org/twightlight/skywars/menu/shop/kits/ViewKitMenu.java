package org.twightlight.skywars.menu.shop.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ViewKitMenu extends PlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("viewkit");

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
                                new KitsMenu(player, groupId);
                            } else if (menu.equalsIgnoreCase("buy")) {
                                if (kit.has(account)) {
                                    player.sendMessage(StringUtils.formatColors(config.getAsString("already-unlocked")));
                                    return;
                                }

                                if (account.getInt("coins") < kit.getCoins()) {
                                    player.sendMessage(StringUtils.formatColors(config.getAsString("enough-coins")));
                                    return;
                                }

                                new ConfirmKitMenu(player, kit, groupId);
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

    private SkyWarsKit kit;
    private String groupId;
    private Map<ItemStack, ConfigAction> map = new HashMap<>();

    public ViewKitMenu(Player player, SkyWarsKit kit, String groupId) {
        super(player, config.getTitle().replace("{kit}", kit.getRawName()), config.getRows());
        this.kit = kit;
        this.groupId = groupId;
        Account account = Database.getInstance().getAccount(player.getUniqueId());

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();
                if (stack.equalsIgnoreCase("{kit_icon}")) {
                    this.setItem(entry.getKey(), kit.getIcon("a"));
                    this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
                    continue;
                }

                if (stack.equalsIgnoreCase("{buy}")) {
                    if (kit.has(account)) {
                        List<String> lore = new ArrayList<>();
                        for (String string : config.getAsStringArray("description-unlocked")) {
                            lore.add(StringUtils.formatColors(string).replace("{name}", kit.getRawName()).replace("{price}", StringUtils.formatNumber(kit.getCoins())).replace("{rarity}",
                                    kit.getRarity().getName()));
                        }
                        ItemStack iconItem = kit.getIcon("a", lore.toArray(new String[lore.size()]));
                        iconItem.setType(Material.matchMaterial(config.getAsString("buy-material").toUpperCase()));
                        iconItem.setDurability((short) config.getAsInt("buy-cant"));
                        lore.clear();
                        this.setItem(entry.getKey(), iconItem);
                        this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
                        continue;
                    }

                    boolean canBuy = kit.hasByPermission(player) && account.getInt("coins") >= kit.getCoins();
                    List<String> lore = new ArrayList<>();
                    for (String string : config.getAsStringArray("description-locked")) {
                        lore.add(StringUtils.formatColors(string).replace("{name}", kit.getRawName()).replace("{price}", StringUtils.formatNumber(kit.getCoins())).replace("{rarity}",
                                kit.getRarity().getName()));
                    }
                    ItemStack iconItem = kit.getIcon(canBuy ? "a" : "c", lore.toArray(new String[lore.size()]));
                    iconItem.setType(Material.matchMaterial(config.getAsString("buy-material").toUpperCase()));
                    iconItem.setDurability((short) config.getAsInt("buy-can"));
                    lore.clear();
                    this.setItem(entry.getKey(), iconItem);
                    this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
                    continue;
                }

                stack = stack.replace("{coins}", StringUtils.formatNumber(account.getInt("coins")));

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
        kit = null;
        groupId = null;
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
