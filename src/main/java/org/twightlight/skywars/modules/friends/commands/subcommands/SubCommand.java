package org.twightlight.skywars.modules.friends.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;

public abstract class SubCommand implements ISubCommand {
    private Friends module;

    private Permission permission;

    public SubCommand(Friends plugin, Permission permission) {
        this.permission = permission;
        this.module = plugin;
    }

    boolean validatePermissions(CommandSender user) {
        if (!user.hasPermission(getPermission())) {
            this.module.getMessageUtil().message(user, this.module.getConfig().getString("messages.no-permission"));
            return false;
        }
        return true;
    }

    private Permission getPermission() {
        return this.permission;
    }

    Friends getModule() {
        return this.module;
    }
}
