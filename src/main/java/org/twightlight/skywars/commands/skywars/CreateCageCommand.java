package org.twightlight.skywars.commands.skywars;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.commands.SubCommand;
import org.twightlight.skywars.setup.cage.CageSetupSession;

public class CreateCageCommand extends SubCommand{

    public CreateCageCommand() {
        super("createcage");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
    }

    @Override
    public void perform(Player player, String[] args) {


        if (args.length < 1) {
            player.sendMessage("§cUse /lsw createcage <id>");
            return;
        }

        new CageSetupSession(player, args[0]);
        player.sendMessage("§aCage setup started for: §e" + args[0]);

    }

    @Override
    public String getUsage() {
        return "createcage <id>";
    }

    @Override
    public String getDescription() {
        return "Create a new SkyWars Cage.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }

}
