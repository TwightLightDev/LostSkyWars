package org.twightlight.skywars.modules.quests.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.quests.Quests;

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
    }

    @Override
    public boolean execute(Player user1, String[] args) {
        Quests.getInstance().getMenuConfig().reload();
        Quests.getInstance().getLangConfig().reload();
        Quests.getInstance().getMainConfig().reload();
        Quests.getInstance().getQuestsConfig().reload();
        user1.sendMessage(ChatColor.GREEN + "&aYou have successfully reloaded all configs!");
        return true;
    }
}
