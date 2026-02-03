package org.twightlight.skywars.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.rank.Rank;
import org.twightlight.skywars.player.rank.TagUtils;
import org.twightlight.skywars.utils.PlayerUtils;
import org.twightlight.skywars.utils.StringUtils;

public class PlayerJoinListener extends Listeners {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        player.removeMetadata("frozen", SkyWars.getInstance());
        evt.setJoinMessage(null);

        TagUtils.sendTeams(player);
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (Core.MODE == CoreMode.ARENA) {
            if (Arena.listServers().size() > 0) {
                Arena.listServers().stream().findFirst().get().connect(account);
            }
        } else {
            account.reloadScoreboard();
            account.refreshPlayer();
            account.refreshPlayers();
        }

        if (Language.lobby$tablist$enabled) {
            NMS.sendTabHeaderFooter(player, Language.lobby$tablist$header, Language.lobby$tablist$footer);
        }

        Rank rank = Rank.getRank(player);
        if (rank != null && rank.getOnJoin() != null) {
            Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
                Database.getInstance().listAccounts().stream()
                        .filter(a -> a.inLobby())
                        .forEach(a -> a.getPlayer().sendMessage(StringUtils.formatColors(PlayerUtils.replaceAll(player, rank.getOnJoin()))));

            }, 2);
        }
    }
}
