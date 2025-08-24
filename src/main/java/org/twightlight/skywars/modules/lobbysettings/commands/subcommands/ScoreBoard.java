package org.twightlight.skywars.modules.lobbysettings.commands.subcommands;

import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;
import org.twightlight.skywars.modules.libs.cmds.SubCommand;

public class ScoreBoard extends SubCommand {
    public ScoreBoard(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "scoreboard";
    }

    @Override
    public void sendUsage(User user) {

    }

    @Override
    public boolean execute(User user, String[] args) {
        if (args.length < 1) {
            sendUsage(user);
            return true;
        }
        if (args[0].equals("on")) {
            user.setScoreboardVisibility(true, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.scoreboard.on"));

        } else if (args[0].equals("off")) {
            user.setScoreboardVisibility(false, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.scoreboard.off"));

        } else {
            sendUsage(user);
            return true;
        }
        return true;
    }
}
