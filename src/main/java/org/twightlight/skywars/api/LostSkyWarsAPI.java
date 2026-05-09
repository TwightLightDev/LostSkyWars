package org.twightlight.skywars.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.api.player.LostPlayer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;

public class LostSkyWarsAPI {

    public static void tryWatch(Player watcher, String usernameToWatch) {
        Account account = null;
        Player target = Bukkit.getPlayer(usernameToWatch);
        if (target == null || (account = Database.getInstance().getAccount(target.getUniqueId())) == null) {
            watcher.sendMessage(Language.command$watch$user_not_found);
            return;
        }

        Arena server = account.getArena();
        if (server == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(target)) {
            watcher.sendMessage(Language.command$watch$user_not_in_match);
            return;
        }

        Account watcherAccount = Database.getInstance().getAccount(watcher.getUniqueId());
        if (watcherAccount == null) return;

        watcher.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
        server.spectate(watcherAccount, target);
    }

    public static LostPlayer getLostPlayer(Player player) {
        if (player == null) {
            return null;
        }

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) {
            return null;
        }

        return new LostPlayer(account);
    }
}
