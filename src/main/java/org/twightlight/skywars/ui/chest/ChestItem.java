package org.twightlight.skywars.ui.chest;

import org.bukkit.inventory.ItemStack;

public class ChestItem {

    private ItemStack item;
    private int weight;

    public ChestItem(ItemStack item, int weight) {
        this.item = item;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public ItemStack getItem() {
        return item;
    }
}
