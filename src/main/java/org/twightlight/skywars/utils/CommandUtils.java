package org.twightlight.skywars.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

public class CommandUtils {

    public static void unregisterAllCommands(JavaPlugin plugin) {
        try {
            // Get the CommandMap
            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            CommandMap commandMap = craftServer.getCommandMap();

            // Access the knownCommands map
            Field field = commandMap.getClass().getDeclaredField("knownCommands");
            field.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) field.get(commandMap);

            // Iterate and remove plugin commands
            Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Command> entry = it.next();
                Command cmd = entry.getValue();
                if (cmd instanceof PluginCommand) {
                    PluginCommand pluginCmd = (PluginCommand) cmd;
                    if (pluginCmd.getPlugin() == plugin) {
                        pluginCmd.unregister(commandMap);
                        it.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
