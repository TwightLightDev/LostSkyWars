package org.twightlight.skywars.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.menu.play.PlayDuelsMenu;
import org.twightlight.skywars.menu.play.PlayMenu;
import org.twightlight.skywars.menu.play.PlayRankedMenu;
import org.twightlight.skywars.ui.SkyWarsMode;
import org.twightlight.skywars.utils.Logger.Level;

public class PlayCommand extends Command {

    public PlayCommand() {
        super("play");

        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            Main.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(" \n§dPlay - Help\n \n§6/play <solo/doubles/ranked/duels> §f- §7Open play menu.\n ");
                return true;
            }

            if (args[0].equalsIgnoreCase("solo")) {
                new PlayMenu(player, SkyWarsMode.SOLO);
            } else if (args[0].equalsIgnoreCase("doubles")) {
                new PlayMenu(player, SkyWarsMode.DOUBLES);
            } else if (args[0].equalsIgnoreCase("ranked")) {
                new PlayRankedMenu(player);
            } else if (args[0].equalsIgnoreCase("duels")) {
                new PlayDuelsMenu(player);
            } else {
                player.sendMessage(" \n§dPlay - Help\n \n§6/play <solo/doubles/ranked/duels> §f- §7Open play menu.\n ");
            }
        }

        return true;
    }
}
