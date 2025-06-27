package org.twightlight.skywars.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.menu.profile.StatisticsMenu;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.Logger.Level;

public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats");

        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass()
                    .getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            Main.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("lostskywars.cmd.stats")) {
                player.sendMessage(Language.command$stats$permission);
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(Language.command$stats$args);
                return true;
            }

            Account account = null;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null
                    || (account = Database.getInstance().getAccount(target.getUniqueId())) == null) {
                account = Database.getInstance().loadOffline(args[0]);
            }

            if (account == null) {
                player.sendMessage(Language.command$stats$user_not_found);
                return true;
            }

            new StatisticsMenu(player, account);
            return true;
        }

        return false;
    }
}
