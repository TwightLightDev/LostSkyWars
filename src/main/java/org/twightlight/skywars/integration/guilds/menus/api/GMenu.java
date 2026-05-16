package org.twightlight.skywars.integration.guilds.menus.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class GMenu {
    private Map<Integer, Item> contents = new HashMap<>();
    private Inventory inv;
    private BukkitTask dynamicTask;

    private GMenu(int size) {
        inv = Bukkit.createInventory(new GMenuHolder(this),
                size, StringUtils.formatColors("&7Guild Donation"));
    }

    public static GMenu createMenu(int size) {
        return new GMenu(size);
    }

    public void open(Player p) {
        inv.clear();

        for (int i : contents.keySet()) {
            inv.setItem(i, contents.get(i).getItem(p));
        }

        p.openInventory(inv);
    }

    public void addContent(int slot, Item i) {
        contents.put(slot, i);
    }

    public void removeContent(int slot) {
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

    public BukkitTask getDynamicTask() {
        return dynamicTask;
    }

    public void setDynamicTask(BukkitTask dynamicTask) {
        this.dynamicTask = dynamicTask;
    }

    public ItemStack getItemStack(int slot) {
        return inv.getItem(slot);
    }

    public void update(Player p) {
        inv.clear();
        for (Map.Entry<Integer, Item> entry : contents.entrySet()) {
            int slot = entry.getKey();
            ItemStack newItem = entry.getValue().getItem(p);
            inv.setItem(slot, newItem);
        }
        p.updateInventory();
    }

    public void update(int slot, Player p) {
        inv.clear(slot);
        inv.setItem(slot, contents.get(slot).getItem(p));

        p.updateInventory();
    }
}
