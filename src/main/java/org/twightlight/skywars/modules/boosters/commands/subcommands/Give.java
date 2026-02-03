package org.twightlight.skywars.modules.boosters.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.utils.StringCheckerUtils;

import java.util.stream.Collectors;

public class Give extends SubCommand {
    public Give(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "give";
    }

    @Override
    public void sendUsage(ModulesUser user) {
        user.sendMessage(Boosters.getLanguage().getString("messages.commands.usages."+getSubCommand()));

    }

    @Override
    public boolean execute(Player user1, String[] args) {
        if (args.length <= 1 || Bukkit.getPlayerExact(args[0]) == null || PlayerUser.getFromUUID(Bukkit.getPlayerExact(args[0]).getUniqueId()) == null) {
            user1.sendMessage(ChatColor.RED + "Player not found!!");
            sendUsage(PlayerUser.getFromUUID(user1.getUniqueId()));
            return true;
        }

        Booster booster = BoosterManager.getBoosters().get(args[1]);
        int amount;

        if (args.length == 3 && StringCheckerUtils.isInteger(args[2])) {
            amount = Integer.parseInt(args[2]);
        } else {
            amount = 1;
        }

        if (booster == null) {
            user1.sendMessage(ChatColor.RED + "Booster not found with id "+ args[1]+"!!");

            sendUsage(PlayerUser.getFromUUID(user1.getUniqueId()));
            return true;
        }

        for (int i = 0; i < amount; i ++) {
            BoosterManager.give(args[0], booster);
        }
        PlayerUser.getFromUUID(user1.getUniqueId()).sendMessage(Boosters.getLanguage().getList("messages.boosters.give").stream().map((line) -> {
            return line.replace("{amount}", args[2]).replace("{booster}", args[1]).replace("{player}", args[0]);
        }).collect(Collectors.toList()));


        return true;
    }
}
