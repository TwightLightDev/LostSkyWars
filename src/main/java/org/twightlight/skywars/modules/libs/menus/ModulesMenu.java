package org.twightlight.skywars.modules.libs.menus;

import com.comphenix.protocol.PacketType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.twightlight.skywars.modules.privategames.User;

import java.util.HashMap;
import java.util.Map;

public abstract class ModulesMenu {
    protected Map<Integer, Item> contents = new HashMap<>();
    protected Inventory inv;

    protected ModulesMenu(int size) {}

    public void open(Player p) {
        inv.clear();

        for (int i : contents.keySet()) {
            inv.setItem(i, contents.get(i).getItem(p.getPlayer()));
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
