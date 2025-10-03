package org.twightlight.skywars.fun.customitems;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.fun.customitems.armorequip.ArmorEquipEvent;

import java.util.EventObject;
import java.util.function.Function;

public abstract class FunItem {
    protected String id;

    protected Function<Player, ItemStack> itemFactory;

    public FunItem(String id, Function<Player, ItemStack> function) {
        this.id = id;
        this.itemFactory = function;
    }

    public void give(Player player) {
        ItemStack itemStack = itemFactory.apply(player);

        NBT.modify(itemStack, nbt -> {
            nbt.setString("funitem", id);
        });

        player.getInventory().addItem(itemStack);
        player.updateInventory();
    }

    public String getId() {
        return id;
    }

    public Function<Player, ItemStack> getItemFactory() {
        return itemFactory;
    }

    public abstract boolean leftClickAirAction(Player player, PlayerInteractEvent event, ItemStack item);

    public abstract boolean leftClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item);

    public abstract boolean rightClickAirAction(Player player, PlayerInteractEvent event, ItemStack item);

    public abstract boolean rightClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item);

    public abstract boolean shiftLeftClickAirAction(Player player, PlayerInteractEvent event, ItemStack item);

    public abstract boolean shiftLeftClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item);

    public abstract boolean shiftRightClickAirAction(Player player, PlayerInteractEvent event, ItemStack item);

    public abstract boolean shiftRightClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item);

    public abstract boolean middleClickAction(Player player, PlayerInteractEvent event, ItemStack item);

    public abstract boolean hitEntityAction(Player player, EntityDamageByEntityEvent event, Entity target, ItemStack item);

    public abstract boolean beDamagedAction(Player player, EntityDamageByEntityEvent event, Entity damager, ItemStack item);

    public abstract boolean breakBlockAction(Player player, BlockBreakEvent event, Block block, ItemStack item);

    public abstract boolean dropAction(Player player, PlayerDropItemEvent event, ItemStack item);

    public abstract boolean onHolding(Player player, EventObject event, ItemStack item);

    public abstract boolean onUnholding(Player player, EventObject event, ItemStack item);

    public abstract boolean onArmorEquip(Player player, ArmorEquipEvent event, ItemStack item);

    public abstract boolean onArmorUnequip(Player player, ArmorEquipEvent event, ItemStack item);

}
