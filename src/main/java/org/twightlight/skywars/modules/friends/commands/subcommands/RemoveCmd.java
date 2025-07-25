package org.twightlight.skywars.modules.friends.commands.subcommands;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.friend.Friend;
import org.twightlight.skywars.modules.friends.user.User;

public class RemoveCmd extends SubCommand {
    public RemoveCmd(Friends module, Permission permission) {
        super(module, permission);
    }

    public void execute(CommandSender user, String... args) {
        if (!validatePermissions(user))
            return;
        User hfUser = getModule().getUserManager().getUser((Player)user);
        if (args.length == 1) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.invalid-arguments").replace("%0%", "friend remove <name>"));
            return;
        }
        if (args[1].length() > 16 || args[1].replaceAll("[a-zA-Z0-9_]", "").length() > 0) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.invalid-input").replace("%0%", args[1]));
            return;
        }
        OfflinePlayer oldFriend;
        if ((oldFriend = Bukkit.getOfflinePlayer(args[1])) == null || !oldFriend.hasPlayedBefore()) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getString("messages.player-not-found").replace("%0%", args[1]));
            return;
        }
        for (Friend friend : hfUser.getFriendList().getAllFriends()) {
            if (!friend.getUniqueId().equals(oldFriend.getUniqueId()))
                continue;
            hfUser.getFriendList().removeFriend(friend);
            getModule().getUserManager().saveUser(((Player) user).getPlayer());
            if (oldFriend.isOnline()) {
                getModule().getUserManager().getUser((Player)oldFriend).getFriendList().removeFriend((OfflinePlayer)user);
                getModule().getUserManager().saveUser(oldFriend);
                for (String line : getModule().getConfig().getStringList("messages.unfriend-receiver"))
                    getModule().getMessageUtil().message((CommandSender)oldFriend, line.replace("%0%", ((Player)user).getDisplayName()));
            } else {
                FileConfiguration storage = getModule().getStorageUtil().getFile(oldFriend.getUniqueId().toString());
                List<String> friends = storage.getStringList("friends");
                friends.remove(((Player)user).getUniqueId().toString());
                storage.set("friends", friends);
                getModule().getStorageUtil().saveFile(storage, oldFriend.getUniqueId().toString());
            }
            for (String line : getModule().getConfig().getStringList("messages.unfriend-sender"))
                getModule().getMessageUtil().message(user, line.replace("%0%", oldFriend.isOnline() ? Bukkit.getPlayer(oldFriend.getUniqueId()).getDisplayName() : oldFriend.getName()));
            return;
        }
        for (String line : getModule().getConfig().getStringList("messages.not-friends"))
            getModule().getMessageUtil().message(user, line.replace("%0%", oldFriend.isOnline() ? Bukkit.getPlayer(oldFriend.getUniqueId()).getDisplayName() : oldFriend.getName()));
    }
}