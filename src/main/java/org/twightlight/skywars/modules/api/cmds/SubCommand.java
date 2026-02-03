package org.twightlight.skywars.modules.api.cmds;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;

public abstract class SubCommand {
    private final Permission permission;

    public SubCommand(Permission permission) {
        this.permission = permission;
    }

    public abstract String getSubCommand();
    public abstract boolean execute(Player user, String[] args);
    public abstract void sendUsage(ModulesUser user);
    public Permission getPermission() {
        return permission;
    }
}
