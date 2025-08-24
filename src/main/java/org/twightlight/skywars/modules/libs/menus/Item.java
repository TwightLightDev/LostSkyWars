package org.twightlight.skywars.modules.libs.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.ui.Executable;

import java.util.function.Function;

public class Item {
    private Executable<InventoryClickEvent> executable;
    private Function<Player, ItemStack> itemStackFunction;

    public Item(Executable<InventoryClickEvent> e, Function<Player, ItemStack> creator) {
        this.executable = e;
        this.itemStackFunction = creator;
    }

    public ItemStack getItem(Player p) {
        ItemStack is = itemStackFunction.apply(p);
        return (is != null) ? is : new ItemStack(Material.BEDROCK);
    }

    public Executable<InventoryClickEvent> getExecutable() {
        return executable;
    }
}
