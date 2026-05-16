package org.twightlight.skywars.fun.customitems.assets;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.fun.customitems.FunItem;
import org.twightlight.skywars.fun.customitems.armorequip.ArmorEquipEvent;
import org.twightlight.skywars.utils.bukkit.ItemBuilder;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.UUID;

public class VoidBlock extends FunItem {
    private List<UUID> activateList = new ArrayList<>();

    public VoidBlock(String id) {
        super(id, (p) -> {
            return new ItemBuilder(XMaterial.BEDROCK).setName("&8Void Block").setLore("&7Once you are almost being consumed", "&7by the abyss. This block will", "&7give you another chance!").toItemStack();
        });
        Bukkit.getPluginManager().registerEvents(new VoidListener(), SkyWars.getInstance());
    }

    @Override
    public boolean leftClickAirAction(Player player, PlayerInteractEvent event, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean leftClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean rightClickAirAction(Player player, PlayerInteractEvent event, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean rightClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean shiftLeftClickAirAction(Player player, PlayerInteractEvent event, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean shiftLeftClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean shiftRightClickAirAction(Player player, PlayerInteractEvent event, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean shiftRightClickBlockAction(Player player, PlayerInteractEvent event, Block block, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean middleClickAction(Player player, PlayerInteractEvent event, ItemStack item) {
        event.setCancelled(true);
        return true;
    }

    @Override
    public boolean hitEntityAction(Player player, EntityDamageByEntityEvent event, Entity target, ItemStack item) {
        return true;
    }

    @Override
    public boolean beDamagedAction(Player player, EntityDamageByEntityEvent event, Entity damager, ItemStack item) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            player.setVelocity(new Vector(0, 100, 0));
            Location loc = player.getLocation();
            player.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY() + 20, loc.getZ()));
            UUID uuid = player.getUniqueId();
            activateList.add(uuid);

            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (!player.isOnline() || ticks > 100 || player.isOnGround()) {
                        this.cancel();
                        return;
                    }

                    if (player.getVelocity().getY() < -1) {
                        player.setVelocity(player.getVelocity().setY(-1));
                    }

                    ticks++;
                }
            }.runTaskTimer(SkyWars.getInstance(), 0L, 1L);
        }
        return true;
    }

    @Override
    public boolean breakBlockAction(Player player, BlockBreakEvent event, Block block, ItemStack item) {
        return true;
    }

    @Override
    public boolean dropAction(Player player, PlayerDropItemEvent event, ItemStack item) {
        return true;
    }

    @Override
    public boolean onHolding(Player player, EventObject event, ItemStack item) {
        return true;
    }

    @Override
    public boolean onUnholding(Player player, EventObject event, ItemStack item) {
        return true;
    }

    @Override
    public boolean onArmorEquip(Player player, ArmorEquipEvent event, ItemStack item) {
        return true;
    }

    @Override
    public boolean onArmorUnequip(Player player, ArmorEquipEvent event, ItemStack item) {
        return true;
    }

    public class VoidListener implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onFallDamage(EntityDamageEvent e) {
            if (!(e.getEntity() instanceof Player)) return;

            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Player p = (Player) e.getEntity();
                if (activateList.contains(p.getUniqueId())) {
                    e.setCancelled(true);
                    activateList.remove(p.getUniqueId());
                }
            }
        }
    }
}
