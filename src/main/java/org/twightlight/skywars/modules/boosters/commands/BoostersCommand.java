package org.twightlight.skywars.modules.boosters.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.lobbysettings.User;
import org.twightlight.skywars.modules.lobbysettings.commands.subcommands.Help;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.Logger.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoostersCommand extends Command {
    List<SubCommand> subCommands = new ArrayList<>();
    public BoostersCommand() {
        super("boosters");
        this.setAliases(Arrays.asList("b"));
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }
        registerSubCommand(new Help(null));
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
                user.sendMessage(Boosters.getLanguage().getString("messages.commands.command-not-found"));
                return true;
            }
            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(args));
            list.remove(0);

            if (player.hasPermission(subCommand.getPermission().getName()) || subCommand.getPermission() == null) {
                subCommand.execute(user, list.toArray(new String[list.size()]));
            } else {
                user.sendMessage(Boosters.getLanguage().getString("messages.commands.no-permission"));
            }

            return true;
        }
        return true;
    }

    public void registerSubCommand(SubCommand sc) {
        subCommands.add(sc);
    }
}
