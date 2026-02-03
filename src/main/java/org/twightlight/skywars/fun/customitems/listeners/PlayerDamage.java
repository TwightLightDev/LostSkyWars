package org.twightlight.skywars.fun.customitems.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.twightlight.skywars.fun.customitems.CustomItemsManager;
import org.twightlight.skywars.fun.customitems.FunItem;

public class PlayerDamage implements Listener {
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        FunItem funItem = CustomItemsManager.getItem(p.getItemInHand());
        if (funItem != null) {
            funItem.hitEntityAction(p, e, e.getEntity(), p.getItemInHand());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        FunItem funItem = CustomItemsManager.getItem(p.getItemInHand());
        if (funItem != null) {
            funItem.beDamagedAction(p, e, e.getDamager(), p.getItemInHand());
        }
    }
}
