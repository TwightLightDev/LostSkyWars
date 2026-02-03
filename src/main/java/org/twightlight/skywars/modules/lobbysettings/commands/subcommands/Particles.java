package org.twightlight.skywars.modules.lobbysettings.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;

public class Particles extends SubCommand {
    public Particles(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "particles";
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
            user.setParticlesVisibility(true, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.particles.on"));

        } else if (args[0].equals("off")) {
            user.setParticlesVisibility(false, true);
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.particles.off"));

        } else {
            sendUsage(user);
            return true;
        }
        return true;
    }
}
