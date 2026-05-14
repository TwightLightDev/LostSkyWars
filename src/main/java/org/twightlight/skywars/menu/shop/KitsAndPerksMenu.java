package org.twightlight.skywars.menu.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsPerk;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.menu.shop.kits.KitsMenu;
import org.twightlight.skywars.menu.shop.perks.PerksMenu;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class KitsAndPerksMenu extends PlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("kitsandperks");

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
                            if (menu.startsWith("kits:")) {
                                String groupId = menu.substring("kits:".length());
                                new KitsMenu(player, groupId);
                            } else if (menu.startsWith("perks:")) {
                                String groupId = menu.substring("perks:".length());
                                new PerksMenu(player, groupId);
                            } else if (menu.equalsIgnoreCase("shop")) {
                                new ShopMenu(player);
                            } else if (menu.equalsIgnoreCase("soulwell")) {
                                new SoulWellMenu(player, true);
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

    private Map<ItemStack, ConfigAction> map = new HashMap<>();

    public KitsAndPerksMenu(Player player) {
        super(player, config.getTitle(), config.getRows());
        Account account = Database.getInstance().getAccount(player.getUniqueId());

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                stack = stack.replace("{souls}", StringUtils.formatNumber(account.getInt("souls")));
                stack = stack.replace("{coins}", StringUtils.formatNumber(account.getInt("coins")));

                long perkTotal = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_PERK).size();
                long perkOwned = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_PERK).stream().filter(c -> c.has(account)).count();
                int perkPercentage = perkTotal > 0 ? (int) ((100.0 * perkOwned) / perkTotal) : 0;
                Cosmetic selectedPerk = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, 1);
                stack = stack.replace("{perks_has}", String.valueOf(perkOwned));
                stack = stack.replace("{perks_max}", String.valueOf(perkTotal));
                stack = stack.replace("{perks_percentage}", perkPercentage + "%");
                stack = stack.replace("{perks_current}", selectedPerk == null || !(selectedPerk instanceof SkyWarsPerk) ? "None" : ((SkyWarsPerk) selectedPerk).getRawName());

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
