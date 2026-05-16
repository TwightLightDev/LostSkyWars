package org.twightlight.skywars.menu.shop.perks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.cosmetics.perk.Perk;
import org.twightlight.skywars.cosmetics.perk.PerkManager;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PagedPlayerMenu;
import org.twightlight.skywars.menu.shop.KitsAndPerksMenu;
import org.twightlight.skywars.nms.enums.Sound;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerksMenu extends PagedPlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("perks");

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
                    Perk perk = perks.get(item);
                    if (evt.getSlot() == this.previousPage) {
                        this.openPrevious();
                    } else if (evt.getSlot() == this.nextPage) {
                        this.openNext();
                    } else if (perk != null) {
                        if (perk.hasByPermission(player) && !perk.has(account, groupId)) {
                            if (!perk.canBeSold()) {
                                player.sendMessage(StringUtils.formatColors(config.getAsString("unavailable").replace("{name}", perk.getRawName())));
                                return;
                            }

                            if (account.getCoins() < perk.getCoins()) {
                                player.sendMessage(StringUtils.formatColors(config.getAsString("enoughcoins").replace("{name}", perk.getRawName())));
                                return;
                            }

                            new ConfirmPerkMenu(player, perk, groupId);
                            return;
                        }
                        Sound.NOTE_PLING.play(player, 1.0F, 1.0F);
                        if (account.getSelectedContainer().getSelectedPerk(groupId) == perk.getId()) {
                            player.sendMessage(StringUtils.formatColors(config.getAsString("deselect").replace("{name}", perk.getRawName())));
                            account.getSelectedContainer().setSelectedPerk(groupId, 0);
                            new PerksMenu(player, groupId);
                            return;
                        }

                        player.sendMessage(StringUtils.formatColors(config.getAsString("select").replace("{name}", perk.getRawName())));
                        account.getSelectedContainer().setSelectedPerk(groupId, perk.getId());
                        new PerksMenu(player, groupId);
                    } else {
                        ConfigAction action = actions.get(item);
                        if (action != null && !action.getType().equals("NOTHING")) {
                            if (action.getType().equals("OPEN")) {
                                String menu = action.getValue();
                                if (menu.equalsIgnoreCase("shop")) {
                                    new KitsAndPerksMenu(player);
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

    private String groupId;
    private Map<ItemStack, Perk> perks;
    private Map<ItemStack, ConfigAction> actions;

    public PerksMenu(Player player, String groupId) {
        super(player, config.getTitle(), config.getRows());
        this.groupId = groupId;
        this.perks = new HashMap<>();
        this.actions = new HashMap<>();
        this.previousPage = 45;
        this.nextPage = 53;
        this.previousStack = config.getAsString("previous-page");
        this.nextStack = config.getAsString("next-page");
        this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        List<ItemStack> items = new ArrayList<>();
        for (Perk perk : PerkManager.listForGroup(groupId)) {
            String rarity = perk.getRarity().getName();
            boolean has = perk.has(account, groupId) && perk.hasByPermission(player);
            ItemStack icon;
            if (!has) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                for (String string : config.getAsStringArray(perk.canBeSold() ? account.getCoins() < perk.getCoins() ? "description-enoughcoins" : "description-purchase" : "description-unavailable")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", perk.getRawName()).replace("{rarity}", rarity).replace("{price}", StringUtils.formatNumber(perk.getCoins())));
                }
                icon = perk.getIcon("\u00a7c", lore.toArray(new String[lore.size()]));
            } else if (account.getSelectedContainer().getSelectedPerk(groupId) == perk.getId()) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                for (String string : config.getAsStringArray("description-selected")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", perk.getRawName()).replace("{rarity}", rarity));
                }
                icon = perk.getIcon("\u00a7a", lore.toArray(new String[lore.size()]));
            } else {
                List<String> lore = new ArrayList<>();
                lore.add("");
                for (String string : config.getAsStringArray("description-unlocked")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", perk.getRawName()).replace("{price}", StringUtils.formatNumber(perk.getCoins())).replace("{rarity}", rarity));
                }
                icon = perk.getIcon("\u00a7a", lore.toArray(new String[lore.size()]));
            }

            items.add(icon);
            this.perks.put(icon, perk);
        }

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < (config.getRows() * 9)) {
                String stack = entry.getValue().getStack();
                stack = stack.replace("{coins}", StringUtils.formatNumber(account.getCoins()));

                ItemStack item = BukkitUtils.deserializeItemStack(stack);
                this.removeSlotsWith(item, entry.getKey());
                this.actions.put(item, entry.getValue().getAction());
            }
        }

        this.setItems(items);

        this.open();
        this.register();
    }

    public void cancel() {
        HandlerList.unregisterAll(this);
        this.perks.clear();
        this.perks = null;
        this.actions.clear();
        this.actions = null;
        this.groupId = null;
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
