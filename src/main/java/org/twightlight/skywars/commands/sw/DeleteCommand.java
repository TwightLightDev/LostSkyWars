package org.twightlight.skywars.commands.sw;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.commands.SubCommand;

public class DeleteCommand extends SubCommand {

    public DeleteCommand() {
        super("delete");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUse /lsw delete <world>");
            return;
        }

        Arena server = Arena.getByWorldName(args[0]);
        if (server == null) {
            sender.sendMessage("§5[LostSkyWars] §cThis world does not have an arena");
            return;
        }

        if (server.getState() != SkyWarsState.WAITING) {
            sender.sendMessage("§5[LostSkyWars] §cThis arena is now ingame.");
            return;
        }

        Arena.removeArena(server);
        sender.sendMessage("§5[LostSkyWars] §aArena deleted.");
    }

    @Override
    public void perform(Player player, String[] args) {
    }

    @Override
    public String getUsage() {
        return "delete <world>";
    }

    @Override
    public String getDescription() {
        return "Delete an Server.";
    }

    @Override
    public boolean onlyForPlayer() {
        return false;
    }
}
