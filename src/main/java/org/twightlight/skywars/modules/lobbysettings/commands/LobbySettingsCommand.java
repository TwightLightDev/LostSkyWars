package org.twightlight.skywars.modules.lobbysettings.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.libs.cmds.SubCommand;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.lobbysettings.User;
import org.twightlight.skywars.modules.lobbysettings.commands.subcommands.*;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.Logger.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbySettingsCommand extends Command {
    List<SubCommand> subCommands = new ArrayList<>();
    public LobbySettingsCommand() {
        super("lobbysettings");
        this.setAliases(Arrays.asList("settings"));
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }

        registerSubCommand(new ScoreBoard(new Permission("lobbysettings.scoreboard.toggle")));

        registerSubCommand(new Fly(new Permission("lobbysettings.fly.toggle")));

        registerSubCommand(new Speed(new Permission("lobbysettings.speed.toggle")));

        registerSubCommand(new JumpBoost(new Permission("lobbysettings.jumpboost.toggle")));

        registerSubCommand(new Vanish(new Permission("lobbysettings.vanish.toggle")));

        registerSubCommand(new Particles(new Permission("lobbysettings.particles.toggle")));

    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = User.getFromUUID(player.getUniqueId());
            if (args.length == 0) {
                return true;
            }
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null) return true;
            if (account.getServer() != null) return true;
            SubCommand subCommand = subCommands.stream().filter(sc -> sc.getSubCommand().equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if (subCommand == null) {
                user.sendMessage(LobbySettings.getLanguage().getString("lobbysettings.general.command-not-found"));
                return true;
            }
            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(args));
            list.remove(0);

            if (player.hasPermission(subCommand.getPermission().getName())) {
                subCommand.execute(user, list.toArray(new String[list.size()]));
            } else {
                user.sendMessage(LobbySettings.getLanguage().getString("lobbysettings.general.no-permission"));
            }

            return true;
        }
        return true;
    }

    public void registerSubCommand(SubCommand sc) {
        subCommands.add(sc);
    }
}
