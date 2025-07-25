package org.twightlight.skywars.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.rank.Rank;
import org.twightlight.skywars.rank.TagUtils;
import org.twightlight.skywars.utils.PlayerUtils;
import org.twightlight.skywars.utils.StringUtils;
import org.twightlight.skywars.world.WorldServer;

public class PlayerJoinListener extends Listeners {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        evt.setJoinMessage(null);

        TagUtils.sendTeams(player);
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (Core.MODE == CoreMode.ARENA) {
            if (WorldServer.listServers().size() > 0) {
                WorldServer.listServers().stream().findFirst().get().connect(account);
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
