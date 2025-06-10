package tk.kanaostore.losteddev.skywars.menu.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.ShopMenu;
import tk.kanaostore.losteddev.skywars.menu.api.PlayerMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.kits.InsaneKitsMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.kits.NormalKitsMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.kits.RankedKitsMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.perks.InsanePerksMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.perks.NormalPerksMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.perks.RankedPerksMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class KitsAndPerksMenu extends PlayerMenu {

    private static final ConfigMenu config = ConfigMenu.getByName("kitsandperks");

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
                            if (menu.equalsIgnoreCase("kitsnormal")) {
                                new NormalKitsMenu(player);
                            } else if (menu.equalsIgnoreCase("kitsinsane")) {
                                new InsaneKitsMenu(player);
                            } else if (menu.equalsIgnoreCase("kitsranked")) {
                                new RankedKitsMenu(player);
                            } else if (menu.equalsIgnoreCase("perksnormal")) {
                                new NormalPerksMenu(player);
                            } else if (menu.equalsIgnoreCase("perksinsane")) {
                                new InsanePerksMenu(player);
                            } else if (menu.equalsIgnoreCase("perksranked")) {
                                new RankedPerksMenu(player);
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


                int max = (int) CosmeticServer.SKYWARS.getByType(SkyWarsPerk.class).stream().filter(cosmetic -> cosmetic.getMode() == 1).count(),
                        amount = (int) CosmeticServer.SKYWARS.getByType(SkyWarsPerk.class).stream().filter(cosmetic -> cosmetic.has(account, 1) && cosmetic.getMode() == 1).count(),
                        percentage = (int) ((100.0 * amount) / max);
                Cosmetic c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, 1);
                stack = stack.replace("{nperks_has}", String.valueOf(amount));
                stack = stack.replace("{nperks_max}", String.valueOf(max));
                stack = stack.replace("{nperks_percentage}", percentage + "%");
                stack = stack.replace("{nperks_current}", c == null || !(c instanceof SkyWarsPerk) ? "None" : ((SkyWarsPerk) c).getRawName());

                max = (int) CosmeticServer.SKYWARS.getByType(SkyWarsPerk.class).stream().filter(cosmetic -> cosmetic.getMode() == 2).count();
                amount = (int) CosmeticServer.SKYWARS.getByType(SkyWarsPerk.class).stream().filter(cosmetic -> cosmetic.has(account, 2) && cosmetic.getMode() == 2).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, 2);
                stack = stack.replace("{iperks_has}", String.valueOf(amount));
                stack = stack.replace("{iperks_max}", String.valueOf(max));
                stack = stack.replace("{iperks_percentage}", percentage + "%");
                stack = stack.replace("{iperks_current}", c == null || !(c instanceof SkyWarsPerk) ? "None" : ((SkyWarsPerk) c).getRawName());

                max = (int) CosmeticServer.SKYWARS.getByType(SkyWarsPerk.class).stream().filter(cosmetic -> cosmetic.getMode() == 3).count();
                amount = (int) CosmeticServer.SKYWARS.getByType(SkyWarsPerk.class).stream().filter(cosmetic -> cosmetic.has(account, 3) && cosmetic.getMode() == 3).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, 3);
                stack = stack.replace("{rperks_has}", String.valueOf(amount));
                stack = stack.replace("{rperks_max}", String.valueOf(max));
                stack = stack.replace("{rperks_percentage}", percentage + "%");
                stack = stack.replace("{rperks_current}", c == null || !(c instanceof SkyWarsPerk) ? "None" : ((SkyWarsPerk) c).getRawName());

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
