package org.twightlight.skywars.modules.boosters.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.lobbysettings.papi.PlaceholderAPI;

import java.util.List;

public class Help extends SubCommand {
    public Help(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "help";
    }

    @Override
    public void sendUsage(ModulesUser user) {

    }
    @Override
    public boolean execute(Player user, String[] args) {
        List<String> list = Boosters.getLanguage().getList("messages.commands.help");
        for (String help : list) {
            if (help.contains("{cmds}")) {
                help = help.replace("{cmds}", "");

                for (SubCommand sc : Boosters.getCommandManager().getSubCommands()) {
                    if (sc.getPermission() == null || user.hasPermission(sc.getPermission()) || sc.getPermission() == null) {
                        sc.sendUsage(PlayerUser.getFromUUID(user.getUniqueId()));
                    }
                }
            }
            PlayerUser.getFromUUID(user.getUniqueId()).sendMessage(help);
        }
        return true;
    }
}
