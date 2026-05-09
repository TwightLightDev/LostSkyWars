package org.twightlight.skywars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.menu.play.PlayDuelsMenu;
import org.twightlight.skywars.menu.play.PlayMenu;
import org.twightlight.skywars.menu.play.PlayRankedMenu;

public class PlayCommand extends Command {

    public PlayCommand() {
        super("play");

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
            if (args.length == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(" \n§dPlay - Help\n \n§6/play <group> §f- §7Open play menu.\n§7Available groups: ");
                for (ArenaGroup gid : GroupManager.all()) {
                    sb.append(gid.getId()).append(", ");
                }
                player.sendMessage(sb.toString().trim());
                return true;
            }

            String arg = args[0].toLowerCase();
            if (arg.equals("solo") || arg.equals("solo_insane")) {
                new PlayMenu(player, "solo");
            } else if (arg.equals("doubles") || arg.equals("doubles_insane")) {
                new PlayMenu(player, "doubles");
            } else if (arg.equals("ranked") || arg.equals("ranked_solo") || arg.equals("ranked_doubles")) {
                new PlayRankedMenu(player);
            } else if (arg.equals("duels")) {
                new PlayDuelsMenu(player);
            } else {
                if (GroupManager.get(arg) != null) {
                    new PlayMenu(player, arg);
                } else {
                    player.sendMessage("§cUnknown group. Use /play <group>");
                }
            }
        }

        return true;
    }
}
