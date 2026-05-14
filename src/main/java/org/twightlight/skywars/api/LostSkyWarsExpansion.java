package org.twightlight.skywars.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.level.Level;
import org.twightlight.skywars.player.ranked.Ranked;
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
            return account.getCoinsFormatted();
        } else if (params.equals("souls")) {
            return account.getSoulsFormatted();
        } else if (params.equals("max_souls")) {
            return StringUtils.formatNumber(account.getMaxSouls());
        } else if (params.startsWith("solo_")) {
            String stat = params.substring(5);
            if (stat.equals("games")) {
                return account.getStatFormatted("solo", "plays");
            }
            String mappedStat = mapLegacyStatName(stat);
            return account.getStatFormatted("solo", mappedStat);
        } else if (params.startsWith("team_")) {
            String stat = params.substring(5);
            if (stat.equals("games")) {
                return account.getStatFormatted("doubles", "plays");
            }
            String mappedStat = mapLegacyStatName(stat);
            return account.getStatFormatted("doubles", mappedStat);
        } else if (params.startsWith("ranked_")) {
            String key = params.substring(7);
            switch (key) {
                case "games":
                    return account.getStatFormatted("ranked_solo", "plays");
                case "league":
                    return Ranked.getLeague(account) != null ? Ranked.getLeague(account).getName() : "";
                case "points":
                    return account.getStatFormatted("ranked_solo", "elo");
                default:
                    String mappedStat = mapLegacyStatName(key);
                    return account.getStatFormatted("ranked_solo", mappedStat);
            }
        } else if (params.startsWith("overall_")) {
            String stat = params.substring(8);
            if (stat.equals("games")) {
                int total = account.getStat("solo", "plays") + account.getStat("doubles", "plays") + account.getStat("ranked_solo", "plays");
                return StringUtils.formatNumber(total);
            }
            String mappedStat = mapLegacyStatName(stat);
            int solo = account.getStat("solo", mappedStat);
            int team = account.getStat("doubles", mappedStat);
            int ranked = account.getStat("ranked_solo", mappedStat);
            return StringUtils.formatNumber(solo + team + ranked);
        } else if (params.equals("level")) {
            return "" + account.getLevel();
        } else if (params.equals("level_symbol")) {
            return Level.getByLevel(account.getLevel()).getLevelSymbol(account);
        } else if (params.equals("current_exp")) {
            return "" + account.getExp();
        }

        return null;
    }

    private String mapLegacyStatName(String legacyStat) {
        switch (legacyStat) {
            case "melee": return "melee_kills";
            case "bow": return "bow_kills";
            case "mob": return "mob_kills";
            case "void": return "void_kills";
            default: return legacyStat;
        }
    }
}
