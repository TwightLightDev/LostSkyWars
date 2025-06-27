package org.twightlight.skywars.modules.recentgames.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.twightlight.skywars.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RGMenu {
    private Map<Integer, Item> contents = new HashMap<>();
    private Inventory inv;

    private RGMenu() {
        inv = Bukkit.createInventory(new RGMenuHolder(this), 36, StringUtils.formatColors("&7Recent Games"));
    }

    public static RGMenu createMenu() {
        return new RGMenu();
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
}
