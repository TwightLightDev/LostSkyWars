package org.twightlight.skywars.hook.guilds.donation;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.hook.guilds.GuildsHook;
import org.twightlight.skywars.hook.guilds.level.Level;
import org.twightlight.skywars.player.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Donator {
    private UUID uuid;
    private long next_refresh;
    private int donation_limit;
    private int donation_today;
    private double ratio;
    private Level level;
    private int coins;
    private Account account;
    private static Map<UUID, Donator> donatorList = new HashMap<>();
    public Donator(Player p) {
        this.uuid = p.getUniqueId();
        donatorList.put(p.getUniqueId(), this);

        next_refresh = Long.parseLong(GuildsHook.getExternalDB().
                getData(p, "nextRefresh", new TypeToken<String>() {}, "0"));
        if (System.currentTimeMillis() > next_refresh) {
            GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), 0, "donation_today");
        }
        while (System.currentTimeMillis() > next_refresh) {
            next_refresh += 86_400_000;
        }
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), next_refresh, "nextRefresh");

        donation_today = GuildsHook.getExternalDB().getData(p, "donation_today", new TypeToken<Integer>() {}, 0);
        donation_limit = GuildsHook.getExternalDB().getData(p, "donation_limit", new TypeToken<Integer>() {}, 1000);
        ratio = GuildsHook.getExternalDB().getData(p, "ratio", new TypeToken<Double>() {}, 0.75D);
        coins = GuildsHook.getExternalDB().getData(p, "coins", new TypeToken<Integer>() {}, 0);
        level = new Level(this);
        account = Database.getInstance().getAccount(p.getUniqueId());

    }

    public static Donator getFromUUID(UUID uuid) {
        return donatorList.get(uuid);
    }

    public double getRatio() {
        return ratio;
    }

    public int getDonationLimit() {
        return donation_limit;
    }

    public int getDonationToday() {
        return donation_today;
    }

    public long getNextRefresh() {
        return next_refresh;
    }

    public void setDonationLimit(int donation_limit) {
        this.donation_limit = donation_limit;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public void setNextRefresh(long next_refresh) {
        this.next_refresh = next_refresh;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void donate(int donation_amount) {
        if (account != null) {
            int left = donation_limit - donation_today;
            int addAmount;
            int finalAmount;
            if (left <= 0) {
                GuildsHook.getLanguage().getList("guilds.donation.limit-reached").forEach(line -> account.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
                return;
            }
            if (donation_amount > left) {
                addAmount = (int) Math.round(left * ratio);
                donation_today += left;
                finalAmount = left;
                account.removeStat("coins", left);

            } else {
                addAmount = (int) Math.round(donation_amount * ratio);
                donation_today += donation_amount;
                finalAmount = donation_amount;
                account.removeStat("coins", donation_amount);
            }
            GuildsHook.getLanguage().getList("guilds.donation.success").
                    forEach(line -> account.getPlayer().
                            sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    line.replace("{pcoins}", String.valueOf(finalAmount))
                                            .replace("{gcoins}", String.valueOf(addAmount))
                                            .replace("{gexp}", String.valueOf(addAmount))
                            )));

            coins += addAmount;
            level.addXP(addAmount);
            GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), donation_today, "donation_today");
            GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), coins, "coins");

        }
    }

    public int getGuildCoins() {
        return coins;
    }

    public void setGuildCoins(int amount) {
        coins = amount;
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), coins, "coins");
    }

    public void resetGuildCoins() {
        coins = 0;
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), coins, "coins");
    }

    public void giveGuildCoins(int amount) {
        coins += amount;
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), coins, "coins");
    }

    public void takeGuildCoins(int amount) {
        coins -= amount;
        if (coins < 0) {
            coins = 0;
        }
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), coins, "coins");
    }

    public Account getAccount() {
        return account;
    }

    public void saveData() {
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), String.valueOf(next_refresh), "nextRefresh");
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), donation_limit, "donation_limit");
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), donation_today, "donation_today");
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), ratio, "ratio");
        GuildsHook.getExternalDB().updateData(Bukkit.getPlayer(uuid), coins, "coins");
        level.saveData();
    }

    public Level getLevel() {
        return level;
    }

    public static Map<UUID, Donator> getDonatorList() {
        return donatorList;
    }

    public static void removeDonator(UUID uuid) {
        donatorList.remove(uuid);
    }
}
