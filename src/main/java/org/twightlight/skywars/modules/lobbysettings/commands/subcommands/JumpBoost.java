package org.twightlight.skywars.modules.lobbysettings.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.utils.StringCheckerUtils;

public class JumpBoost extends SubCommand {
    public JumpBoost(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "jump";
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
        if (!StringCheckerUtils.isInteger(args[0])) {
            sendUsage(user);
            return true;
        }
        user.setJumpBoost(Integer.parseInt(args[0]), true);
        if (Integer.parseInt(args[0]) > 0 && Integer.parseInt(args[0]) < 6) {
            Player p = Bukkit.getPlayer(user.getUUID());
            LobbySettings.getLanguage().getList("lobbysettings.jumpboost.on").forEach(line -> {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{amplifier}", args[0]));
            });

        } else {
            user.sendMessage(LobbySettings.getLanguage().getList("lobbysettings.jumpboost.off"));

        }
        return true;
    }
}
