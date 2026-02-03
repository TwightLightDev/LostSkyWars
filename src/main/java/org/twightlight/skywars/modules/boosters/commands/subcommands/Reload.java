package org.twightlight.skywars.modules.boosters.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.boosters.Boosters;

public class Reload extends SubCommand {
    public Reload(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "reload";
    }

    @Override
    public void sendUsage(ModulesUser user) {
        user.sendMessage(Boosters.getLanguage().getString("messages.commands.usages."+getSubCommand()));
    }

    @Override
    public boolean execute(Player user1, String[] args) {
        Boosters.getMenuConfig().reload();
        Boosters.getBoostersConfig().reload();
        Boosters.getLanguage().reload();
        Boosters.getConfig().reload();
        user1.sendMessage(ChatColor.GREEN + "&aYou have successfully reloaded all configs!");
        return true;
    }
}
