package org.twightlight.skywars.fun.customitems.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.fun.customitems.CustomItemsManager;
import org.twightlight.skywars.fun.customitems.FunItem;


public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("funitems.admin")) {
                sender.sendMessage("§fUnknown command. Type \"/help\" for help.");
            }
            if (args.length == 0) {
                player.sendMessage("§cCommand not found or you don't have permission!");
                return true;
            }

            if (args[0].equals("give")) {
                if (args.length < 3) {
                    player.sendMessage("§cCommand not found or you don't have permission!");
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    FunItem item = CustomItemsManager.getItem(args[2]);
                    item.give(target);
                }
            } else if (args[0].equals("get")) {
                if (args.length < 2) {
                    player.sendMessage("§cCommand not found or you don't have permission!");
                } else {
                    FunItem item = CustomItemsManager.getItem(args[1]);
                    item.give(player);
                }
            } else if (args[0].equals("list")) {
                player.sendMessage("§6-----------List of Items-----------");
                for (String i : CustomItemsManager.getItems()) {
                    player.sendMessage(" §f- " + i);
                }
                player.sendMessage("§6-----------------------------------");

            }
            return true;
        }
        return true;
    }
}
