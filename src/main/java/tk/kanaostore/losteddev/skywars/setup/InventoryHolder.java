package tk.kanaostore.losteddev.skywars.setup;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryHolder implements org.bukkit.inventory.InventoryHolder {
    private Inventory inv;
    private Map<Integer, Executable<InventoryClickEvent>> buttonsMap = new HashMap<>();
    private Executable<InventoryCloseEvent> closeExecutable;
    private boolean cancelEvent;

    public InventoryHolder() {
        new InventoryHolder(true);
    }

    public InventoryHolder(boolean cancelEvent) {
        this.cancelEvent = cancelEvent;
    }

    public void setInventory(Inventory inv) {
        this.inv = inv;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public Map<Integer, Executable<InventoryClickEvent>> getButtonsMap() {
        return buttonsMap;
    }

    public void setButton(Integer slot, Executable<InventoryClickEvent> executable) {
        if (executable == null) {
            buttonsMap.put(slot, e -> {
                return;
            });
            return;
        }
        buttonsMap.put(slot, executable);
    }

    public void setCloseExecutable(Executable<InventoryCloseEvent> closeExecutable) {
        this.closeExecutable = closeExecutable;
    }

    public Executable<InventoryCloseEvent> getCloseExecutable() {
        return closeExecutable;
    }

    public boolean willCancelEvent() {
        return cancelEvent;
    }
}
