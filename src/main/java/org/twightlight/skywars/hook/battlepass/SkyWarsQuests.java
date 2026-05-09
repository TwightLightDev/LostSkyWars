package org.twightlight.skywars.hook.battlepass;

import net.advancedplugins.bp.impl.actions.containers.ExternalActionContainer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.twightlight.skywars.api.event.game.SkyWarsGameEndEvent;
import org.twightlight.skywars.api.event.game.SkyWarsGameStartEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerSpawnEntityEvent;
import org.twightlight.skywars.arena.Arena;

public class SkyWarsQuests extends ExternalActionContainer {
    public SkyWarsQuests(JavaPlugin plugin) {
        super(plugin, "lostskywars");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGameEnd(SkyWarsGameEndEvent e) {
        if (e.getServer() != null) {
            Arena server = e.getServer();

            if (e.hasWinner() && !server.isPrivate()) {
                for (Player p : e.getWinnerTeam().getMembers()) {
                    super.executionBuilder("win")
                            .player(p)
                            .root(server.getName())
                            .subRoot("group", server.getGroup().getId())
                            .progressSingle()
                            .buildAndExecute();
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerKill(SkyWarsPlayerDeathEvent e) {
        if (e.getServer() != null) {
            Arena server = e.getServer();
            if (!server.isPrivate() && e.getKiller() != null) {
                executionBuilder("kill")
                        .player(e.getKiller())
                        .root(e.getCause().name())
                        .subRoot("killed", e.getPlayer().getName())
                        .subRoot("projectile", (e.getKiller() instanceof Projectile) ? "" : (e.getKiller()).getType().name())
                        .progressSingle()
                        .buildAndExecute();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGameStart(SkyWarsGameStartEvent e) {
        if (e.getServer() != null) {
            Arena server = e.getServer();
            if (!server.isPrivate()) {
                for (Player p : server.getPlayers(false)) {
                    executionBuilder("play")
                            .player(p)
                            .root(server.getName())
                            .subRoot("group", server.getGroup().getId())
                            .progressSingle()
                            .buildAndExecute();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMobSummon(SkyWarsPlayerSpawnEntityEvent e) {
        if (e.getServer() != null) {
            Arena server = e.getServer();
            if (!server.isPrivate()) {
                executionBuilder("spawnmob")
                        .player(e.getPlayer())
                        .root(e.getEntity().getType().name())
                        .progressSingle()
                        .buildAndExecute();
            }
        }

    }
}
