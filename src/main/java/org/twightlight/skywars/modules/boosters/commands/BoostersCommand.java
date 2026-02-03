package org.twightlight.skywars.modules.boosters.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.commands.subcommands.Give;
import org.twightlight.skywars.modules.boosters.commands.subcommands.Help;
import org.twightlight.skywars.modules.boosters.commands.subcommands.Menu;
import org.twightlight.skywars.modules.boosters.commands.subcommands.Reload;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.player.Account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoostersCommand extends Command {
    List<SubCommand> subCommands = new ArrayList<>();

    Help help;

    public BoostersCommand() {
        super("boosters");
        this.setAliases(Arrays.asList("b"));
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }
        help = new Help(null);
        registerSubCommand(help);
        registerSubCommand(new Menu(null));
        registerSubCommand(new org.twightlight.skywars.modules.boosters.commands.subcommands.List(new Permission("boosters.admin.list")));
        registerSubCommand(new Give(new Permission("boosters.admin.give")));
        registerSubCommand(new Reload(new Permission("boosters.admin.reload")));

    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerUser user = PlayerUser.getFromUUID(player.getUniqueId());
            if (args.length == 0) {
                help.execute(player, args);
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

            if (subCommand.getPermission() == null || player.hasPermission(subCommand.getPermission().getName()) || subCommand.getPermission() == null) {
                subCommand.execute(Bukkit.getPlayer(user.getUUID()), list.toArray(new String[list.size()]));
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

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

}
