package tk.kanaostore.losteddev.skywars.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import tk.kanaostore.losteddev.skywars.setup.InventoryHolder;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryHolder) {
            if (((InventoryHolder) e.getInventory().getHolder()).getCloseExecutable() != null) {
                ((InventoryHolder) e.getInventory().getHolder()).getCloseExecutable().execute(e);
            }
        }
    }
}
