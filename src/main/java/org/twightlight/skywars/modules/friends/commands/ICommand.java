package org.twightlight.skywars.modules.friends.commands;

import org.bukkit.command.CommandSender;

public interface ICommand {
    void registerSubCommands();

    void execute(CommandSender paramCommandSender, String... paramVarArgs);
}
