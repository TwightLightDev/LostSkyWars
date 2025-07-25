package org.twightlight.skywars.modules.friends.friend;

import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.friends.Friends;

public class FriendRequest {
    private int expireTime;

    private UUID requested;

    private Long sent;

    private UUID uuid;

    public FriendRequest(UUID uuid, UUID requested, int expireTime) {
        this.sent = Long.valueOf(System.currentTimeMillis());
        this.expireTime = expireTime;
        this.requested = requested;
        this.uuid = uuid;
    }

    public void accept(Friends module) {
        module.getFriendRequestManager().removeRequest(getRequested().getUniqueId(), this);
        if (module.getStorageUtil().getFile(getSender().getUniqueId().toString()).getBoolean("friends-list-full")) {
            module.getMessageUtil().message((CommandSender)getRequested(), module.getConfig().getStringList("messages.request-not-allowed"));
            if (getSender().isOnline())
                for (String line : module.getConfig().getStringList("messages.max-friends"))
                    module.getMessageUtil().message((CommandSender)getSender(), line.replace("%0%", String.valueOf(module.getUserManager().getUser((Player)getSender()).getFriendLimit())));
            return;
        }
        module.getUserManager().getUser((Player)getRequested()).getFriendList().addFriend(new Friend(this.uuid));
        module.getUserManager().saveUser(getRequested());
        if (getSender().isOnline()) {
            Player user = (Player)getSender();
            for (String line : module.getConfig().getStringList("messages.request-accepted"))
                module.getMessageUtil().message((CommandSender)user, line.replace("%0%", ((Player)getRequested()).getDisplayName()));
            module.getUserManager().getUser(user).getFriendList().addFriend(new Friend(this.requested));
            module.getUserManager().saveUser((OfflinePlayer)user);
            return;
        }
        FileConfiguration storage = module.getStorageUtil().getFile(this.uuid.toString());
        List<String> friends = storage.getStringList("friends");
        friends.add(this.requested.toString());
        storage.set("friends", friends);
        module.getStorageUtil().saveFile(storage, this.uuid.toString());
    }

    public void deny(Friends plugin) {
        plugin.getFriendRequestManager().removeRequest(getRequested().getUniqueId(), this);
    }

    public OfflinePlayer getSender() {
        return Bukkit.getOfflinePlayer(this.uuid);
    }

    OfflinePlayer getRequested() {
        return Bukkit.getOfflinePlayer(this.requested);
    }

    public int getRemainingTime() {
        return (int)((this.expireTime * 60) - System.currentTimeMillis() / 1000L - this.sent.longValue() / 1000L) / 60;
    }

    boolean isExpired() {
        return (System.currentTimeMillis() / 1000L - this.sent.longValue() / 1000L >= (this.expireTime * 60));
    }
}
