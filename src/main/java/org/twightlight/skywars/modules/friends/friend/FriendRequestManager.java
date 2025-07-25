package org.twightlight.skywars.modules.friends.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.modules.friends.Friends;

public class FriendRequestManager {
    private Map<UUID, FriendRequest> friendRequests;

    private Friends module;

    public FriendRequestManager(Friends module) {
        this.friendRequests = new HashMap<>();
        this.module = module;
        startExpiryCheckThread();
    }

    private void startExpiryCheckThread() {
        (new BukkitRunnable() {
            public void run() {
                for (UUID uuid : FriendRequestManager.this.friendRequests.keySet()) {
                    if (((FriendRequest)FriendRequestManager.this.friendRequests.get(uuid)).isExpired()) {
                        if (((FriendRequest)FriendRequestManager.this.friendRequests.get(uuid)).getSender().isOnline()) {
                            Player user = (Player)((FriendRequest)FriendRequestManager.this.friendRequests.get(uuid)).getSender();
                            for (String line : FriendRequestManager.this.module.getConfig().getStringList("messages.request-expired-sender"))
                                FriendRequestManager.this.module.getMessageUtil().message((CommandSender)user, line.replace("%0%", ((FriendRequest)FriendRequestManager.this.friendRequests.get(uuid)).getRequested().getName()));
                        }
                        if (((FriendRequest)FriendRequestManager.this.friendRequests.get(uuid)).getRequested().isOnline()) {
                            Player user = (Player)((FriendRequest)FriendRequestManager.this.friendRequests.get(uuid)).getRequested();
                            for (String line : FriendRequestManager.this.module.getConfig().getStringList("messages.request-expired-receiver"))
                                FriendRequestManager.this.module.getMessageUtil().message((CommandSender)user, line.replace("%0%", ((FriendRequest)FriendRequestManager.this.friendRequests.get(uuid)).getSender().getName()));
                        }
                        FriendRequestManager.this.removeRequest(uuid, (FriendRequest)FriendRequestManager.this.friendRequests.get(uuid));
                    }
                }
            }
        }).runTaskTimerAsynchronously((Plugin)this.module.getPlugin(), 0L, 20L);
    }

    public void addRequest(FriendRequest request) {
        this.friendRequests.put(request.getRequested().getUniqueId(), request);
    }

    void removeRequest(UUID uuid, FriendRequest request) {
        this.friendRequests.remove(uuid, request);
    }

    public List<FriendRequest> getFriendRequests(UUID uuid) {
        List<FriendRequest> requests = new ArrayList<>();
        if (this.friendRequests.isEmpty())
            return requests;
        for (UUID uuid1 : this.friendRequests.keySet()) {
            if (uuid1.equals(uuid))
                requests.add(this.friendRequests.get(uuid1));
        }
        return requests;
    }
}
