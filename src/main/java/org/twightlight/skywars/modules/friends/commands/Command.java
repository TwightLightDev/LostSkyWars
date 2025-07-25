package org.twightlight.skywars.modules.friends.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.friends.commands.subcommands.SubCommand;

import java.util.HashMap;
import java.util.Map;

abstract class Command implements CommandExecutor, ICommand {
    private Map<String, SubCommand> subCommands;

    private Friends module;

    private CommandSender user;

    private String[] args;

    Command(Friends module) {
        this.subCommands = new HashMap<>();
        this.module = module;
        registerSubCommands();
    }

    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        this.user = commandSender;
        this.args = strings;
        execute(this.user, this.args);
        return true;
    }

    void addSubCommand(String name, SubCommand subCommand) {
        this.subCommands.put(name, subCommand);
    }

    void executeSubCommand(String name) {
        if (!getSubCommands().containsKey(name)) {
            ((SubCommand)getSubCommands().get("add")).execute(this.user, this.args);
            return;
        }
        ((SubCommand)getSubCommands().get(name)).execute(this.user, this.args);
    }

    private Map<String, SubCommand> getSubCommands() {
        return this.subCommands;
    }

    Friends getModule() {
        return this.module;
    }
}
