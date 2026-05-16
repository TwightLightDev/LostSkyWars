package org.twightlight.skywars.modules.lobbysettings.commands.subcommands;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;

public class Players extends SubCommand {
    public Players(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "players";
    }

    @Override
    public void sendUsage(ModulesUser user) {
        user.sendMessage(LobbySettings.getLanguage().getString("lobbysettings."+getSubCommand()+".usage"));
    }

    @Override
    public boolean execute(Player player, String[] args) {
        User user = User.getFromUUID(player.getUniqueId());

        if (args.length < 1) {
            sendUsage(user);
            return true;
        }
        if (args[0].equals("on")) {
            user.setPlayersVisibility(true, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.players.on"));

        } else if (args[0].equals("off")) {
            user.setPlayersVisibility(false, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.players.off"));

        } else {
            sendUsage(user);
            return true;
        }
        return true;
    }
}
