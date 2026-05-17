package org.twightlight.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.bungee.core.Core;
import org.twightlight.skywars.bungee.core.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.player.Account;

public class PlayerJoinListener extends Listeners {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        player.removeMetadata("frozen", SkyWars.getInstance());
        evt.setJoinMessage(null);

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (Core.MODE == CoreMode.ARENA) {
            if (!Arena.listArenas().isEmpty()) {
                Arena.listArenas().stream().findFirst().get().connect(account);
            }
        } else {
            account.reloadScoreboard();
            account.refreshPlayer();
            account.refreshPlayers();
        }

        if (Language.lobby$tablist$enabled) {
            NMS.sendTabHeaderFooter(player, Language.lobby$tablist$header, Language.lobby$tablist$footer);
        }
    }
}
