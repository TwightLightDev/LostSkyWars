package org.twightlight.skywars.cmd.sw;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.cmd.SubCommand;
import org.twightlight.skywars.utils.LostLogger;
import org.twightlight.skywars.utils.LostLogger.LostLevel;

public class UnloadCommand extends SubCommand {

    public static final LostLogger LOGGER = Main.LOGGER.getModule("UnloadCommand");

    public UnloadCommand() {
        super("unload");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUse /lsw unload <world-name>");
            return;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world != null) {
            try {
                Bukkit.unloadWorld(world, true);
                sender.sendMessage("§5[LostSkyWars] §aWorld unloaded successfully!");
            } catch (Exception ex) {
                LOGGER.log(LostLevel.WARNING, "Cannot unload world \"" + world.getName() + "\": ", ex);
                sender.sendMessage("§5[LostSkyWars] §cError unloading \"" + world.getName() + "\" (see the console)");
            }
        } else {
            sender.sendMessage("§5[LostSkyWars] §cWorld not found.");
        }
    }

    @Override
    public void perform(Player player, String[] args) {
    }

    @Override
    public String getUsage() {
        return "unload <world-name>";
    }

    @Override
    public String getDescription() {
        return "Unload a server world.";
    }

    @Override
    public boolean onlyForPlayer() {
        return false;
    }
}
