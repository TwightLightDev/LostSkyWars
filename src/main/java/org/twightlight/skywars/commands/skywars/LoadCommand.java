package org.twightlight.skywars.commands.skywars;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.commands.SubCommand;

import java.io.File;

@SuppressWarnings("deprecation")
public class LoadCommand extends SubCommand {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("LoadCommand");

    public LoadCommand() {
        super("load");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUse /lsw load <world-name>");
            return;
        }

        if (Bukkit.getWorld(args[0]) != null) {
            sender.sendMessage("§5[LostSkyWars] §cWorld already loaded.");
            return;
        }

        File map = new File(args[0]);
        if (!map.exists() || !map.isDirectory()) {
            sender.sendMessage("§5[LostSkyWars] §cWorld folder not found.");
            return;
        }

        try {
            sender.sendMessage("§5[LostSkyWars] §aLoading...");
            Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> {
                sender.sendMessage("§5[LostSkyWars] §aWorld loaded successfully!");
            });
        } catch (Exception ex) {
            sender.sendMessage("§5[LostSkyWars] §cError loading \"" + map.getName() + "\" (see the console)");
            LOGGER.log(Level.WARNING, "Cannot load world \"" + map.getName() + "\": ", ex);
        }
    }

    @Override
    public void perform(Player player, String[] args) {
    }

    @Override
    public String getUsage() {
        return "load <world-name>";
    }

    @Override
    public String getDescription() {
        return "Load a world of server folder.";
    }

    @Override
    public boolean onlyForPlayer() {
        return false;
    }
}
