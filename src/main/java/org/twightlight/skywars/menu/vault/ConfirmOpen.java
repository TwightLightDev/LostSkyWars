package org.twightlight.skywars.menu.vault;

import io.github.losteddev.boxes.api.box.Box;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.hook.boxes.BoxNPC;
import org.twightlight.skywars.menu.MenuConfig;
import org.twightlight.skywars.menu.MenuConfig.ConfigAction;
import org.twightlight.skywars.menu.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.menu.lobby.MysteryVaultMenu;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;

import java.util.HashMap;
import java.util.Map;

public class ConfirmOpen extends PlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("confirmvault");

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
                            if (menu.equalsIgnoreCase("vault")) {
                                new MysteryVaultMenu(player, npc);
                            } else if (menu.equalsIgnoreCase("confirm")) {
                                npc.open(account, box);
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

    private Box box;
    private BoxNPC npc;
    private Map<ItemStack, ConfigAction> map = new HashMap<>();

    public ConfirmOpen(Player player, Box box, BoxNPC npc) {
        super(player, config.getTitle(), config.getRows());
        this.box = box;
        this.npc = npc;

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                if (stack.equalsIgnoreCase("{box}")) {
                    this.setItem(entry.getKey(), box.getIcon());
                    continue;
                }

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
        box = null;
        npc = null;
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
