package org.twightlight.skywars.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.type.Duels;
import org.twightlight.skywars.arena.type.solo.Solo;
import org.twightlight.skywars.arena.type.solo.SoloRanked;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.rank.Rank;
import org.twightlight.skywars.arena.ui.enums.SkyWarsMode;
import org.twightlight.skywars.arena.ui.enums.SkyWarsType;
import org.twightlight.skywars.arena.Arena;

public class LostSkyWarsPlusExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "TwightLight";
    }

    @Override
    public String getIdentifier() {
        return "lswplus";
    }

    @Override
    public String getVersion() {
        return SkyWars.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {

        if (params.equals("players_solo")) {
            int playing = CoreLobbies.SOLO_NORMAL + CoreLobbies.SOLO_INSANE;
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena<?> server : Arena.listServers()) {
                    if (server.getMode().equals(SkyWarsMode.SOLO) && (server.getType() == SkyWarsType.NORMAL || server.getType() == SkyWarsType.INSANE)) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_doubles")) {
            int playing = CoreLobbies.DOUBLES_NORMAL + CoreLobbies.DOUBLES_INSANE;
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena<?> server : Arena.listServers()) {
                    if (server.getMode().equals(SkyWarsMode.DOUBLES) && (server.getType() == SkyWarsType.NORMAL || server.getType() == SkyWarsType.INSANE)) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_ranked")) {
            int playing = CoreLobbies.DOUBLES_RANKED + CoreLobbies.SOLO_RANKED;
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena<?> server : Arena.listServers()) {
                    if (server.getType().equals(SkyWarsType.RANKED)) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_duels")) {
            int playing = CoreLobbies.SOLO_DUELS + CoreLobbies.DOUBLES_DUELS;
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena<?> server : Arena.listServers()) {
                    if (server.getType().equals(SkyWarsType.DUELS)) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("rank_prefix")) {
            return Rank.getRank(player).getColoredName();
        } else if (params.equals("team_tag")) {
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null ||
                    account.getServer() == null ||
                    !(account.getServer() instanceof Arena<?>) ||
                    (account.getServer() instanceof Solo) ||
                    (account.getServer() instanceof SoloRanked)||
                    (account.getServer() instanceof Duels)) {
                return "";
            }
            Arena<?> arena = (Arena<?>) account.getServer();
            return arena.getTeam(player).getAlphabeticalTag() + " ";
        } else if (params.equals("team_alphabet")) {
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null ||
                    account.getServer() == null ||
                    !(account.getServer() instanceof Arena<?>) ||
                    (account.getServer() instanceof Solo) ||
                    (account.getServer() instanceof SoloRanked)||
                    (account.getServer() instanceof Duels)) {
                return "";
            }
            Arena<?> arena = (Arena<?>) account.getServer();
            return arena.getTeam(player).getAlphabetical();
        }

        return null;
    }
}
