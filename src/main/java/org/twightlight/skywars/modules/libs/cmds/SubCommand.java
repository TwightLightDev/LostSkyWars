package org.twightlight.skywars.modules.libs.cmds;

import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.lobbysettings.User;

public abstract class SubCommand {
    private final Permission permission;

    public SubCommand(Permission permission) {
        this.permission = permission;
    }

    public abstract String getSubCommand();
    public abstract boolean execute(User user, String[] args);
    public abstract void sendUsage(User user);
    public Permission getPermission() {
        return permission;
    }
}
