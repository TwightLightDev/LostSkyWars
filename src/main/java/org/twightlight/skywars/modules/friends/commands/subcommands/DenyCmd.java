package org.twightlight.skywars.modules.friends.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.friend.Friend;
import org.twightlight.skywars.modules.friends.friend.FriendRequest;

public class DenyCmd extends SubCommand {
    public DenyCmd(Friends module, Permission permission) {
        super(module, permission);
    }

    public void execute(CommandSender user, String... args) {
        if (!validatePermissions(user))
            return;
        if (args.length == 1) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.invalid-arguments").replace("%0%", "friend deny <name>"));
            return;
        }
        if (args[1].length() > 16 || args[1].replaceAll("[a-zA-Z0-9_]", "").length() > 0) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.invalid-input").replace("%0%", args[1]));
            return;
        }
        OfflinePlayer potentialFriend;
        if ((potentialFriend = getModule().getPlugin().getServer().getOfflinePlayer(args[1])) == null || !potentialFriend.hasPlayedBefore()) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.player-not-found").replace("%0%", args[1]));
            return;
        }
        for (Friend friend : getModule().getUserManager().getUser((Player)user).getFriendList().getAllFriends()) {
            if (friend.getUniqueId().equals(potentialFriend.getUniqueId())) {
                getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.already-friends"));
                return;
            }
        }
        if (getModule().getFriendRequestManager().getFriendRequests(((Player)user).getUniqueId()).isEmpty()) {
            for (String line : getModule().getConfig().getStringList("messages.no-request"))
                getModule().getMessageUtil().message(user, line.replace("%0%", Bukkit.getOfflinePlayer(args[1]).isOnline() ? Bukkit.getPlayer(args[1]).getDisplayName() : Bukkit.getOfflinePlayer(args[1]).getName()));
            return;
        }
        FriendRequest request = null;
        for (FriendRequest request1 : getModule().getFriendRequestManager().getFriendRequests(((Player)user).getUniqueId())) {
            if (request1.getSender().equals(potentialFriend))
                request = request1;
        }
        if (request == null) {
            for (String line : getModule().getConfig().getStringList("messages.no-request"))
                getModule().getMessageUtil().message(user, line.replace("%0%", potentialFriend.isOnline() ? Bukkit.getPlayer(args[1]).getDisplayName() : potentialFriend.getName()));
            return;
        }
        request.deny(getModule());
        for (String line : getModule().getConfig().getStringList("messages.request-denied"))
            getModule().getMessageUtil().message(user, line.replace("%0%", potentialFriend.isOnline() ? Bukkit.getPlayer(args[1]).getDisplayName() : potentialFriend.getName()));
    }
}
