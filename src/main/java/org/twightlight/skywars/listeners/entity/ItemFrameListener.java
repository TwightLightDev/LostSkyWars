package org.twightlight.skywars.listeners.entity;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.twightlight.skywars.arena.ui.enums.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.cosmetics.visual.assets.sprays.Spray;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsSpray;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;

import java.util.Iterator;

public class ItemFrameListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame && Spray.getSpray((ItemFrame) event.getEntity()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity() instanceof ItemFrame && Spray.getSpray((ItemFrame) event.getEntity()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame && Spray.getSpray((ItemFrame) event.getEntity()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            for (Entity nearby : block.getWorld().getNearbyEntities(block.getLocation(), 1, 1, 1)) {
                if (nearby instanceof ItemFrame && Spray.getSpray((ItemFrame) event.getEntity()) != null) {
                    it.remove();
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onItemFrameClicked(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        ItemFrame frame = (ItemFrame) event.getRightClicked();
        if (Spray.getSpray(frame) == null) {
            return;
        }
        event.setCancelled(true);
        Spray spray = Spray.getSpray(frame);
        Player player = event.getPlayer();
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        Arena server = null;
        if (account == null || (server = account.getArena()) == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
            return;
        }
        int selectedId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.SPRAY.getSelectionColumn());
        VisualCosmetic cos = VisualCosmetic.findByTypeAndId(VisualCosmeticType.SPRAY, selectedId);
        if (cos instanceof SkyWarsSpray) {
            SkyWarsSpray spray1 = (SkyWarsSpray) cos;
            spray.applyImage(player, spray1);
        }
    }
}
