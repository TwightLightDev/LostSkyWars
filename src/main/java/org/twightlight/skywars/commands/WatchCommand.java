package org.twightlight.skywars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.ui.enums.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;

public class WatchCommand extends Command {

    public WatchCommand() {
        super("watch");

        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass()
                    .getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Level.SEVERE, "Could not register command: ", ex);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account != null) {
                if (account.getArena() != null) {
                    return true;
                }

                if (!player.hasPermission("lostskywars.cmd.watch")) {
                    player.sendMessage(Language.command$watch$permission);
                    return true;
                }

                if (args.length == 0) {
                    player.sendMessage(Language.command$watch$args);
                    return true;
                }

                Account acc = null;
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null || (acc = Database.getInstance().getAccount(target.getUniqueId())) == null) {
                    player.sendMessage(Language.command$watch$user_not_found);
                    return true;
                }

                Arena server = acc.getArena();
                if (server == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(target)) {
                    player.sendMessage(Language.command$watch$user_not_in_match);
                    return true;
                }

                player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                server.spectate(account, target);
            }
        }

        return false;
    }
}
