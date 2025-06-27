package org.twightlight.skywars.setup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.ui.Executable;

public abstract class Menu {
    protected InventoryHolder holder;

    public Menu(int slot, boolean cancelEvent) {
        holder = new InventoryHolder(cancelEvent);
        Inventory inv = Bukkit.createInventory(holder, slot, ChatColor.translateAlternateColorCodes('&', "SetupMenu"));
        holder.setInventory(inv);
    }

    public void setItem(int slot, ItemStack item, Executable<InventoryClickEvent> executable) {
        holder.getInventory().setItem(slot, item);
        holder.setButton(slot, executable);
    }

    public void open(Player p) {
        p.openInventory(holder.getInventory());
    }
}
