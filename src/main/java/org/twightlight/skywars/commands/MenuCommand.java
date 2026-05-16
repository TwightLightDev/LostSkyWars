package org.twightlight.skywars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.menu.lobby.DeliveryManMenu;
import org.twightlight.skywars.menu.lobby.StatsNPCMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.CosmeticsMenu;

public class MenuCommand extends Command {

    public MenuCommand() {
        super("swmenu");
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Logger.Level.SEVERE, "Could not register command: ", ex);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(" \n§dMenu - Help\n \n§6/swmenu stats §f- §7Open stats menu.\n§6/swmenu delivery §f- §7Open delivery menu.\n§6/swmenu cosmetics §f- §7Open cosmetics menu.\n ");
                return true;
            }

            String action = args[0];
            if (action.equalsIgnoreCase("stats")) {
                new StatsNPCMenu(player);
            } else if (action.equalsIgnoreCase("delivery")) {
                new DeliveryManMenu(player);
            } else if (action.equalsIgnoreCase("cosmetics")) {
                new CosmeticsMenu(player);
            } else {
                player.sendMessage(" \n§dMenu - Help\n \n§6/swmenu stats §f- §7Open stats menu.\n§6/swmenu delivery §f- §7Open delivery menu.\n§6/swmenu cosmetics §f- §7Open cosmetics menu.\n ");
            }
        }

        return true;
    }
}
