package org.twightlight.skywars.cmd.sw;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cmd.SubCommand;
import org.twightlight.skywars.utils.Logger;
import org.twightlight.skywars.utils.Logger.Level;

public class UnloadCommand extends SubCommand {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("UnloadCommand");

    public UnloadCommand() {
        super("unload");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUse /lsw unload <world-name>");
            return;
        }

        if (args[0] != null) {
            try {
                SkyWars.getInstance().getWorldLoader().unload(args[0]);
                sender.sendMessage("§5[LostSkyWars] §aWorld unloaded successfully!");
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Cannot unload world \"" + args[0] + "\": ", ex);
                sender.sendMessage("§5[LostSkyWars] §cError unloading \"" + args[0] + "\" (see the console)");
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
