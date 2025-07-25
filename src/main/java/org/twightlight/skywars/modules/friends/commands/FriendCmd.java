package org.twightlight.skywars.modules.friends.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.commands.subcommands.*;


public class FriendCmd extends Command {
    public FriendCmd(Friends module) {
        super(module);
    }

    public void registerSubCommands() {
        addSubCommand("accept", (SubCommand)new AcceptCmd(getModule(), new Permission("friends.accept")));
        addSubCommand("add", (SubCommand)new AddCmd(getModule(), new Permission("friends.add")));
        addSubCommand("deny", (SubCommand)new DenyCmd(getModule(), new Permission("friends.deny")));
        addSubCommand("help", (SubCommand)new HelpCmd(getModule(), new Permission("friends.help")));
        addSubCommand("list", (SubCommand)new ListCmd(getModule(), new Permission("friends.list")));
        addSubCommand("removeall", (SubCommand)new RemoveAllCmd(getModule(), new Permission("friends.removeall")));
        addSubCommand("remove", (SubCommand)new RemoveCmd(getModule(), new Permission("friends.remove")));
        addSubCommand("requests", (SubCommand)new RequestsCmd(getModule(), new Permission("friends.requests")));
        addSubCommand("toggle", (SubCommand)new ToggleCmd(getModule(), new Permission("friends.toggle")));
    }

    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player))
            return;
        Player user = (Player)sender;
        if (args.length == 0) {
            executeSubCommand("help");
            return;
        }
        executeSubCommand(args[0]);
    }
}
