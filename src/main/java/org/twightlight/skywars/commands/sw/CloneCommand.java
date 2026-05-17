package org.twightlight.skywars.commands.sw;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.ui.interfaces.ScanCallback;
import org.twightlight.skywars.commands.SubCommand;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;

import java.util.ArrayList;
import java.util.List;

public class CloneCommand extends SubCommand {

    public CloneCommand() {
        super("clone");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUse /lsw clone <world> <newName>");
            return;
        }

        String worldName = args[1];
        Arena server = Arena.getByWorldName(args[0]);
        if (server == null) {
            sender.sendMessage("§5[LostSkyWars] §cThis world does not have an arena");
            return;
        }

        if (Arena.getByWorldName(worldName) != null) {
            sender.sendMessage("§5[LostSkyWars] §cThis world already is a arena");
            return;
        }

        sender.sendMessage("§5[LostSkyWars] §aCloning the arena...");
        YamlWrapper cu = YamlWrapper.getConfig(worldName, "plugins/LostSkyWars/servers");
        cu.set("name", server.getName());
        cu.set("group", server.getGroup().getId());
        cu.set("cube", server.getConfig().getWorldCube().toString().replace(args[0], worldName));
        cu.set("min-players", server.getConfig().getMinPlayers());
        if (server.getConfig().getConfig().contains("waiting-cube")) {
            cu.set("waiting-cube", server.getConfig().getWaitingCube().toString().replace(args[0], worldName));
            cu.set("waiting-lobby", BukkitUtils.serializeLocation(server.getConfig().getWaitingLocation()).replace(args[0], worldName));
        }
        List<String> spawns = new ArrayList<>();
        for (String spawn : server.getConfig().listSpawns()) {
            spawns.add(spawn.replace(args[0], worldName));
        }
        cu.set("spawns", spawns);
        List<String> chests = new ArrayList<>();
        for (String chest : server.getConfig().listChests()) {
            chests.add(chest.replace(args[0], worldName));
        }
        cu.set("chests", chests);
        List<String> balloons = new ArrayList<>();
        for (String balloon : server.getConfig().listBalloons()) {
            balloons.add(balloon.replace(args[0], worldName));
        }
        cu.set("balloons", balloons);

        SkyWars.getInstance().getWorldLoader().cloneArenaWorld(args[0], worldName);

        Arena.loadArena(cu.getFile(), new ScanCallback() {

            @Override
            public void finish() {
                sender.sendMessage("§5[LostSkyWars] §aArena cloned.");
            }
        });
    }

    @Override
    public void perform(Player player, String[] args) {
    }

    @Override
    public String getUsage() {
        return "clone <world> <newName>";
    }

    @Override
    public String getDescription() {
        return "Clone an Server.";
    }

    @Override
    public boolean onlyForPlayer() {
        return false;
    }
}
