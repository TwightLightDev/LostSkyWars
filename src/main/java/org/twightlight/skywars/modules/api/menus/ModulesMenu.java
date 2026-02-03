package org.twightlight.skywars.modules.api.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModulesMenu {
    protected Map<Integer, Item> contents = new HashMap<>();
    protected Inventory inv;
    protected int size;

    protected ModulesMenu(int size) {
        this.size = size;
    }

    public void open(Player p) {
        if (inv == null) return;

        inv.clear();

        for (int i : contents.keySet()) {
            inv.setItem(i, getItem(i).getItem(p.getPlayer()));
        }

        p.openInventory(inv);
    }

    public void update(Player p, int... ints) {
        if (inv == null) return;

        for (int i : ints) {
            ItemStack is = getItem(i).getItem(p.getPlayer());

            ItemStack itemStack = inv.getItem(i);

            if (is.getItemMeta() != null)
                itemStack.setItemMeta(is.getItemMeta());
            itemStack.setDurability(is.getDurability());
            itemStack.setType(is.getType());
            itemStack.setData(is.getData());
        }

    }

    public void update(Player p, List<Integer> ints) {
        if (inv == null) return;

        for (int i : ints) {
            ItemStack is = getItem(i).getItem(p.getPlayer());

            ItemStack itemStack = inv.getItem(i);
            if (is.getItemMeta() != null)
                itemStack.setItemMeta(is.getItemMeta());
            itemStack.setDurability(is.getDurability());
            itemStack.setType(is.getType());
            itemStack.setData(is.getData());
        }
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

    public List<Integer> getSlotsList() {
        return new ArrayList<>(contents.keySet());
    }

    public void remove() {
        contents.forEach((i, a) -> a.remove());
        contents.clear();
        inv.clear();
        inv = null;
    }


}
