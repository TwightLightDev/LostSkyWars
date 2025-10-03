package org.twightlight.skywars.fun.customitems.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.twightlight.skywars.fun.customitems.CustomItemsManager;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AutoItemCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("get", "give", "list");

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return Collections.emptyList();

        String current = args[args.length - 1].toUpperCase();

        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(current.toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        String first = args[0].toLowerCase();
        if (first.equals("get")) {
            return CustomItemsManager.getItems().stream()
                    .filter(m -> m.startsWith(current.toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        } else if (first.equals("give")) {
            if (args.length == 2) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                        .filter(m -> m.startsWith(current.toLowerCase()))
                        .sorted()
                        .collect(Collectors.toList());
            }
            if (args.length == 3) {
                return CustomItemsManager.getItems().stream()
                        .filter(m -> m.startsWith(current.toLowerCase()))
                        .sorted()
                        .collect(Collectors.toList());
            }
        }


        return Collections.emptyList();
    }
}
