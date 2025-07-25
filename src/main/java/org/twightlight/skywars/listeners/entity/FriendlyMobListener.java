package org.twightlight.skywars.listeners.entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerSpawnEntityEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;

import java.util.*;

public class FriendlyMobListener implements Listener {
    private final Map<UUID, Long> recentEggUsers = new HashMap<>();
    private final Map<Player, List<Monster>> friendlyMonsters = new HashMap<>();

    @EventHandler
    public void onUseSpawnEgg(PlayerInteractEvent event) {
        Account account = Database.getInstance().getAccount(event.getPlayer().getUniqueId());
        if (account == null) {
            return;
        }
        SkyWarsServer server = account.getServer();
        if (server == null || server.isSpectator(event.getPlayer())) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType() != Material.MONSTER_EGG) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        recentEggUsers.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;
        if (!(event.getEntity() instanceof Monster)) return;
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Long t = recentEggUsers.get(player.getUniqueId());
            if (t != null && now - t < 1500) {
                recentEggUsers.remove(player.getUniqueId());
                event.getEntity().setCustomName(ChatColor.translateAlternateColorCodes( '&', "&c" + player.getName() + " " + event.getEntity().getName()));
                friendlyMonsters.computeIfAbsent(player, k -> new ArrayList<>());
                friendlyMonsters.get(player).add((Monster) event.getEntity());
                event.getEntity().setMetadata("owner", new FixedMetadataValue(SkyWars.getInstance(), player.getUniqueId().toString()));
                SkyWarsPlayerSpawnEntityEvent e = new SkyWarsPlayerSpawnEntityEvent(player, event.getEntity(), Database.getInstance().getAccount(player.getUniqueId()).getServer());
                Bukkit.getPluginManager().callEvent(e);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerBeingAttacked(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player owner = (Player) event.getEntity();

        if (friendlyMonsters.get(owner) == null) return;

        for (Monster mob : friendlyMonsters.get(owner)) {
            if (event.getDamager() instanceof LivingEntity) {
                if (event.getDamager() instanceof Player) {
                    if (isTeammate(owner, (Player) event.getDamager())) {
                        return;
                    }
                }
                mob.setTarget((LivingEntity) event.getDamager());
            }
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player owner = (Player) event.getDamager();

        if (friendlyMonsters.get(owner) == null) return;

        for (Monster mob : friendlyMonsters.get(owner)) {
            if (event.getEntity() instanceof LivingEntity) {
                if (event.getEntity() instanceof Player) {
                    if (isTeammate(owner, (Player) event.getEntity())) {
                        return;
                    }
                }
                mob.setTarget((LivingEntity) event.getEntity());
            }
        }
    }

    @EventHandler
    public void onMobHurt(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Monster)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Monster mob = (Monster) event.getEntity();
        Entity attacker = event.getDamager();

        if (!mob.hasMetadata("owner")) return;

        UUID ownerId = UUID.fromString(mob.getMetadata("owner").get(0).asString());
        Player owner = Bukkit.getPlayer(ownerId);
        if (owner == null) return;

        if (!isTeammate(owner, (Player) attacker)) {
            mob.setTarget((LivingEntity) attacker);
        }
    }

    @EventHandler
    public void onMobTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Monster)) return;
        if (event.getTarget() instanceof Player) {
            Player target = (Player) event.getTarget();
            Monster mob = (Monster) event.getEntity();
            if (!mob.hasMetadata("owner")) return;

            UUID ownerId = UUID.fromString(mob.getMetadata("owner").get(0).asString());
            Player owner = Bukkit.getPlayer(ownerId);
            if (owner == null) return;

            if (isTeammate(owner, target)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isTeammate(Player owner, Player target) {
        Account account = Database.getInstance().getAccount(owner.getUniqueId());
        if (account == null) {
            return true;
        }
        SkyWarsServer server = account.getServer();
        if (server == null || server.isSpectator(target)) {
            return true;
        }
        if (server.getTeam(owner) == null || server.getTeam(target) == null) {
            return true;
        }
        return server.getTeam(owner) == server.getTeam(target);
    }
}
