package org.twightlight.skywars.modules.quests.commands.subcommands;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.quests.User;
import org.twightlight.skywars.modules.quests.menus.QuestsMasterMenu;


public class Menu extends SubCommand {
    public Menu(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "menu";
    }

    @Override
    public void sendUsage(ModulesUser user) {

    }

    @Override
    public boolean execute(Player user, String[] args) {
        QuestsMasterMenu.open(User.getUser(user));
        return true;
    }
}
