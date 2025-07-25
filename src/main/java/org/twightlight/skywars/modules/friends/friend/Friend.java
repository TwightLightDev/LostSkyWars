package org.twightlight.skywars.modules.friends.friend;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.player.Account;

public class Friend {
    private UUID uuid;

    public Friend(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return isOnline() ? Bukkit.getPlayer(this.uuid).getDisplayName() : Bukkit.getOfflinePlayer(this.uuid).getName();
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public boolean isOnline() {
        return Bukkit.getOfflinePlayer(this.uuid).isOnline();
    }

    public String getStatus() {
        Account account = Database.getInstance().getAccount(uuid);
        if (account.getServer() == null) {
            return Friends.getInstance().getConfig().getString("messages.online-status.idling");
        } else {
            return Friends.getInstance().getConfig().getString("messages.online-status.playing").replace("%0%", account.getServer().getName());
        }
    }
}
