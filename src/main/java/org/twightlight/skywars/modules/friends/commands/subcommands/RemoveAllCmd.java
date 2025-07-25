package org.twightlight.skywars.modules.friends.commands.subcommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.user.User;

public class RemoveAllCmd extends SubCommand {
    public RemoveAllCmd(Friends module, Permission permission) {
        super(module, permission);
    }

    public void execute(CommandSender user, String... args) {
        if (!validatePermissions(user))
            return;
        User hfUser = getModule().getUserManager().getUser((Player)user);
        if (hfUser.getFriendList().getFriendsListSize() <= 0) {
            getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.friends-list-already-empty"));
            return;
        }
        hfUser.getFriendList().clear();
        getModule().getUserManager().saveUser((OfflinePlayer)user);
        getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.friends-list-cleared"));
    }
}
