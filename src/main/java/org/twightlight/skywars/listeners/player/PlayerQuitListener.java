package org.twightlight.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.twightlight.skywars.cmd.sw.BuildCommand;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.rank.TagUtils;

public class PlayerQuitListener extends Listeners {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        Player player = evt.getPlayer();
        evt.setQuitMessage(null);

        TagUtils.reset(player.getName());
        Account account = Database.getInstance().unloadAccount(player.getUniqueId());
        if (account != null) {
            if (account.getServer() != null) {
                account.getServer().disconnect(account, "-quit");
            }

            account.save();
            account.destroy();
        }

        BuildCommand.remove(player);
    }
}
