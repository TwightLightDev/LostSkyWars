package tk.kanaostore.losteddev.skywars.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.level.Level;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ranked.Ranked;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

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
        return Main.getInstance().getDescription().getVersion();
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
        } else if (params.equals("solo_wins")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("solo_kills")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("solo_melee")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("solo_mob")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("solo_void")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("solo_bow")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("solo_assists")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("solo_deaths")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("solo_games")) {
            return account.getFormatted(params.replace("_games", "plays"));
        } else if (params.equals("team_wins")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("team_kills")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("team_melee")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("team_void")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("team_mob")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("team_bow")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("team_assists")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("team_deaths")) {
            return account.getFormatted(params.replace("_", ""));
        } else if (params.equals("team_games")) {
            return account.getFormatted(params.replace("_games", "plays"));
        } else if (params.equals("ranked_wins")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
        } else if (params.equals("ranked_kills")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
        } else if (params.equals("ranked_melee")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
        } else if (params.equals("ranked_mob")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
        } else if (params.equals("ranked_void")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
        } else if (params.equals("ranked_bow")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
        } else if (params.equals("ranked_assists")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
        } else if (params.equals("ranked_deaths")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
        } else if (params.equals("ranked_games")) {
            return Ranked.getFormatted(account, "plays");
        } else if (params.equals("ranked_league")) {
            return Ranked.getLeague(account).getName();
        } else if (params.equals("ranked_points")) {
            return Ranked.getFormatted(account, params.split("_")[1]);
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
