package org.twightlight.skywars.menu.shop.kits;

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
import org.twightlight.skywars.cosmetics.skywars.SkyWarsKit;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.menu.MenuConfig;
import org.twightlight.skywars.menu.MenuConfig.ConfigAction;
import org.twightlight.skywars.menu.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PagedPlayerMenu;
import org.twightlight.skywars.nms.Sound;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class KitSelectorMenu extends PagedPlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("kitselector");

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
                    SkyWarsKit kit = kits.get(item);
                    if (evt.getSlot() == this.previousPage) {
                        this.openPrevious();
                    } else if (evt.getSlot() == this.nextPage) {
                        this.openNext();
                    } else if (kit != null) {
                        if (!kit.has(account) || (kit.isPermissible() && !kit.hasByPermission(player))) {
                            Sound.ENDERMAN_TELEPORT.play(player, 1.0F, 1.0F);
                            return;
                        }

                        Sound.NOTE_PLING.play(player, 1.0F, 1.0F);
                        if (account.hasSelected(kit, kit.getMode())) {
                            player.sendMessage(StringUtils.formatColors(config.getAsString("deselect").replace("{name}", kit.getRawName())));
                            account.setSelected(kit.getServer(), kit.getType(), kit.getMode(), 0);
                            new KitSelectorMenu(player, groupId);
                            return;
                        }

                        player.sendMessage(StringUtils.formatColors(config.getAsString("select").replace("{name}", kit.getRawName())));
                        account.setSelected(kit, kit.getMode());
                        new KitSelectorMenu(player, groupId);
                    } else {
                        ConfigAction action = actions.get(item);
                        if (action != null && !action.getType().equals("NOTHING")) {
                            if (action.getType().equals("OPEN")) {
                                String menu = action.getValue();
                                if (menu.equalsIgnoreCase("closeinv")) {
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
    private Map<ItemStack, SkyWarsKit> kits;
    private Map<ItemStack, ConfigAction> actions;

    public KitSelectorMenu(Player player, String groupId) {
        super(player, config.getTitle(), config.getRows());
        this.groupId = groupId;
        this.kits = new HashMap<>();
        this.actions = new HashMap<>();
        this.previousPage = 45;
        this.nextPage = 53;
        this.previousStack = config.getAsString("previous-page");
        this.nextStack = config.getAsString("next-page");
        this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        List<ItemStack> items = new ArrayList<>();
        for (Cosmetic cosmetic : CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_KIT)) {
            SkyWarsKit kit = (SkyWarsKit) cosmetic;
            if (!kit.isAllowedInGroup(groupId)) {
                continue;
            }
            String rarity = kit.getRarity().getName();
            boolean has = kit.has(account) && kit.hasByPermission(player);
            ItemStack icon;
            if (!has) {
                List<String> lore = new ArrayList<>();
                for (String string : config.getAsStringArray("description-locked")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", kit.getRawName()).replace("{rarity}", rarity));
                }
                icon = kit.getIcon("c", lore.toArray(new String[lore.size()]));
                icon.setType(Material.matchMaterial("STAINED_GLASS_PANE"));
                icon.setDurability((short) 14);
            } else if (account.hasSelected(kit, kit.getMode())) {
                List<String> lore = new ArrayList<>();
                for (String string : config.getAsStringArray("description-selected")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", kit.getRawName()).replace("{rarity}", rarity));
                }
                icon = kit.getIcon("a", lore.toArray(new String[lore.size()]));
            } else {
                List<String> lore = new ArrayList<>();
                for (String string : config.getAsStringArray("description-unlocked")) {
                    lore.add(StringUtils.formatColors(string).replace("{name}", kit.getRawName()).replace("{rarity}", rarity));
                }
                icon = kit.getIcon("a", lore.toArray(new String[lore.size()]));
            }

            items.add(icon);
            this.kits.put(icon, kit);
        }

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < (config.getRows() * 9)) {
                String stack = entry.getValue().getStack();

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
        this.kits.clear();
        this.kits = null;
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
