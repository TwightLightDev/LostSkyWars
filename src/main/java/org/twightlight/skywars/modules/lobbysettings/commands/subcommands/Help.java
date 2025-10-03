package org.twightlight.skywars.modules.lobbysettings.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;

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
    public void sendUsage(User user) {

    }

    @Override
    public boolean execute(User user, String[] args) {
        List<String> list = LobbySettings.getLanguage().getList("lobbysettings.help");
        for (String help : list) {
            if (help.contains("{cmds}")) {
                for (SubCommand sc : LobbySettings.getCommandManager().getSubCommands()) {
                    if (Bukkit.getPlayer(user.getUUID()).hasPermission(sc.getPermission()) || sc.getPermission() == null) {
                        sc.sendUsage(user);
                    }
                }
            }
            user.sendMessage(help);
        }
        return true;
    }
}
