package org.twightlight.skywars.hook.decenthologram.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.ui.enums.SkyWarsMode;
import org.twightlight.skywars.arena.ui.enums.SkyWarsType;
import org.twightlight.skywars.hook.decenthologram.User;
import org.twightlight.skywars.hook.decenthologram.holograms.Leaderboard;
import org.twightlight.skywars.modules.privategames.PrivateGames;
import org.twightlight.skywars.modules.privategames.menus.MainMenu;

import java.util.Arrays;


public class LeaderboardsCommand extends Command {

    public LeaderboardsCommand() {
        super("lead");
        this.setAliases(Arrays.asList("leaderboard", "leaderboards"));
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
            User user = User.getFromPlayer(player);
            if (args.length == 0) {

                return true;
            }

            switch (args[0].toLowerCase()) {
                case "switch":
                    user.switchLeaderboards();
                case "view":
                    if (args.length == 2) {
                        String mode = args[1].toLowerCase();

                        user.switchLeaderboards(Leaderboard.Mode.fromName(mode));
                    }
            }
            return true;
        }
        return true;
    }


}
