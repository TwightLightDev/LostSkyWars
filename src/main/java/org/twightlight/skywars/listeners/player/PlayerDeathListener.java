package org.twightlight.skywars.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.player.Account;

import java.util.List;
import java.util.UUID;

public class PlayerDeathListener extends Listeners {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        Player player = evt.getEntity();
        evt.setDeathMessage(null);

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account != null) {
            Arena server = account.getArena();
            if (server == null) {
                evt.setDroppedExp(0);
                player.setHealth(20.0);
                Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> account.refreshPlayer(), 3);
            } else {
                player.setHealth(20.0);
                List<Account> hitters = account.getLastHitters();
                Account killer = hitters.size() > 0 ? hitters.get(0) : null;

                EntityDamageEvent lastDamage = player.getLastDamageCause();
                if (lastDamage instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) lastDamage).getDamager();
                    if (!(damager instanceof Player)) {
                        if (damager.hasMetadata("owner")) {
                            UUID ownerId = UUID.fromString(damager.getMetadata("owner").get(0).asString());
                            killer = Database.getInstance().getAccount(ownerId);
                        }
                        server.kill(account, killer, true);
                    } else {
                        server.kill(account, killer, false);
                    }
                } else {
                    server.kill(account, killer, false);
                }

                for (Account hitter : hitters) {
                    if (hitter != null && (killer == null || !hitter.equals(killer))
                            && (hitter.getArena() != null && hitter.getArena().equals(server))
                            && hitter.getPlayer() != null && !server.isSpectator(hitter.getPlayer())) {
                        if (!server.isPrivate() && server.getGroup().hasTrait("has_stats")) {
                            hitter.addStat(server.getGroup().getId() + "_assists");
                        }
                    }
                }
            }
        }
    }
}
