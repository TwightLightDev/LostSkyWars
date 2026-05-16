package org.twightlight.skywars.modules.quests.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.quests.User;
import org.twightlight.skywars.modules.quests.commands.subcommands.Menu;
import org.twightlight.skywars.modules.quests.commands.subcommands.Reload;
import org.twightlight.skywars.player.Account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestsCommand extends Command {
    List<SubCommand> subCommands = new ArrayList<>();

    public QuestsCommand() {
        super("quests");
        this.setAliases(Arrays.asList("q", "quest"));
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }
        registerSubCommand(new Menu(null));
        registerSubCommand(new Reload(new Permission("quests.admin")));

    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = User.getUser(player);
            if (args.length == 0) {
                return true;
            }
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null) return true;
            if (account.getArena() != null) return true;
            SubCommand subCommand = subCommands.stream().filter(sc -> sc.getSubCommand().equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if (subCommand == null) {
                user.sendMessage(Quests.getInstance().getLangConfig().getString("messages.commands.command-not-found"));
                return true;
            }
            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(args));
            list.remove(0);

            if (subCommand.getPermission() == null || player.hasPermission(subCommand.getPermission().getName()) || subCommand.getPermission() == null) {
                subCommand.execute(Bukkit.getPlayer(user.getPlayer().getUniqueId()), list.toArray(new String[list.size()]));
            } else {
                user.sendMessage(Quests.getInstance().getLangConfig().getString("messages.commands.no-permission"));
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
