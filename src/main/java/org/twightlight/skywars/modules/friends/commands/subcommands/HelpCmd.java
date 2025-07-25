package org.twightlight.skywars.modules.friends.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;

public class HelpCmd extends SubCommand {
    public HelpCmd(Friends module, Permission permission) {
        super(module, permission);
    }

    public void execute(CommandSender user, String... args) {
        if (!validatePermissions(user))
            return;
        getModule().getMessageUtil().message(user, getModule().getConfig().getStringList("messages.help"));
    }
}