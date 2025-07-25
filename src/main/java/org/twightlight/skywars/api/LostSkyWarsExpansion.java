package org.twightlight.skywars.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.level.Level;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.ranked.Ranked;
import org.twightlight.skywars.utils.StringUtils;

public class LostSkyWarsExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "lostedd";
    }

    @Override
    public String getIdentifier() {
        return "lostskywars";
    }

    @Override
    public String getVersion() {
        return SkyWars.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        Account account = null;
        if (player == null || (account = Database.getInstance().getAccount(player.getUniqueId())) == null) {
            return "";
        }

        if (params.equals("coins")) {
            return account.getFormatted("coins");
        } else if (params.equals("souls")) {
            return account.getFormatted("souls");
        } else if (params.equals("max_souls")) {
            return StringUtils.formatNumber(account.getContainers("account").get("sw_maxsouls").getAsInt());
        } else if (params.startsWith("solo_")) {
            if (params.equals("solo_games")) {
                return account.getFormatted("soloplays");
            }
            return account.getFormatted(params.replace("_", ""));
        } else if (params.startsWith("team_")) {
            if (params.equals("team_games")) {
                return account.getFormatted("teamplays");
            }
            return account.getFormatted(params.replace("_", ""));
        } else if (params.startsWith("ranked_")) {
            String key = params.split("_")[1];
            switch (key) {
                case "games":
                    return Ranked.getFormatted(account, "plays");
                case "league":
                    return Ranked.getLeague(account).getName();
                default:
                    return Ranked.getFormatted(account, key);
            }
        } else if (params.startsWith("overall_")) {
            String stat = params.split("_")[1];
            String soloKey = stat.equals("games") ? "soloplays" : "solo" + stat;
            String teamKey = stat.equals("games") ? "teamplays" : "team" + stat;
            String rankedKey = stat.equals("games") ? "plays" : stat;

            int solo = account.getInt(soloKey);
            int team = account.getInt(teamKey);
            int ranked = Ranked.getInt(account, rankedKey);

            return StringUtils.formatNumber(solo + team + ranked);
        } else if (params.equals("level")) {
            int level = account.getLevel();
            return "" + level;
        } else if (params.equals("level_symbol")) {
            return Level.getByLevel(account.getLevel()).getLevelSymbol(account);
        } else if (params.equals("current_exp")) {
            double level = account.getExp();
            return "" + level;
        }

        return null;
    }
}
