package org.twightlight.skywars.modules.lobbysettings.commands.subcommands;

import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;

public class Chat extends SubCommand {
    public Chat(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "chat";
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
            user.setChatVisibility(true, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.chat.on"));

        } else if (args[0].equals("off")) {
            user.setChatVisibility(false, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.chat.off"));

        } else {
            sendUsage(user);
            return true;
        }
        return true;
    }
}
