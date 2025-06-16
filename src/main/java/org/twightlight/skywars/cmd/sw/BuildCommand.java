package org.twightlight.skywars.cmd.sw;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.cmd.SubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuildCommand extends SubCommand {

    private static List<UUID> builders = new ArrayList<>();

    public BuildCommand() {
        super("build");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
    }

    @Override
    public void perform(Player player, String[] args) {
        if (builders.contains(player.getUniqueId())) {
            builders.remove(player.getUniqueId());
            player.sendMessage("§5[LostSkyWars] §aBuilder mode disabled.");
        } else {
            builders.add(player.getUniqueId());
            player.sendMessage("§5[LostSkyWars] §aBuilder mode enabled.");
        }
    }

    @Override
    public String getUsage() {
        return "build";
    }

    @Override
    public String getDescription() {
        return "Enable builder mode.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }

    public static void remove(Player player) {
        builders.remove(player.getUniqueId());
    }

    public static boolean isBuilder(Player player) {
        return builders.contains(player.getUniqueId());
    }
}
