package org.twightlight.skywars.modules.privategames.menus.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.twightlight.skywars.modules.privategames.PrivateGamesUser;
import org.twightlight.skywars.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PGMenu {
    private Map<Integer, Item> contents = new HashMap<>();
    private Inventory inv;

    private PGMenu(int size) {
        inv = Bukkit.createInventory(new PGMenuHolder(this), size, StringUtils.formatColors("&7Private Games"));
    }

    public static PGMenu createMenu(int size) {
        return new PGMenu(size);
    }

    public void open(PrivateGamesUser p) {
        inv.clear();

        for (int i : contents.keySet()) {
            inv.setItem(i, contents.get(i).getItem(p.getPlayer()));
        }

        p.getPlayer().openInventory(inv);
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
