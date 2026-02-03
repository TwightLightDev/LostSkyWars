package org.twightlight.skywars.fun.customitems.armorequip;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorListener implements Listener {
    private final List<String> list = new ArrayList<>();

    public ArmorListener() {
        list.add("FURNACE");
        list.add("CHEST");
        list.add("TRAPPED_CHEST");
        list.add("BEACON");
        list.add("DISPENSER");
        list.add("DROPPER");
        list.add("HOPPER");
        list.add("WORKBENCH");
        list.add("ENCHANTMENT_TABLE");
        list.add("ENDER_CHEST");
        list.add("ANVIL");
        list.add("BED_BLOCK");
        list.add("FENCE_GATE");
        list.add("SPRUCE_FENCE_GATE");
        list.add("BIRCH_FENCE_GATE");
        list.add("ACACIA_FENCE_GATE");
        list.add("JUNGLE_FENCE_GATE");
        list.add("DARK_OAK_FENCE_GATE");
        list.add("IRON_DOOR_BLOCK");
        list.add("WOODEN_DOOR");
        list.add("SPRUCE_DOOR");
        list.add("BIRCH_DOOR");
        list.add("JUNGLE_DOOR");
        list.add("ACACIA_DOOR");
        list.add("DARK_OAK_DOOR");
        list.add("WOOD_BUTTON");
        list.add("STONE_BUTTON");
        list.add("TRAP_DOOR");
        list.add("IRON_TRAPDOOR");
        list.add("DIODE_BLOCK_OFF");
        list.add("DIODE_BLOCK_ON");
        list.add("REDSTONE_COMPARATOR_OFF");
        list.add("REDSTONE_COMPARATOR_ON");
        list.add("FENCE");
        list.add("SPRUCE_FENCE");
        list.add("BIRCH_FENCE");
        list.add("JUNGLE_FENCE");
        list.add("DARK_OAK_FENCE");
        list.add("ACACIA_FENCE");
        list.add("NETHER_FENCE");
        list.add("BREWING_STAND");
        list.add("CAULDRON");
        list.add("SIGN_POST");
        list.add("WALL_SIGN");
        list.add("SIGN");
        list.add("LEVER");
        list.add("BLACK_SHULKER_BOX");
        list.add("BLUE_SHULKER_BOX");
        list.add("BROWN_SHULKER_BOX");
        list.add("CYAN_SHULKER_BOX");
        list.add("GRAY_SHULKER_BOX");
        list.add("GREEN_SHULKER_BOX");
        list.add("LIGHT_BLUE_SHULKER_BOX");
        list.add("LIME_SHULKER_BOX");
        list.add("MAGENTA_SHULKER_BOX");
        list.add("ORANGE_SHULKER_BOX");
        list.add("PINK_SHULKER_BOX");
        list.add("PURPLE_SHULKER_BOX");
        list.add("RED_SHULKER_BOX");
        list.add("SILVER_SHULKER_BOX");
        list.add("WHITE_SHULKER_BOX");
        list.add("YELLOW_SHULKER_BOX");
        list.add("DAYLIGHT_DETECTOR_INVERTED");
        list.add("DAYLIGHT_DETECTOR");
    }

    @EventHandler
    public final void inventoryClick(InventoryClickEvent e) {
        boolean shift = false, numberkey = false;
        if (e.isCancelled())
            return;
        if (e.getAction() == InventoryAction.NOTHING)
            return;
        if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT))
            shift = true;
        if (e.getClick().equals(ClickType.NUMBER_KEY))
            numberkey = true;
        if (e.getSlotType() != InventoryType.SlotType.ARMOR && e.getSlotType() != InventoryType.SlotType.QUICKBAR && e.getSlotType() != InventoryType.SlotType.CONTAINER)
            return;
        if (e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER))
            return;
        if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER))
            return;
        if (!(e.getWhoClicked() instanceof Player))
            return;
        ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
        if (!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot())
            return;
        if (shift) {
            newArmorType = ArmorType.matchType(e.getCurrentItem());
            if (newArmorType != null) {
                boolean equipping = true;
                if (e.getRawSlot() == newArmorType.getSlot())
                    equipping = false;
                if ((newArmorType.equals(ArmorType.HELMET) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getHelmet()) : !isAirOrNull(e.getWhoClicked().getInventory().getHelmet()))) || (newArmorType.equals(ArmorType.CHESTPLATE) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getChestplate()) : !isAirOrNull(e.getWhoClicked().getInventory().getChestplate()))) || (newArmorType.equals(ArmorType.LEGGINGS) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getLeggings()) : !isAirOrNull(e.getWhoClicked().getInventory().getLeggings()))) || (newArmorType.equals(ArmorType.BOOTS) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getBoots()) : !isAirOrNull(e.getWhoClicked().getInventory().getBoots())))) {
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
                    Bukkit.getServer().getPluginManager().callEvent((Event) armorEquipEvent);
                    if (armorEquipEvent.isCancelled())
                        e.setCancelled(true);
                }
            }
        } else {
            ItemStack newArmorPiece = e.getCursor();
            ItemStack oldArmorPiece = e.getCurrentItem();
            if (numberkey) {
                if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                    ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
                    if (!isAirOrNull(hotbarItem)) {
                        newArmorType = ArmorType.matchType(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
                    } else {
                        newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
                    }
                }
            } else if (isAirOrNull(e.getCursor()) && !isAirOrNull(e.getCurrentItem())) {
                newArmorType = ArmorType.matchType(e.getCurrentItem());
            }
            if (newArmorType != null && e.getRawSlot() == newArmorType.getSlot()) {
                ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.PICK_DROP;
                if (e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey)
                    method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
                Bukkit.getServer().getPluginManager().callEvent((Event) armorEquipEvent);
                if (armorEquipEvent.isCancelled())
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL)
            return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Material mat = e.getClickedBlock().getType();
                for (String s : this.list) {
                    if (mat.name().equalsIgnoreCase(s))
                        return;
                }
            }
            ArmorType newArmorType = ArmorType.matchType(e.getItem());
            if (newArmorType != null && ((
                    newArmorType.equals(ArmorType.HELMET) && isAirOrNull(e.getPlayer().getInventory().getHelmet())) || (newArmorType.equals(ArmorType.CHESTPLATE) && isAirOrNull(e.getPlayer().getInventory().getChestplate())) || (newArmorType.equals(ArmorType.LEGGINGS) && isAirOrNull(e.getPlayer().getInventory().getLeggings())) || (newArmorType.equals(ArmorType.BOOTS) && isAirOrNull(e.getPlayer().getInventory().getBoots())))) {
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
                Bukkit.getServer().getPluginManager().callEvent((Event) armorEquipEvent);
                if (armorEquipEvent.isCancelled()) {
                    e.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void inventoryDrag(InventoryDragEvent event) {
        ArmorType type = ArmorType.matchType(event.getOldCursor());
        if (event.getRawSlots().isEmpty())
            return;
        if (type != null && type.getSlot() == ((Integer) event.getRawSlots().stream().findFirst().orElse(Integer.valueOf(0))).intValue()) {
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(), ArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor());
            Bukkit.getServer().getPluginManager().callEvent((Event) armorEquipEvent);
            if (armorEquipEvent.isCancelled()) {
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e) {
        ArmorType type = ArmorType.matchType(e.getBrokenItem());
        if (type != null) {
            Player p = e.getPlayer();
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent((Event) armorEquipEvent);
            if (armorEquipEvent.isCancelled()) {
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));
                if (type.equals(ArmorType.HELMET)) {
                    p.getInventory().setHelmet(i);
                } else if (type.equals(ArmorType.CHESTPLATE)) {
                    p.getInventory().setChestplate(i);
                } else if (type.equals(ArmorType.LEGGINGS)) {
                    p.getInventory().setLeggings(i);
                } else if (type.equals(ArmorType.BOOTS)) {
                    p.getInventory().setBoots(i);
                }
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e) {
        Player p = e.getEntity();
        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (!isAirOrNull(i))
                Bukkit.getServer().getPluginManager().callEvent((Event) new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
        }
    }

    private boolean isAirOrNull(ItemStack item) {
        return (item == null || item.getType().equals(Material.AIR));
    }
}