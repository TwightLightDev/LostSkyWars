package org.twightlight.skywars.modules.friends.commands.subcommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.user.User;

public class ToggleCmd extends SubCommand {
    public ToggleCmd(Friends module, Permission permission) {
        super(module, permission);
    }

    public void execute(CommandSender user, String... args) {
        if (!validatePermissions(user))
            return;
        User hfUser = getModule().getUserManager().getUser((Player)user);
        hfUser.toggleRequests();
        getModule().getUserManager().saveUser((OfflinePlayer)user);
        for (String line : getModule().getConfig().getStringList("messages.toggle"))
            getModule().getMessageUtil().message(user, line.replace("%0%", hfUser.isAllowingRequests() ? "enabled" : "disabled"));
    }
}
