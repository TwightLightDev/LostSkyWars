package org.twightlight.skywars.hook.guilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.hook.GuildsHook;
import org.twightlight.skywars.hook.guilds.donation.Donator;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.utils.StringCheckerUtils;

import java.util.Arrays;


public class GuildCoinsCommand extends Command {

    public GuildCoinsCommand() {
        super("guildcoins");
        setAliases(Arrays.asList("guildscoins", "guildcoin", "guildscoin", "gcoin", "gcoins", "clanscoins", "clancoins", "ccoins", "clanscoin", "clancoin", "ccoin"));
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("guilddonation.admin")) {
                sender.sendMessage("§fUnknown command. Type \"/help\" for help.");
                return true;
            }
            if (args.length == 0) {
                GuildsHook.getLanguage().getList("guilds.donation.help.coins").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
                return true;
            }

            if (args[0].equals("give")) {

                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Lack of args!");
                    return true;
                }
                if (!StringCheckerUtils.isInteger(args[2]) || Bukkit.getPlayer(args[1]) == null) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                } else {
                    Donator.getFromUUID(Bukkit.getPlayer(args[1]).getUniqueId()).giveGuildCoins(Integer.parseInt(args[2]));
                }
                return true;

            } else if (args[0].equals("set")) {
                if (args.length < 3) {
                    player.sendMessage( ChatColor.RED + "Lack of args!");
                    return true;
                }
                if (!StringCheckerUtils.isInteger(args[2]) || Bukkit.getPlayer(args[1]) == null) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                } else {
                    Donator.getFromUUID(Bukkit.getPlayer(args[1]).getUniqueId()).setGuildCoins(Integer.parseInt(args[2]));
                }
                return true;
            } else if (args[0].equals("take")) {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Lack of args!");
                    return true;
                }
                if (!StringCheckerUtils.isInteger(args[2]) || Bukkit.getPlayer(args[1]) == null) {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                } else {
                    Donator.getFromUUID(Bukkit.getPlayer(args[1]).getUniqueId()).takeGuildCoins(Integer.parseInt(args[2]));
                }
                return true;
            } else if (args[0].equals("reset")) {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Lack of args!");
                    return true;
                }
                if (Bukkit.getPlayer(args[1]) == null) {
                    player.sendMessage(ChatColor.RED + "Player not found or being offline!");
                    return true;
                } else {
                    Donator.getFromUUID(Bukkit.getPlayer(args[1]).getUniqueId()).resetGuildCoins();
                }
                return true;
            } else if (args[0].equals("bal")) {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Lack of args!");
                    return true;
                }
                if (Bukkit.getPlayer(args[1]) == null) {
                    player.sendMessage(ChatColor.RED + "Player not found or being offline!");
                    return true;
                } else {
                    GuildsHook.getLanguage().
                            getList("guilds.guild-coin.balance").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{amount}", String.valueOf(Donator.getFromUUID(Bukkit.getPlayer(args[1]).getUniqueId()).getGuildCoins()))));
                }
            }


            return true;
        }
        return true;
    }
}

