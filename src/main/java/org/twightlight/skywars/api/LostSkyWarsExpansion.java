package org.twightlight.skywars.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.level.Level;
import org.twightlight.skywars.player.rank.Rank;
import org.twightlight.skywars.player.ranked.League;
import org.twightlight.skywars.utils.StringUtils;

public class LostSkyWarsExpansion extends PlaceholderExpansion {

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
        return "lostskywars";
    }

    @Override
    public String getVersion() {
        return SkyWars.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";

        // =====================================================================
        // Player count placeholders: players_<groupId>
        // e.g. %lostskywars_players_solo%, %lostskywars_players_ranked_solo%
        // =====================================================================
        if (params.startsWith("players_")) {
            String targetGroup = params.substring(8);
            int playing = CoreLobbies.getPlayerCount(targetGroup);
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                for (Arena server : Arena.listArenas()) {
                    if (server.getGroup().getId().equals(targetGroup)) {
                        playing += server.getOnline();
                    }
                }
            }
            return String.valueOf(playing);
        }

        // =====================================================================
        // Player count by trait: players_trait_<trait>
        // e.g. %lostskywars_players_trait_has_elo% (counts all ranked groups)
        // =====================================================================
        if (params.startsWith("players_trait_")) {
            String trait = params.substring(14);
            int playing = 0;
            for (ArenaGroup group : GroupManager.getGroups()) {
                if (group.hasTrait(trait)) {
                    playing += CoreLobbies.getPlayerCount(group.getId());
                    if (Core.MODE == CoreMode.MULTI_ARENA) {
                        for (Arena server : Arena.listArenas()) {
                            if (server.getGroup().getId().equals(group.getId())) {
                                playing += server.getOnline();
                            }
                        }
                    }
                }
            }
            return String.valueOf(playing);
        }

        // =====================================================================
        // Rank / team tag placeholders (no account needed for rank_prefix)
        // =====================================================================
        if (params.equals("rank_prefix")) {
            return Rank.getRank(player).getColoredName();
        }

        // =====================================================================
        // Account-dependent placeholders
        // =====================================================================
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) return "";

        // --- Team tag / alphabet ---
        if (params.equals("team_tag")) {
            if (account.getArena() == null) return "";
            Arena arena = account.getArena();
            ArenaGroup group = arena.getGroup();
            if (group == null || group.getTeamSize() <= 1) return "";
            return arena.getTeam(player) != null ? arena.getTeam(player).getAlphabeticalTag() + " " : "";
        }
        if (params.equals("team_alphabet")) {
            if (account.getArena() == null) return "";
            Arena arena = account.getArena();
            ArenaGroup group = arena.getGroup();
            if (group == null || group.getTeamSize() <= 1) return "";
            return arena.getTeam(player) != null ? arena.getTeam(player).getAlphabetical() : "";
        }

        // --- Global profile stats ---
        if (params.equals("coins")) {
            return account.getCoinsFormatted();
        }
        if (params.equals("souls")) {
            return account.getSoulsFormatted();
        }
        if (params.equals("max_souls")) {
            return StringUtils.formatNumber(account.getMaxSouls());
        }
        if (params.equals("level")) {
            return String.valueOf(account.getLevel());
        }
        if (params.equals("level_symbol")) {
            return Level.getByLevel(account.getLevel()).getLevelSymbol(account);
        }
        if (params.equals("current_exp")) {
            return String.valueOf(account.getExp());
        }
        if (params.equals("elo")) {
            return account.getEloFormatted();
        }
        if (params.equals("brave_points")) {
            return String.valueOf(account.getBravePoints());
        }
        if (params.equals("league")) {
            League league = account.getLeague();
            return league != null ? league.getName() : "";
        }
        if (params.equals("league_tag")) {
            return account.getLeagueTag();
        }

        // =====================================================================
        // Per-group stats: <groupId>_<statName>
        // e.g. %lostskywars_solo_kills%, %lostskywars_ranked_solo_wins%
        //      %lostskywars_solo_games% (maps to "plays")
        // =====================================================================
        for (String groupId : GroupManager.getGroupIds()) {
            String prefix = groupId + "_";
            if (params.startsWith(prefix)) {
                String stat = params.substring(prefix.length());
                if (stat.equals("games")) {
                    return account.getStatFormatted(groupId, "plays");
                }
                String mappedStat = mapLegacyStatName(stat);
                return account.getStatFormatted(groupId, mappedStat);
            }
        }

        // =====================================================================
        // Overall stats: overall_<statName>
        // Sums across all groups
        // =====================================================================
        if (params.startsWith("overall_")) {
            String stat = params.substring(8);
            String mappedStat = stat.equals("games") ? "plays" : mapLegacyStatName(stat);
            int total = 0;
            for (String groupId : GroupManager.getGroupIds()) {
                total += account.getStat(groupId, mappedStat);
            }
            return StringUtils.formatNumber(total);
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
