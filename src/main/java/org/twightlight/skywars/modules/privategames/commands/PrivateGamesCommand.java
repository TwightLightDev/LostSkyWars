package org.twightlight.skywars.modules.privategames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;

import org.twightlight.skywars.modules.privategames.PrivateGames;
import org.twightlight.skywars.modules.privategames.PrivateGamesUser;
import org.twightlight.skywars.modules.privategames.menus.MainMenu;
import org.twightlight.skywars.utils.Logger.Level;

import java.util.Arrays;


public class PrivateGamesCommand extends Command {

    public PrivateGamesCommand() {
        super("p");
        this.setAliases(Arrays.asList("privategames"));
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
            PrivateGamesUser user = PrivateGames.getStorage().getUser(player);
            if (args.length == 0) {
                user.togglePrivateGame();
                return true;
            }

            if (args[0].equals("toggle")) {
                user.togglePrivateGame();
            } else if (args[0].equals("settings")) {
                MainMenu.open(PrivateGames.getStorage().getUser(player));
            }
            return true;
        }
        return true;
    }


}
