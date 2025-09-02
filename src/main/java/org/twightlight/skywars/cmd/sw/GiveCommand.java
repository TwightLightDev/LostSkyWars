package org.twightlight.skywars.cmd.sw;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.cmd.SubCommand;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.StringUtils;

import java.util.concurrent.CompletableFuture;

public class GiveCommand extends SubCommand {

    public GiveCommand() {
        super("give");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(
                    " \n§dGive - Help\n \n§6/lsw give coins <player> <amount> §f- §7Give coins to a player.\n§6/lsw give souls <player> <amount> §f- §7Give souls to a player.\n ");
            return;
        }

        String type = args[0];
        if (type.equalsIgnoreCase("coins") || type.equalsIgnoreCase("souls")) {
            if (args.length < 3) {
                sender.sendMessage("§cUse /lsw give " + type.toLowerCase() + " <player> <amount>");
                return;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            CompletableFuture<Account> account1;
            boolean save;
            account1 = Database.getInstance().loadOffline(args[1]);

            if (account1 == null) {
                sender.sendMessage("§5[LostSkyWars] §cUser not found!");
                return;
            }
            if (target == null || !target.isOnline()) {
                save = true;
            } else {
                save = false;
            }

            account1.thenAccept((account -> {
                try {
                    if (args[2].startsWith("-") || args[2].equals("0")) {
                        throw new NumberFormatException();
                    }

                    int amount = Integer.parseInt(args[2]);
                    account.addStat(type.toLowerCase(), amount);
                    if (account.getInt(type.toLowerCase()) < 0) {
                        account.getContainer("skywars").get(type.toLowerCase()).set(0);
                    }

                    if (type.equalsIgnoreCase("souls")) {
                        if (account.getInt("souls") > account.getContainer("account").get("sw_maxsouls").getAsInt()) {
                            account.getContainer("skywars").get("souls").set(account.getContainer("account").get("sw_maxsouls").getAsInt());
                        }
                    }

                    sender.sendMessage(
                            "§5[LostSkyWars] §aChanged " + account.getName() + " " + type.toLowerCase() + " to §b" + StringUtils.formatNumber(account.getInt(type.toLowerCase())) + "§a.");

                    if (save) {
                        account.save();
                        account.destroy();
                    }
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§5[LostSkyWars] §cInvalid amount (amount > 0 & amount < " + Integer.MAX_VALUE + ")");
                }
            }));

        } else {
            sender.sendMessage(
                    " \n§dGive - Help\n \n§6/lsw give coins <player> <amount> §f- §7Give coins to a player.\n§6/lsw give souls <player> <amount> §f- §7Give souls to a player.\n ");
        }
    }

    @Override
    public void perform(Player player, String[] args) {
    }

    @Override
    public String getUsage() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give coins/souls.";
    }

    @Override
    public boolean onlyForPlayer() {
        return false;
    }
}
