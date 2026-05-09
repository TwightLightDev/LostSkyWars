package org.twightlight.skywars.player.ranked;

import org.bukkit.entity.Player;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.StringUtils;

import java.util.List;

public class Ranked {

    public static void decrease(Account account, String stats) {
        decrease(account, stats, 1);
    }

    public static void decrease(Account account, String stats, int amount) {
        for (int i = 0; i < amount; i++) {
            if (account.getContainer("ranked").get(stats).getAsInt() == 0) {
                break;
            }
            account.getContainer("ranked").get(stats).removeInt(1);
        }
    }

    public static void increase(Account account, String stats) {
        increase(account, stats, 1);
    }

    public static void increaseBravePoints(Account account, int amount) {
        account.getContainer("ranked").get("brave_points").addInt(amount);
        if (getBravePoints(account) > 100) {
            account.getContainer("ranked").get("brave_points").set(100);
        }
    }

    public static void increase(Account account, String stats, int amount) {
        account.getContainer("ranked").get(stats).addInt(amount);
    }

    public static String getTag(Player player) {
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        return StringUtils.getFirstColor(getLeague(account).getName()) + "[" + StringUtils.formatNumber(getPoints(account)) + "] ";
    }

    public static int getPoints(Account account) {
        return account.getContainer("ranked").get("points").getAsInt();
    }

    public static int getBravePoints(Account account) {
        return account.getContainer("ranked").get("brave_points").getAsInt();
    }

    public static int getInt(Account account, String key) {
        return account.getContainer("ranked").get(key).getAsInt();
    }

    public static String getFormatted(Account account, String key) {
        return StringUtils.formatNumber(account.getContainer("ranked").get(key).getAsInt());
    }

    public static League getLeague(Account account) {
        int points = getPoints(account);
        List<League> leagues = League.listLeagues();

        if (points >= leagues.get(0).getPoints()) {
            return leagues.get(0);
        }

        int min = 0;
        int max = leagues.size() - 1;
        int pivot;

        while (min <= max) {
            pivot = (min + max) / 2;
            int required = leagues.get(pivot).getPoints();

            if (points >= required) {
                if (pivot == 0 || points < leagues.get(pivot - 1).getPoints()) {
                    return leagues.get(pivot);
                }
                max = pivot - 1;
            } else {
                min = pivot + 1;
            }
        }

        return leagues.get(leagues.size() - 1);
    }
}
