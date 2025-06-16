package org.twightlight.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.cmd.sw.BuildCommand;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.setup.InventoryHolder;

public class InventoryClickListener extends Listeners {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getWhoClicked() instanceof Player) {
            ItemStack item = evt.getCurrentItem();
            Player player = (Player) evt.getWhoClicked();
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null) {
                return;
            }

            SkyWarsServer server = account.getServer();
            if (server == null) {
                evt.setCancelled(!BuildCommand.isBuilder(player));
                if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(player.getInventory())) {
                    for (int slot : new int[]{Language.lobby$hotbar$profile$slot, Language.lobby$hotbar$shop$slot, Language.lobby$hotbar$players$slot}) {
                        if (evt.getSlot() == slot) {
                            if (item != null && item.hasItemMeta()) {
                                PlayerInteractListener.handleClick(player, account, item.getItemMeta().getDisplayName(), evt);
                            }
                        }
                    }
                }
            } else {
                if (server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
                    evt.setCancelled(true);
                    if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(player.getInventory())) {
                        for (int slot : new int[]{Language.game$hotbar$kits$slot, Language.game$hotbar$play_again$slot, Language.game$hotbar$quit$slot,
                                Language.game$hotbar$quit_spectator$slot}) {
                            if (evt.getSlot() == slot) {
                                if (item != null && item.hasItemMeta()) {
                                    PlayerInteractListener.handleClickArena(player, account, server, item.getItemMeta().getDisplayName(), evt);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick_2(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof InventoryHolder)) return;

        InventoryHolder holder = (InventoryHolder) e.getInventory().getHolder();
        int slot = e.getRawSlot();

        if (slot < 0 || slot >= e.getInventory().getSize()) return;

        if (holder.getButtonsMap().containsKey(slot)) {
            e.setCancelled(true);
            holder.getButtonsMap().get(slot).execute(e);
        } else {
            e.setCancelled(holder.willCancelEvent());
        }
    }
}
