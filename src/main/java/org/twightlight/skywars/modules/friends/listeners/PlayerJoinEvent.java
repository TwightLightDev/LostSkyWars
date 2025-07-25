package org.twightlight.skywars.modules.friends.listeners;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.modules.friends.Friends;

public class PlayerJoinEvent implements Listener {
    private Friends module;

    public PlayerJoinEvent(Friends module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player user = e.getPlayer();
        this.module.getUserManager().cacheUser(user);
        if (!this.module.getFriendRequestManager().getFriendRequests(user.getUniqueId()).isEmpty())
            for (String line : this.module.getConfig().getStringList("messages.pending-requests"))
                this.module.getMessageUtil().message((CommandSender)user, line.replace("%0%", String.valueOf(this.module.getFriendRequestManager().getFriendRequests(user.getUniqueId()).size())));
        if (!this.module.getPlugin().getServer().getOnlinePlayers().isEmpty())
            for (Player onlineUser : this.module.getPlugin().getServer().getOnlinePlayers()) {
                if (this.module.getUserManager().getUser(onlineUser).getFriendList().contains(user))
                    this.module.getMessageUtil().message((CommandSender)onlineUser, this.module.getConfig().getString("messages.join").replace("%0%", user.getName()));
            }
    }
}
