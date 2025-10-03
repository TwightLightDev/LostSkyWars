package org.twightlight.skywars.modules.lobbysettings.commands.subcommands;

import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;
import org.twightlight.skywars.modules.api.cmds.SubCommand;

public class Vanish extends SubCommand {
    public Vanish(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "vanish";
    }

    @Override
    public void sendUsage(User user) {
        user.sendMessage(LobbySettings.getLanguage().getString("lobbysettings."+getSubCommand()+".usage"));
    }

    @Override
    public boolean execute(User user, String[] args) {
        if (args.length < 1) {
            sendUsage(user);
            return true;
        }
        if (args[0].equals("on")) {
            user.setVanishState(true, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.vanish.on"));

        } else if (args[0].equals("off")) {
            user.setVanishState(false, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.vanish.off"));

        } else {
            sendUsage(user);
            return true;
        }
        return true;
    }
}
