package org.twightlight.skywars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.bungee.core.Core;
import org.twightlight.skywars.bungee.core.CoreMode;
import org.twightlight.skywars.commands.sw.*;
import org.twightlight.skywars.integration.boxes.commands.BoxNPCCommand;
import org.twightlight.skywars.integration.citizens.commands.DeliveryNPCCommand;
import org.twightlight.skywars.integration.citizens.commands.ShopkeeperNPCCommand;
import org.twightlight.skywars.integration.citizens.commands.StatsNPCCommand;

import java.util.*;
import java.util.stream.Collectors;

public class SkyWarsCommand extends Command {

    private List<SubCommand> commands = new ArrayList<>();

    public SkyWarsCommand() {
        super("lsw");
        this.setAliases(Arrays.asList("lostsw", "lostskywars"));

        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "lostskywars", this);
        } catch (ReflectiveOperationException ex) {
            SkyWars.LOGGER.log(Level.SEVERE, "Cannot register command: ", ex);
        }

        if (Core.MODE != CoreMode.ARENA) {
            commands.add(new SetLobbyCommand());
            commands.add(new BuildCommand());
            if (SkyWars.citizens) {
                commands.add(new DeliveryNPCCommand());
                commands.add(new ShopkeeperNPCCommand());
                if (SkyWars.protocollib) {
                    commands.add(new StatsNPCCommand());
                }
                commands.add(new PreviewCommand());
            }
            if (SkyWars.lostboxes) {
                commands.add(new BoxNPCCommand());
            }
            commands.add(new WellNPCCommand());
            commands.add(new AODCommand());
            commands.add(new SetupCommand());
            commands.add(new ReloadCommand());

        }
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            commands.add(new CreateCageCommand());
            commands.add(new DeathCryCommand());
        }
        if (Core.MODE != CoreMode.ARENA) {}
        commands.add(new GiveCommand());
        commands.add(new RemoveCommand());
        if (Core.MODE != CoreMode.LOBBY) {
            commands.add(new ForceStartCommand());
            commands.add(new LoadCommand());
            commands.add(new UnloadCommand());
            commands.add(new TeleportCommand());
            commands.add(new CreateCommand());
            commands.add(new ChestCommand());
            commands.add(new BalloonsCommand());
            commands.add(new WaitingLobbyCommand());
        }
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            commands.add(new CloneCommand());
            commands.add(new DeleteCommand());
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("lostskywars.cmd.skywars")) {
            sender.sendMessage("§fUnknown command. Type \"/help\" for help.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender, 1);
            return true;
        }

        try {
            sendHelp(sender, Integer.parseInt(args[0]));
        } catch (NumberFormatException ex) {
            SubCommand subCommand = commands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if (subCommand == null) {
                sendHelp(sender, 1);
                return true;
            }

            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(args));
            list.remove(0);
            if (subCommand.onlyForPlayer()) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§5[LostSkyWars] §cThis command can be used only by players.");
                    return true;
                }

                subCommand.perform((Player) sender, list.toArray(new String[list.size()]));
                return true;
            }

            subCommand.perform(sender, list.toArray(new String[list.size()]));
        }

        return true;
    }

    private void sendHelp(CommandSender sender, int page) {
        List<SubCommand> commands = this.commands.stream().filter(subcommand -> sender instanceof Player || !subcommand.onlyForPlayer()).collect(Collectors.toList());
        Map<Integer, StringBuilder> pages = new HashMap<>();

        int pagesCount = (commands.size() + 5) / 6;
        for (int index = 0; index < commands.size(); index++) {
            int currentPage = (index + 6) / 6;
            if (!pages.containsKey(currentPage)) {
                pages.put(currentPage, new StringBuilder(" \n§dHelp - " + currentPage + "/" + pagesCount + "\n \n"));
            }

            pages.get(currentPage).append("§6/lsw " + commands.get(index).getUsage() + " §f- §7" + commands.get(index).getDescription() + "\n");
        }

        StringBuilder sb = pages.get(page);
        if (sb == null) {
            sender.sendMessage("§5[LostSkyWars] §cPage not found.");
            return;
        }

        sb.append(" ");
        sender.sendMessage(sb.toString());
    }
}
