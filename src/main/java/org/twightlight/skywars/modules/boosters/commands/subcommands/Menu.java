package org.twightlight.skywars.modules.boosters.commands.subcommands;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.menus.MainMenu;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;


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
        user.sendMessage(Boosters.getLanguage().getString("messages.commands.usages."+getSubCommand()));

    }

    @Override
    public boolean execute(Player user, String[] args) {
        MainMenu.open(PlayerUser.getFromUUID(user.getUniqueId()));
        return true;
    }
}
