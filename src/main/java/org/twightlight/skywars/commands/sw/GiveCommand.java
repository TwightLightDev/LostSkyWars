package org.twightlight.skywars.commands.sw;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.commands.SubCommand;
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

            CompletableFuture<Account> accountFuture = Database.getInstance().loadAccountOffline(args[1]);

            if (accountFuture == null) {
                sender.sendMessage("§5[LostSkyWars] §cUser not found!");
                return;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            boolean save = (target == null || !target.isOnline());

            accountFuture.thenAccept((account -> {
                if (account == null) {
                    sender.sendMessage("§5[LostSkyWars] §cUser not found!");
                    return;
                }
                try {
                    if (args[2].startsWith("-") || args[2].equals("0")) {
                        throw new NumberFormatException();
                    }

                    int amount = Integer.parseInt(args[2]);

                    if (type.equalsIgnoreCase("coins")) {
                        account.addCoins(amount);
                        int current = account.getCoins();
                        if (current < 0) {
                            account.getProfile().get("coins").set(0);
                        }
                        sender.sendMessage("§5[LostSkyWars] §aChanged " + account.getName() + " coins to §b" + StringUtils.formatNumber(account.getCoins()) + "§a.");
                    } else {
                        account.addSouls(amount);
                        int current = account.getSouls();
                        if (current < 0) {
                            account.getProfile().get("souls").set(0);
                        }
                        if (account.getSouls() > account.getMaxSouls()) {
                            account.getProfile().get("souls").set(account.getMaxSouls());
                        }
                        sender.sendMessage("§5[LostSkyWars] §aChanged " + account.getName() + " souls to §b" + StringUtils.formatNumber(account.getSouls()) + "§a.");
                    }

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
