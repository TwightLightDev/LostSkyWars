package org.twightlight.skywars.fun.customitems.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.twightlight.skywars.fun.customitems.CustomItemsManager;
import org.twightlight.skywars.fun.customitems.FunItem;

public class BlockBreak implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        FunItem funItem = CustomItemsManager.getItem(e.getPlayer().getItemInHand());
        if (funItem != null) {
            funItem.breakBlockAction(e.getPlayer(), e, e.getBlock(), e.getPlayer().getItemInHand());
        }
    }
}
