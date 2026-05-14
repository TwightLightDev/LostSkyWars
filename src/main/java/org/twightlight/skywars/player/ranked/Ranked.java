package org.twightlight.skywars.player.ranked;

import org.bukkit.entity.Player;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.StringUtils;

import java.util.List;

public class Ranked {

    public static void addElo(Account account, String groupId, int amount) {
        account.addStat(groupId, "elo", amount);
    }

    public static void removeElo(Account account, String groupId, int amount) {
        account.removeStat(groupId, "elo", amount);
    }

    public static int getElo(Account account, String groupId) {
        return account.getStat(groupId, "elo");
    }

    public static int getTotalElo(Account account) {
        int total = 0;
        total += account.getStat("ranked_solo", "elo");
        total += account.getStat("ranked_doubles", "elo");
        return total;
    }

    public static void addBravePoints(Account account, String groupId, int amount) {
        account.addStat(groupId, "brave_points", amount);
        if (account.getStat(groupId, "brave_points") > 100) {
            account.getStatsForGroup(groupId).get("brave_points").set(100);
        }
    }

    public static int getBravePoints(Account account, String groupId) {
        return account.getStat(groupId, "brave_points");
    }

    public static void increase(Account account, String groupId, String statName) {
        increase(account, groupId, statName, 1);
    }

    public static void increase(Account account, String groupId, String statName, int amount) {
        account.addStat(groupId, statName, amount);
    }

    public static void decrease(Account account, String groupId, String statName) {
        decrease(account, groupId, statName, 1);
    }

    public static void decrease(Account account, String groupId, String statName, int amount) {
        account.removeStat(groupId, statName, amount);
    }

    public static int getInt(Account account, String groupId, String statName) {
        return account.getStat(groupId, statName);
    }

    public static String getFormatted(Account account, String groupId, String statName) {
        return StringUtils.formatNumber(account.getStat(groupId, statName));
    }

    public static String getTag(Player player) {
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) return "";
        return StringUtils.getFirstColor(getLeague(account).getName()) + "[" + StringUtils.formatNumber(getTotalElo(account)) + "] ";
    }

    public static League getLeague(Account account) {
        int points = getTotalElo(account);
        List<League> leagues = League.listLeagues();

        if (leagues.isEmpty()) return null;

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
