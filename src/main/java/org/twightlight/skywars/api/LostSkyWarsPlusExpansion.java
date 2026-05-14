package org.twightlight.skywars.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.rank.Rank;

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
            int playing = CoreLobbies.getPlayerCount("solo") + CoreLobbies.getPlayerCount("solo_insane");
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena server : Arena.listServers()) {
                    String gid = server.getGroup().getId();
                    if (gid.equals("solo") || gid.equals("solo_insane")) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_doubles")) {
            int playing = CoreLobbies.getPlayerCount("doubles") + CoreLobbies.getPlayerCount("doubles_insane");
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena server : Arena.listServers()) {
                    String gid = server.getGroup().getId();
                    if (gid.equals("doubles") || gid.equals("doubles_insane")) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_ranked")) {
            int playing = CoreLobbies.getPlayerCount("ranked_solo") + CoreLobbies.getPlayerCount("ranked_doubles");
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena server : Arena.listServers()) {
                    if (server.getGroup().hasTrait("has_elo")) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("players_duels")) {
            int playing = CoreLobbies.getPlayerCount("duels");
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena server : Arena.listServers()) {
                    if (server.getGroup().getId().equals("duels")) {
                        playing += server.getOnline();
                    }
                }
            }
            return "" + playing;
        } else if (params.equals("rank_prefix")) {
            return Rank.getRank(player).getColoredName();
        } else if (params.equals("team_tag")) {
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null || account.getArena() == null) return "";
            Arena arena = account.getArena();
            ArenaGroup group = arena.getGroup();
            if (group == null || group.getTeamSize() <= 1) return "";
            return arena.getTeam(player) != null ? arena.getTeam(player).getAlphabeticalTag() + " " : "";
        } else if (params.equals("team_alphabet")) {
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null || account.getArena() == null) return "";
            Arena arena = account.getArena();
            ArenaGroup group = arena.getGroup();
            if (group == null || group.getTeamSize() <= 1) return "";
            return arena.getTeam(player) != null ? arena.getTeam(player).getAlphabetical() : "";
        }

        return null;
    }
}
