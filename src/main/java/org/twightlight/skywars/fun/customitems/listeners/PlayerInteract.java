package org.twightlight.skywars.fun.customitems.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.twightlight.skywars.fun.customitems.CustomItemsManager;
import org.twightlight.skywars.fun.customitems.FunItem;

public class PlayerInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        FunItem funItem = CustomItemsManager.getItem(e.getItem());
        if (funItem != null) {
            switch (e.getAction()) {
                case LEFT_CLICK_BLOCK:
                    if (e.getPlayer().isSneaking()) {
                        funItem.shiftLeftClickBlockAction(e.getPlayer(), e, e.getClickedBlock(), e.getItem());
                        break;
                    }
                    funItem.leftClickBlockAction(e.getPlayer(), e, e.getClickedBlock(), e.getItem());
                    break;
                case RIGHT_CLICK_BLOCK:
                    if (e.getPlayer().isSneaking()) {
                        funItem.shiftRightClickBlockAction(e.getPlayer(), e, e.getClickedBlock(), e.getItem());
                        break;
                    }
                    funItem.rightClickBlockAction(e.getPlayer(), e, e.getClickedBlock(), e.getItem());
                    break;
                case LEFT_CLICK_AIR:
                    if (e.getPlayer().isSneaking()) {
                        funItem.shiftLeftClickAirAction(e.getPlayer(), e, e.getItem());
                        break;
                    }
                    funItem.leftClickAirAction(e.getPlayer(), e, e.getItem());
                    break;
                case RIGHT_CLICK_AIR:
                    if (e.getPlayer().isSneaking()) {
                        funItem.shiftRightClickAirAction(e.getPlayer(), e, e.getItem());
                        break;
                    }
                    funItem.rightClickAirAction(e.getPlayer(), e, e.getItem());
                    break;
                case PHYSICAL:
                    funItem.middleClickAction(e.getPlayer(), e, e.getItem());
                    break;
            }
        }
    }
}
