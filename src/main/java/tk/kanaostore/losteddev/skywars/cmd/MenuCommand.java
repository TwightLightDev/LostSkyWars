package tk.kanaostore.losteddev.skywars.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.*;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class MenuCommand extends Command {

    public MenuCommand() {
        super("lswmenu");
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            Main.LOGGER.log(LostLogger.LostLevel.SEVERE, "Could not register command: ", ex);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(" \n§dMenu - Help\n \n§6/lswmenu play <solo/doubles/ranked/duels> §f- §7Open play menu.\n§6/lswmenu stats §f- §7Open stats menu.\n§6/lswmenu delivery §f- §7Open delivery menu.\n ");
                return true;
            }

            String action = args[0];
            if (action.equalsIgnoreCase("play")) {
                if (args.length < 2) {
                    player.sendMessage("§cUse /lswmenu play <solo/doubles/ranked/duels>");
                    return true;
                }

                if (args[1].equalsIgnoreCase("solo")) {
                    new PlayMenu(player, SkyWarsMode.SOLO);
                } else if (args[1].equalsIgnoreCase("doubles")) {
                    new PlayMenu(player, SkyWarsMode.DOUBLES);
                } else if (args[1].equalsIgnoreCase("ranked")) {
                    new PlayRankedMenu(player);
                } else if (args[1].equalsIgnoreCase("duels")) {
                    new PlayDuelsMenu(player);
                }
            } else if (action.equalsIgnoreCase("stats")) {
                new StatsNPCMenu(player);
            } else if (action.equalsIgnoreCase("delivery")) {
                new DeliveryManMenu(player);
            } else {
                player.sendMessage(" \n§dMenu - Help\n \n§6/lswmenu play <solo/doubles/ranked/duels> §f- §7Open play menu.\n§6/lswmenu stats §f- §7Open stats menu.\n§6/lswmenu delivery §f- §7Open delivery menu.\n ");
            }
        }

        return true;
    }
}
