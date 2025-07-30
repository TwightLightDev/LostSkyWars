package org.twightlight.skywars.cmd.sw;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.cmd.SubCommand;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.utils.StringUtils;

public class CosmeticsCommand extends SubCommand {

    public CosmeticsCommand() {
        super("cosmetics");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        String action = args[0];
        if (action.equalsIgnoreCase("give")) {
            if (args.length < 4) {
                sendHelp(sender);
            }
            try {
                Cosmetic cosmetic = Cosmetic.findFrom(CosmeticServer.SKYWARS, CosmeticType.valueOf(args[1]), 1, args[2]);
                cosmetic.give(Database.getInstance().getAccount(Bukkit.getPlayer(args[3]).getUniqueId()));
                sender.sendMessage(StringUtils.formatColors("&aSuccessfully gave a cosmetic to " + args[3]));
            } catch (NullPointerException | NumberFormatException e) {
                sendHelp(sender);
            }

        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" \n§dSetup - Help\n \n§6/lsw cosmetics give <type> <id> <player> §f- §7Give cosmetic to a player.\n ");
    }

    @Override
    public void perform(Player player, String[] args) {

    }

    @Override
    public String getUsage() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return "Fast setup through gui.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }
}
