package org.twightlight.skywars.integration.guilds.commands;

import me.leoo.guilds.bukkit.manager.GuildsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.integration.guilds.GuildsIntegration;
import org.twightlight.skywars.integration.guilds.donation.Donator;
import org.twightlight.skywars.integration.guilds.menus.DonationMenu;
import org.twightlight.skywars.utils.string.StringCheckerUtils;

import java.util.Arrays;


public class GuildsDonationCommand extends Command {

    public GuildsDonationCommand() {
        super("guilddonation");
        setAliases(Arrays.asList("guildsdonation", "gdonation", "clansdonation", "clandonation", "cdonation"));
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

            Donator user = Donator.getFromUUID(player.getUniqueId());
            if (args.length == 0) {
                GuildsIntegration.getLanguage().getList("guilds.donation.help.general").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
                return true;
            }

            if (args[0].equals("donate")) {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Lack of args!");
                    return true;
                }
                if (GuildsManager.getByPlayer(player.getUniqueId()) == null) {
                    GuildsIntegration.getLanguage().getList("guilds.donation.not-in-guild").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
                    return true;
                }
                if (StringCheckerUtils.isInteger(args[1])) {
                    int i = Integer.parseInt(args[1]);
                    if (i >= 100) {
                        if (user.getAccount().getCoins() >= i) {
                            user.donate(i);
                            return true;
                        } else {
                            GuildsIntegration.getLanguage().getList("guilds.donation.lack-of-money").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
                            return true;
                        }
                    } else {
                        GuildsIntegration.getLanguage().getList("guilds.donation.minimum").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid arguments");
                    return true;
                }
            } else if (args[0].equals("help")) {
                GuildsIntegration.getLanguage().getList("guilds.donation.help.general").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
                return true;
            } else if (args[0].equals("menu")) {
                DonationMenu.open(user);
                return true;
            }
            return true;
        }
        return true;
    }

}
