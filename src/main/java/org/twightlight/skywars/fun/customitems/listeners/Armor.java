package org.twightlight.skywars.fun.customitems.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.twightlight.skywars.fun.customitems.CustomItemsManager;
import org.twightlight.skywars.fun.customitems.FunItem;
import org.twightlight.skywars.fun.customitems.armorequip.ArmorEquipEvent;

public class Armor implements Listener {
    @EventHandler
    public void onArmorEquip(ArmorEquipEvent e) {
        if (e.getNewArmorPiece() == null || e.getNewArmorPiece().getType() == Material.AIR) return;

        FunItem funItem = CustomItemsManager.getItem(e.getNewArmorPiece());
        if (funItem != null) {
            funItem.onArmorEquip(e.getPlayer(), e, e.getNewArmorPiece());
        }
    }

    @EventHandler
    public void onArmorUnequip(ArmorEquipEvent e) {
        if(e.getOldArmorPiece() == null || e.getOldArmorPiece().getType() == Material.AIR) return;

        FunItem funItem = CustomItemsManager.getItem(e.getOldArmorPiece());
        if (funItem != null) {
            funItem.onArmorUnequip(e.getPlayer(), e, e.getOldArmorPiece());
        }
    }
}
