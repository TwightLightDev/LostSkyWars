package org.twightlight.skywars.modules.api.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public abstract class ModulesMenu {
    protected Map<Integer, Item> contents = new HashMap<>();
    protected Inventory inv;
    protected int size;

    protected ModulesMenu(int size) {
        this.size = size;
    }

    public void open(Player p) {
        inv.clear();

        for (int i : contents.keySet()) {
            inv.setItem(i, getItem(i).getItem(p.getPlayer()));
        }

        p.openInventory(inv);
    }

    public void setItem(int slot, Item i) {
        contents.put(slot, i);
    }

    public void removeItem(int slot) {
        contents.remove(slot);
    }

    public boolean hasItem(int slot) {
        return contents.containsKey(slot);
    }

    public Item getItem(int slot) {
        if (hasItem(slot)) {
            return contents.get(slot);
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public void clear() {
        contents.clear();
        inv.clear();
    }
}
