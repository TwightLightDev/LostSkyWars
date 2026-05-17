package org.twightlight.skywars.database.providers;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.bungee.core.Core;
import org.twightlight.skywars.bungee.core.CoreDatabase;
import org.twightlight.skywars.database.player.ValueContainer;
import org.twightlight.skywars.player.Account;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class MySQLDatabase extends Database {

    private String host;
    private String port;
    private String dbname;
    private String username;
    private String password;

    private Connection connection;
    private ExecutorService executor;

    public MySQLDatabase() {
        CoreDatabase config = Core.getCoreDatabase();
        this.host = config.getHost();
        this.port = config.getPort();
        this.dbname = config.getDbname();
        this.username = config.getUser();
        this.password = config.getPassword();

        this.openConnection();
        this.executor = Executors.newCachedThreadPool();

        this.migrateFromLegacy();

        this.createProfileTable();
        this.createStatsTable();
        this.createCosmeticsTable();
        this.createSelectionsTable();
    }

    private void createProfileTable() {
        this.update("CREATE TABLE IF NOT EXISTS profile ("
                + "uuid VARCHAR(36) PRIMARY KEY,"
                + "name VARCHAR(32) NOT NULL,"
                + "coins INTEGER DEFAULT 0,"
                + "souls INTEGER DEFAULT 0,"
                + "level INTEGER DEFAULT 1,"
                + "exp DOUBLE DEFAULT 0.0,"
                + "max_souls INTEGER DEFAULT 100,"
                + "well_roll INTEGER DEFAULT 1,"
                + "souls_per_win INTEGER DEFAULT 0,"
                + "mystery_dusts INTEGER DEFAULT 0,"
                + "deliveries TEXT DEFAULT '{}',"
                + "leveling TEXT DEFAULT '[]',"
                + "elo INTEGER DEFAULT 0,"
                + "brave_points INTEGER DEFAULT 0"
                + ");");
    }


    private void createStatsTable() {
        this.update("CREATE TABLE IF NOT EXISTS stats ("
                + "uuid VARCHAR(36) NOT NULL,"
                + "group_id VARCHAR(32) NOT NULL,"
                + "kills INTEGER DEFAULT 0,"
                + "wins INTEGER DEFAULT 0,"
                + "assists INTEGER DEFAULT 0,"
                + "deaths INTEGER DEFAULT 0,"
                + "plays INTEGER DEFAULT 0,"
                + "melee_kills INTEGER DEFAULT 0,"
                + "bow_kills INTEGER DEFAULT 0,"
                + "mob_kills INTEGER DEFAULT 0,"
                + "void_kills INTEGER DEFAULT 0,"
                + "PRIMARY KEY (uuid, group_id)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
    }

    private void createCosmeticsTable() {
        this.update("CREATE TABLE IF NOT EXISTS cosmetics ("
                + "uuid VARCHAR(36) PRIMARY KEY,"
                + "kits TEXT DEFAULT '{}',"
                + "perks TEXT DEFAULT '{}',"
                + "cages TEXT DEFAULT '{}',"
                + "death_cries TEXT DEFAULT '{}',"
                + "trails TEXT DEFAULT '{}',"
                + "balloons TEXT DEFAULT '{}',"
                + "kill_messages TEXT DEFAULT '{}',"
                + "kill_effects TEXT DEFAULT '{}',"
                + "sprays TEXT DEFAULT '{}',"
                + "victory_dances TEXT DEFAULT '{}',"
                + "titles TEXT DEFAULT '{}',"
                + "symbols TEXT DEFAULT '{}'"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
    }

    private void createSelectionsTable() {
        this.update("CREATE TABLE IF NOT EXISTS selections ("
                + "uuid VARCHAR(36) PRIMARY KEY,"
                + "kit TEXT DEFAULT '{}',"
                + "perk TEXT DEFAULT '{}',"
                + "cage INTEGER DEFAULT 0,"
                + "death_cry INTEGER DEFAULT 0,"
                + "trail INTEGER DEFAULT 0,"
                + "balloon INTEGER DEFAULT 0,"
                + "kill_message INTEGER DEFAULT 0,"
                + "kill_effect INTEGER DEFAULT 0,"
                + "spray INTEGER DEFAULT 0,"
                + "victory_dance INTEGER DEFAULT 0,"
                + "title INTEGER DEFAULT 0,"
                + "symbol INTEGER DEFAULT 0,"
                + "last_selected BIGINT DEFAULT 0,"
                + "favorites TEXT DEFAULT '[]'"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
    }

    // =========================================================================
    // MIGRATION
    // =========================================================================

    @Override
    public void migrateFromLegacy() {
        if (!tableExists("premium_lostedaccount")) {
            return;
        }
        LOGGER.info("Legacy tables detected. Starting migration...");

        this.createProfileTable();
        this.createStatsTable();
        this.createCosmeticsTable();
        this.createSelectionsTable();

        CachedRowSet accountRs = this.query("SELECT * FROM premium_lostedaccount");
        if (accountRs == null) {
            LOGGER.info("No data in premium_lostedaccount to migrate.");
            renameLegacyTables();
            return;
        }

        try {
            Map<String, String> uuidToName = new LinkedHashMap<>();
            Map<String, Map<String, Object>> accountData = new LinkedHashMap<>();

            while (accountRs.next()) {
                String uuid = accountRs.getString("id");
                String name = accountRs.getString("name");
                uuidToName.put(uuid, name);

                Map<String, Object> data = new LinkedHashMap<>();
                data.put("mystery_dusts", safeGetInt(accountRs, "mysterydusts", 0));
                data.put("max_souls", safeGetInt(accountRs, "sw_maxsouls", 100));
                data.put("well_roll", safeGetInt(accountRs, "sw_wellroll", 1));
                data.put("souls_per_win", safeGetInt(accountRs, "sw_soulswin", 0));
                data.put("deliveries", safeGetString(accountRs, "deliveries", "{}"));
                data.put("leveling", safeGetString(accountRs, "leveling", "[]"));
                accountData.put(uuid, data);
            }
            closeQuietly(accountRs);

            CachedRowSet swRs = this.query("SELECT * FROM premium_lostedskywars");
            Map<String, Map<String, Object>> swData = new LinkedHashMap<>();
            if (swRs != null) {
                while (swRs.next()) {
                    String uuid = swRs.getString("id");
                    Map<String, Object> data = new LinkedHashMap<>();

                    data.put("coins", safeGetInt(swRs, "coins", 0));
                    data.put("souls", safeGetInt(swRs, "souls", 0));
                    data.put("level", safeGetInt(swRs, "level", 1));
                    data.put("exp", safeGetDouble(swRs, "exp", 0.0));

                    data.put("solokills", safeGetInt(swRs, "solokills", 0));
                    data.put("solowins", safeGetInt(swRs, "solowins", 0));
                    data.put("soloassists", safeGetInt(swRs, "soloassists", 0));
                    data.put("solodeaths", safeGetInt(swRs, "solodeaths", 0));
                    data.put("soloplays", safeGetInt(swRs, "soloplays", 0));
                    data.put("solomelee", safeGetInt(swRs, "solomelee", 0));
                    data.put("solobow", safeGetInt(swRs, "solobow", 0));
                    data.put("solomob", safeGetInt(swRs, "solomob", 0));
                    data.put("solovoid", safeGetInt(swRs, "solovoid", 0));

                    data.put("teamkills", safeGetInt(swRs, "teamkills", 0));
                    data.put("teamwins", safeGetInt(swRs, "teamwins", 0));
                    data.put("teamassists", safeGetInt(swRs, "teamassists", 0));
                    data.put("teamdeaths", safeGetInt(swRs, "teamdeaths", 0));
                    data.put("teamplays", safeGetInt(swRs, "teamplays", 0));
                    data.put("teammelee", safeGetInt(swRs, "teammelee", 0));
                    data.put("teambow", safeGetInt(swRs, "teambow", 0));
                    data.put("teommob", safeGetInt(swRs, "teammob", 0));
                    data.put("teamvoid", safeGetInt(swRs, "teamvoid", 0));

                    data.put("kits", safeGetString(swRs, "kits", "{}"));
                    data.put("perks", safeGetString(swRs, "perks", "{}"));
                    data.put("cages", safeGetString(swRs, "cages", "{}"));
                    data.put("deathcry", safeGetString(swRs, "deathcry", "{}"));
                    data.put("trail", safeGetString(swRs, "trail", "{}"));
                    data.put("ballons", safeGetString(swRs, "ballons", "{}"));
                    data.put("killmessage", safeGetString(swRs, "killmessage", "{}"));
                    data.put("killeffect", safeGetString(swRs, "killeffect", "{}"));
                    data.put("spray", safeGetString(swRs, "spray", "{}"));
                    data.put("victorydance", safeGetString(swRs, "victorydance", "{}"));
                    data.put("title", safeGetString(swRs, "title", "{}"));
                    data.put("selected", safeGetString(swRs, "selected", "0:0:0 : 0"));
                    data.put("lastSelected", safeGetLong(swRs, "lastSelected", 0L));
                    data.put("favorites", safeGetString(swRs, "favorites", "[]"));

                    swData.put(uuid, data);
                }
                closeQuietly(swRs);
            }

            Map<String, Map<String, Object>> rankedData = new LinkedHashMap<>();
            if (tableExists("ranked_lostedskywars")) {
                CachedRowSet rankedRs = this.query("SELECT * FROM ranked_lostedskywars");
                if (rankedRs != null) {
                    while (rankedRs.next()) {
                        String uuid = rankedRs.getString("id");
                        Map<String, Object> data = new LinkedHashMap<>();
                        data.put("kills", safeGetInt(rankedRs, "kills", 0));
                        data.put("wins", safeGetInt(rankedRs, "wins", 0));
                        data.put("assists", safeGetInt(rankedRs, "assists", 0));
                        data.put("deaths", safeGetInt(rankedRs, "deaths", 0));
                        data.put("plays", safeGetInt(rankedRs, "plays", 0));
                        data.put("melee", safeGetInt(rankedRs, "melee", 0));
                        data.put("bow", safeGetInt(rankedRs, "bow", 0));
                        data.put("mob", safeGetInt(rankedRs, "mob", 0));
                        data.put("void", safeGetInt(rankedRs, "void", 0));
                        data.put("points", safeGetInt(rankedRs, "points", 0));
                        data.put("brave_points", safeGetInt(rankedRs, "brave_points", 0));
                        rankedData.put(uuid, data);
                    }
                    closeQuietly(rankedRs);
                }
            }

            for (Map.Entry<String, String> entry : uuidToName.entrySet()) {
                String uuid = entry.getKey();
                String name = entry.getValue();
                Map<String, Object> accData = accountData.get(uuid);
                Map<String, Object> sw = swData.get(uuid);

                int coins = sw != null ? (int) sw.getOrDefault("coins", 0) : 0;
                int souls = sw != null ? (int) sw.getOrDefault("souls", 0) : 0;
                int level = sw != null ? (int) sw.getOrDefault("level", 1) : 1;
                double exp = sw != null ? (double) sw.getOrDefault("exp", 0.0) : 0.0;

                // elo and brave_points go into profile (global), summed from ranked data
                Map<String, Object> ranked = rankedData.get(uuid);
                int elo = ranked != null ? (int) ranked.getOrDefault("points", 0) : 0;
                int bravePoints = ranked != null ? (int) ranked.getOrDefault("brave_points", 0) : 0;

                this.update("INSERT IGNORE INTO profile (uuid, name, coins, souls, level, exp, max_souls, well_roll, souls_per_win, mystery_dusts, deliveries, leveling, elo, brave_points) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        uuid, name, coins, souls, level, exp,
                        accData.get("max_souls"), accData.get("well_roll"), accData.get("souls_per_win"),
                        accData.get("mystery_dusts"),
                        accData.get("deliveries"), accData.get("leveling"),
                        elo, bravePoints);


                if (sw != null) {
                    this.update("INSERT IGNORE INTO stats (uuid, group_id, kills, wins, assists, deaths, plays, melee_kills, bow_kills, mob_kills, void_kills) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            uuid, "solo",
                            sw.get("solokills"), sw.get("solowins"), sw.get("soloassists"),
                            sw.get("solodeaths"), sw.get("soloplays"),
                            sw.get("solomelee"), sw.get("solobow"), sw.get("solomob"), sw.get("solovoid"));

                    this.update("INSERT IGNORE INTO stats (uuid, group_id, kills, wins, assists, deaths, plays, melee_kills, bow_kills, mob_kills, void_kills) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            uuid, "doubles",
                            sw.get("teamkills"), sw.get("teamwins"), sw.get("teamassists"),
                            sw.get("teamdeaths"), sw.get("teamplays"),
                            sw.get("teammelee"), sw.get("teambow"), sw.get("teammob"), sw.get("teamvoid"));

                    this.update("INSERT IGNORE INTO cosmetics (uuid, kits, perks, cages, death_cries, trails, balloons, kill_messages, kill_effects, sprays, victory_dances, titles, symbols) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            uuid,
                            sw.getOrDefault("kits", "{}"), sw.getOrDefault("perks", "{}"),
                            sw.getOrDefault("cages", "{}"), sw.getOrDefault("deathcry", "{}"),
                            sw.getOrDefault("trail", "{}"), sw.getOrDefault("ballons", "{}"),
                            sw.getOrDefault("killmessage", "{}"), sw.getOrDefault("killeffect", "{}"),
                            sw.getOrDefault("spray", "{}"), sw.getOrDefault("victorydance", "{}"),
                            sw.getOrDefault("title", "{}"), "{}");

                    String oldSelected = (String) sw.getOrDefault("selected", "0:0:0 : 0");
                    long lastSelected = (long) sw.getOrDefault("lastSelected", 0L);
                    String favorites = (String) sw.getOrDefault("favorites", "[]");

                    Map<String, Object> parsedSelection = parseLegacySelected(oldSelected);

                    this.update("INSERT IGNORE INTO selections (uuid, kit, perk, cage, death_cry, trail, balloon, kill_message, kill_effect, spray, victory_dance, title, symbol, last_selected, favorites) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            uuid,
                            parsedSelection.get("kit"), parsedSelection.get("perk"),
                            parsedSelection.get("cage"), parsedSelection.get("death_cry"),
                            parsedSelection.get("trail"), parsedSelection.get("balloon"),
                            parsedSelection.get("kill_message"), parsedSelection.get("kill_effect"),
                            parsedSelection.get("spray"), parsedSelection.get("victory_dance"),
                            parsedSelection.get("title"), parsedSelection.get("symbol"),
                            lastSelected, favorites);
                }

                // Ranked stats go into stats table WITHOUT elo/brave_points
                if (ranked != null) {
                    this.update("INSERT IGNORE INTO stats (uuid, group_id, kills, wins, assists, deaths, plays, melee_kills, bow_kills, mob_kills, void_kills) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            uuid, "ranked_solo",
                            ranked.get("kills"), ranked.get("wins"), ranked.get("assists"),
                            ranked.get("deaths"), ranked.get("plays"),
                            ranked.get("melee"), ranked.get("bow"), ranked.get("mob"), ranked.get("void"));
                }
            }

            LOGGER.info("Migration complete! Migrated " + uuidToName.size() + " players.");
            renameLegacyTables();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Migration failed: ", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseLegacySelected(String selected) {
        Map<String, Object> result = new LinkedHashMap<>();

        String[] parts = selected.split(" : ");

        String kitPart = parts.length > 0 ? parts[0] : "0:0:0:0";
        String[] kitModes = kitPart.split(":");

        org.json.simple.JSONObject kitJson = new org.json.simple.JSONObject();
        if (kitModes.length > 0 && !kitModes[0].trim().equals("0")) kitJson.put("solo", Integer.parseInt(kitModes[0].trim()));
        if (kitModes.length > 1 && !kitModes[1].trim().equals("0")) kitJson.put("solo_insane", Integer.parseInt(kitModes[1].trim()));
        if (kitModes.length > 2 && !kitModes[2].trim().equals("0")) kitJson.put("ranked_solo", Integer.parseInt(kitModes[2].trim()));
        if (kitModes.length > 3 && !kitModes[3].trim().equals("0")) kitJson.put("duels", Integer.parseInt(kitModes[3].trim()));
        result.put("kit", kitJson.toString());

        String perkPart = parts.length > 1 ? parts[1] : "0:0:0";
        String[] perkModes = perkPart.split(":");

        org.json.simple.JSONObject perkJson = new org.json.simple.JSONObject();
        if (perkModes.length > 0 && !perkModes[0].trim().equals("0")) perkJson.put("solo", Integer.parseInt(perkModes[0].trim()));
        if (perkModes.length > 1 && !perkModes[1].trim().equals("0")) perkJson.put("solo_insane", Integer.parseInt(perkModes[1].trim()));
        if (perkModes.length > 2 && !perkModes[2].trim().equals("0")) perkJson.put("ranked_solo", Integer.parseInt(perkModes[2].trim()));
        result.put("perk", perkJson.toString());

        result.put("cage", safeParseInt(parts, 2));
        result.put("death_cry", safeParseInt(parts, 3));
        result.put("balloon", safeParseInt(parts, 4));
        result.put("symbol", safeParseInt(parts, 5));
        result.put("trail", safeParseInt(parts, 6));
        result.put("kill_message", safeParseInt(parts, 7));
        result.put("spray", safeParseInt(parts, 8));
        result.put("kill_effect", safeParseInt(parts, 9));
        result.put("victory_dance", safeParseInt(parts, 10));
        result.put("title", safeParseInt(parts, 11));

        return result;
    }

    private int safeParseInt(String[] parts, int index) {
        if (index >= parts.length) return 0;
        try {
            return Integer.parseInt(parts[index].trim().split(":")[0]);
        } catch (Exception ex) {
            return 0;
        }
    }

    private boolean tableExists(String tableName) {
        CachedRowSet rs = query("SHOW TABLES LIKE '" + tableName + "'");
        boolean exists = rs != null;
        closeQuietly(rs);
        return exists;
    }

    private void renameLegacyTables() {
        if (tableExists("premium_lostedaccount")) {
            this.update("RENAME TABLE premium_lostedaccount TO premium_lostedaccount_backup");
        }
        if (tableExists("premium_lostedskywars")) {
            this.update("RENAME TABLE premium_lostedskywars TO premium_lostedskywars_backup");
        }
        if (tableExists("ranked_lostedskywars")) {
            this.update("RENAME TABLE ranked_lostedskywars TO ranked_lostedskywars_backup");
        }
        LOGGER.info("Legacy tables renamed to *_backup.");
    }

    private String safeGetString(CachedRowSet rs, String col, String def) {
        try {
            String val = rs.getString(col);
            return val != null ? val : def;
        } catch (Exception ex) {
            return def;
        }
    }

    private int safeGetInt(CachedRowSet rs, String col, int def) {
        try {
            return rs.getInt(col);
        } catch (Exception ex) {
            return def;
        }
    }

    private long safeGetLong(CachedRowSet rs, String col, long def) {
        try {
            return rs.getLong(col);
        } catch (Exception ex) {
            return def;
        }
    }

    private double safeGetDouble(CachedRowSet rs, String col, double def) {
        try {
            return rs.getDouble(col);
        } catch (Exception ex) {
            return def;
        }
    }

    private boolean safeGetBoolean(CachedRowSet rs, String col, boolean def) {
        try {
            return rs.getBoolean(col);
        } catch (Exception ex) {
            return def;
        }
    }

    // =========================================================================
    // PROFILE — now includes elo and brave_points
    // =========================================================================

    @Override
    public Map<String, ValueContainer> loadProfile(UUID uuid, String name) {
        CachedRowSet rs = this.query("SELECT * FROM `profile` WHERE `uuid` = ?", uuid.toString());

        if (rs != null) {
            try {
                if (rs.next()) {
                    Map<String, ValueContainer> map = new LinkedHashMap<>();
                    String oldName = rs.getString("name");
                    if (!oldName.equals(name)) {
                        this.execute("UPDATE `profile` SET `name` = ? WHERE `uuid` = ?", name, uuid.toString());
                    }
                    map.put("coins", new ValueContainer(rs.getInt("coins")));
                    map.put("souls", new ValueContainer(rs.getInt("souls")));
                    map.put("level", new ValueContainer(rs.getInt("level")));
                    map.put("exp", new ValueContainer(rs.getDouble("exp")));
                    map.put("max_souls", new ValueContainer(rs.getInt("max_souls")));
                    map.put("well_roll", new ValueContainer(rs.getInt("well_roll")));
                    map.put("souls_per_win", new ValueContainer(rs.getInt("souls_per_win")));
                    map.put("mystery_dusts", new ValueContainer(rs.getInt("mystery_dusts")));
                    map.put("deliveries", new ValueContainer(rs.getString("deliveries")));
                    map.put("leveling", new ValueContainer(rs.getString("leveling")));
                    map.put("elo", new ValueContainer(rs.getInt("elo")));
                    map.put("brave_points", new ValueContainer(rs.getInt("brave_points")));
                    return map;
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Could not loadProfile(\"" + name + "\"): ", ex);
            } finally {
                closeQuietly(rs);
            }
        }

        Map<String, ValueContainer> defaults = buildDefaultProfile();
        this.execute("INSERT INTO `profile` (uuid, name, coins, souls, level, exp, max_souls, well_roll, souls_per_win, mystery_dusts, last_rank, deliveries, leveling, show_players, show_gore, elo, brave_points) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                uuid.toString(), name, 0, 0, 1, 0.0, 100, 1, 0, 0, "&7", "{}", "[]", true, true, 0, 0);
        return defaults;
    }

    private Map<String, ValueContainer> buildDefaultProfile() {
        Map<String, ValueContainer> map = new LinkedHashMap<>();
        map.put("coins", new ValueContainer(0));
        map.put("souls", new ValueContainer(0));
        map.put("level", new ValueContainer(1));
        map.put("exp", new ValueContainer(0.0));
        map.put("max_souls", new ValueContainer(100));
        map.put("well_roll", new ValueContainer(1));
        map.put("souls_per_win", new ValueContainer(0));
        map.put("mystery_dusts", new ValueContainer(0));
        map.put("deliveries", new ValueContainer("{}"));
        map.put("leveling", new ValueContainer("[]"));
        map.put("elo", new ValueContainer(0));
        map.put("brave_points", new ValueContainer(0));
        return map;
    }

    @Override
    public void saveProfile(UUID uuid, Map<String, ValueContainer> data) {
        this.execute("UPDATE `profile` SET coins = ?, souls = ?, level = ?, exp = ?, max_souls = ?, well_roll = ?, souls_per_win = ?, mystery_dusts = ?, deliveries = ?, leveling = ?, elo = ?, brave_points = ? WHERE uuid = ?",
                data.get("coins").get(), data.get("souls").get(), data.get("level").get(), data.get("exp").get(),
                data.get("max_souls").get(), data.get("well_roll").get(), data.get("souls_per_win").get(),
                data.get("mystery_dusts").get(),
                data.get("deliveries").get(), data.get("leveling").get(),
                data.get("elo").get(), data.get("brave_points").get(),
                uuid.toString());
    }


    // =========================================================================
    // STATS — NO elo/brave_points
    // =========================================================================

    @Override
    public Map<String, ValueContainer> loadStats(UUID uuid, String groupId, String name) {
        CachedRowSet rs = this.query("SELECT * FROM `stats` WHERE `uuid` = ? AND `group_id` = ?", uuid.toString(), groupId);

        if (rs != null) {
            try {
                if (rs.next()) {
                    Map<String, ValueContainer> map = new LinkedHashMap<>();
                    map.put("kills", new ValueContainer(rs.getInt("kills")));
                    map.put("wins", new ValueContainer(rs.getInt("wins")));
                    map.put("assists", new ValueContainer(rs.getInt("assists")));
                    map.put("deaths", new ValueContainer(rs.getInt("deaths")));
                    map.put("plays", new ValueContainer(rs.getInt("plays")));
                    map.put("melee_kills", new ValueContainer(rs.getInt("melee_kills")));
                    map.put("bow_kills", new ValueContainer(rs.getInt("bow_kills")));
                    map.put("mob_kills", new ValueContainer(rs.getInt("mob_kills")));
                    map.put("void_kills", new ValueContainer(rs.getInt("void_kills")));
                    return map;
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Could not loadStats(\"" + name + "\", \"" + groupId + "\"): ", ex);
            } finally {
                closeQuietly(rs);
            }
        }

        Map<String, ValueContainer> defaults = buildDefaultStats();
        this.execute("INSERT INTO `stats` (uuid, group_id, kills, wins, assists, deaths, plays, melee_kills, bow_kills, mob_kills, void_kills) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                uuid.toString(), groupId, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        return defaults;
    }

    private Map<String, ValueContainer> buildDefaultStats() {
        Map<String, ValueContainer> map = new LinkedHashMap<>();
        map.put("kills", new ValueContainer(0));
        map.put("wins", new ValueContainer(0));
        map.put("assists", new ValueContainer(0));
        map.put("deaths", new ValueContainer(0));
        map.put("plays", new ValueContainer(0));
        map.put("melee_kills", new ValueContainer(0));
        map.put("bow_kills", new ValueContainer(0));
        map.put("mob_kills", new ValueContainer(0));
        map.put("void_kills", new ValueContainer(0));
        return map;
    }

    @Override
    public void saveStats(UUID uuid, String groupId, Map<String, ValueContainer> data) {
        this.execute("UPDATE `stats` SET kills = ?, wins = ?, assists = ?, deaths = ?, plays = ?, melee_kills = ?, bow_kills = ?, mob_kills = ?, void_kills = ? WHERE uuid = ? AND group_id = ?",
                data.get("kills").get(), data.get("wins").get(), data.get("assists").get(),
                data.get("deaths").get(), data.get("plays").get(),
                data.get("melee_kills").get(), data.get("bow_kills").get(),
                data.get("mob_kills").get(), data.get("void_kills").get(),
                uuid.toString(), groupId);
    }

    // =========================================================================
    // COSMETICS
    // =========================================================================

    @Override
    public Map<String, ValueContainer> loadCosmetics(UUID uuid, String name) {
        CachedRowSet rs = this.query("SELECT * FROM `cosmetics` WHERE `uuid` = ?", uuid.toString());

        if (rs != null) {
            try {
                if (rs.next()) {
                    Map<String, ValueContainer> map = new LinkedHashMap<>();
                    map.put("kits", new ValueContainer(rs.getString("kits")));
                    map.put("perks", new ValueContainer(rs.getString("perks")));
                    map.put("cages", new ValueContainer(rs.getString("cages")));
                    map.put("death_cries", new ValueContainer(rs.getString("death_cries")));
                    map.put("trails", new ValueContainer(rs.getString("trails")));
                    map.put("balloons", new ValueContainer(rs.getString("balloons")));
                    map.put("kill_messages", new ValueContainer(rs.getString("kill_messages")));
                    map.put("kill_effects", new ValueContainer(rs.getString("kill_effects")));
                    map.put("sprays", new ValueContainer(rs.getString("sprays")));
                    map.put("victory_dances", new ValueContainer(rs.getString("victory_dances")));
                    map.put("titles", new ValueContainer(rs.getString("titles")));
                    map.put("symbols", new ValueContainer(rs.getString("symbols")));
                    return map;
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Could not loadCosmetics(\"" + name + "\"): ", ex);
            } finally {
                closeQuietly(rs);
            }
        }

        Map<String, ValueContainer> defaults = buildDefaultCosmetics();
        this.execute("INSERT INTO `cosmetics` (uuid, kits, perks, cages, death_cries, trails, balloons, kill_messages, kill_effects, sprays, victory_dances, titles, symbols) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                uuid.toString(), "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}");
        return defaults;
    }

    private Map<String, ValueContainer> buildDefaultCosmetics() {
        Map<String, ValueContainer> map = new LinkedHashMap<>();
        map.put("kits", new ValueContainer("{}"));
        map.put("perks", new ValueContainer("{}"));
        map.put("cages", new ValueContainer("{}"));
        map.put("death_cries", new ValueContainer("{}"));
        map.put("trails", new ValueContainer("{}"));
        map.put("balloons", new ValueContainer("{}"));
        map.put("kill_messages", new ValueContainer("{}"));
        map.put("kill_effects", new ValueContainer("{}"));
        map.put("sprays", new ValueContainer("{}"));
        map.put("victory_dances", new ValueContainer("{}"));
        map.put("titles", new ValueContainer("{}"));
        map.put("symbols", new ValueContainer("{}"));
        return map;
    }

    @Override
    public void saveCosmetics(UUID uuid, Map<String, ValueContainer> data) {
        this.execute("UPDATE `cosmetics` SET kits = ?, perks = ?, cages = ?, death_cries = ?, trails = ?, balloons = ?, kill_messages = ?, kill_effects = ?, sprays = ?, victory_dances = ?, titles = ?, symbols = ? WHERE uuid = ?",
                data.get("kits").get(), data.get("perks").get(), data.get("cages").get(),
                data.get("death_cries").get(), data.get("trails").get(), data.get("balloons").get(),
                data.get("kill_messages").get(), data.get("kill_effects").get(), data.get("sprays").get(),
                data.get("victory_dances").get(), data.get("titles").get(), data.get("symbols").get(),
                uuid.toString());
    }

    // =========================================================================
    // SELECTIONS
    // =========================================================================

    @Override
    public Map<String, ValueContainer> loadSelections(UUID uuid, String name) {
        CachedRowSet rs = this.query("SELECT * FROM `selections` WHERE `uuid` = ?", uuid.toString());

        if (rs != null) {
            try {
                if (rs.next()) {
                    Map<String, ValueContainer> map = new LinkedHashMap<>();
                    map.put("kit", new ValueContainer(rs.getString("kit")));
                    map.put("perk", new ValueContainer(rs.getString("perk")));
                    map.put("cage", new ValueContainer(rs.getInt("cage")));
                    map.put("death_cry", new ValueContainer(rs.getInt("death_cry")));
                    map.put("trail", new ValueContainer(rs.getInt("trail")));
                    map.put("balloon", new ValueContainer(rs.getInt("balloon")));
                    map.put("kill_message", new ValueContainer(rs.getInt("kill_message")));
                    map.put("kill_effect", new ValueContainer(rs.getInt("kill_effect")));
                    map.put("spray", new ValueContainer(rs.getInt("spray")));
                    map.put("victory_dance", new ValueContainer(rs.getInt("victory_dance")));
                    map.put("title", new ValueContainer(rs.getInt("title")));
                    map.put("symbol", new ValueContainer(rs.getInt("symbol")));
                    map.put("last_selected", new ValueContainer(rs.getLong("last_selected")));
                    map.put("favorites", new ValueContainer(rs.getString("favorites")));
                    return map;
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Could not loadSelections(\"" + name + "\"): ", ex);
            } finally {
                closeQuietly(rs);
            }
        }

        Map<String, ValueContainer> defaults = buildDefaultSelections();
        this.execute("INSERT INTO `selections` (uuid, kit, perk, cage, death_cry, trail, balloon, kill_message, kill_effect, spray, victory_dance, title, symbol, last_selected, favorites) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                uuid.toString(), "{}", "{}", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0L, "[]");
        return defaults;
    }

    private Map<String, ValueContainer> buildDefaultSelections() {
        Map<String, ValueContainer> map = new LinkedHashMap<>();
        map.put("kit", new ValueContainer("{}"));
        map.put("perk", new ValueContainer("{}"));
        map.put("cage", new ValueContainer(0));
        map.put("death_cry", new ValueContainer(0));
        map.put("trail", new ValueContainer(0));
        map.put("balloon", new ValueContainer(0));
        map.put("kill_message", new ValueContainer(0));
        map.put("kill_effect", new ValueContainer(0));
        map.put("spray", new ValueContainer(0));
        map.put("victory_dance", new ValueContainer(0));
        map.put("title", new ValueContainer(0));
        map.put("symbol", new ValueContainer(0));
        map.put("last_selected", new ValueContainer(0L));
        map.put("favorites", new ValueContainer("[]"));
        return map;
    }

    @Override
    public void saveSelections(UUID uuid, Map<String, ValueContainer> data) {
        this.execute("UPDATE `selections` SET kit = ?, perk = ?, cage = ?, death_cry = ?, trail = ?, balloon = ?, kill_message = ?, kill_effect = ?, spray = ?, victory_dance = ?, title = ?, symbol = ?, last_selected = ?, favorites = ? WHERE uuid = ?",
                data.get("kit").get(), data.get("perk").get(),
                data.get("cage").get(), data.get("death_cry").get(),
                data.get("trail").get(), data.get("balloon").get(),
                data.get("kill_message").get(), data.get("kill_effect").get(),
                data.get("spray").get(), data.get("victory_dance").get(),
                data.get("title").get(), data.get("symbol").get(),
                data.get("last_selected").get(), data.get("favorites").get(),
                uuid.toString());
    }

    // =========================================================================
    // ACCOUNT MANAGEMENT
    // =========================================================================

    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();
    private final Map<UUID, Account> offlineaccounts = new ConcurrentHashMap<>();

    @Override
    public Account loadAccount(UUID id, String name) {
        Account account = accounts.get(id);
        if (account == null) {
            account = new Account(id, name);
            this.accounts.put(id, account);
        }
        return account;
    }

    @Override
    public CompletableFuture<Account> loadAccountOffline(String name) {
        return CompletableFuture.supplyAsync(() -> {
            CachedRowSet rs = query("SELECT * FROM `profile` WHERE LOWER(`name`) = ?",
                    name.toLowerCase());
            if (rs == null) {
                return null;
            }
            try {
                if (!rs.next()) {
                    return null;
                }
                return new Account(UUID.fromString(rs.getString("uuid")), name);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "loadOffline(\"" + name + "\") error: ", ex);
                return null;
            } finally {
                closeQuietly(rs);
            }
        });
    }

    @Override
    public CompletableFuture<Account> getAccountOffline(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Account account = offlineaccounts.get(uuid);
            if (account != null) {
                return account;
            }
            CachedRowSet rs = query("SELECT * FROM `profile` WHERE `uuid` = ?", uuid.toString());
            if (rs == null) {
                return null;
            }
            try {
                if (!rs.next()) {
                    return null;
                }
                account = new Account(uuid, Bukkit.getOfflinePlayer(uuid).getName());
                offlineaccounts.put(uuid, account);
                return account;
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "getAccountOffline(\"" + uuid + "\") error: ", ex);
                return null;
            } finally {
                closeQuietly(rs);
            }
        });
    }

    @Override
    public Account unloadOfflineAccount(UUID uuid) {
        return offlineaccounts.remove(uuid);
    }

    @Override
    public Account unloadAccount(UUID id) {
        return accounts.remove(id);
    }

    @Override
    public Account getAccount(UUID id) {
        return accounts.get(id);
    }

    @Override
    public Account cacheAccount(Account account) {
        if (account == null) return null;
        return accounts.put(account.getUniqueId(), account);
    }

    @Override
    public Account uncacheAccount(UUID id) {
        if (id == null) return null;
        return accounts.remove(id);
    }

    @Override
    public Collection<Account> listAccounts() {
        return ImmutableList.copyOf(accounts.values());
    }

    @Override
    public Collection<Account> listOfflineAccounts() {
        return ImmutableList.copyOf(offlineaccounts.values());
    }

    // =========================================================================
    // SQL UTILITIES
    // =========================================================================

    public void openConnection() {
        try {
            boolean reconnected = this.connection != null;
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + dbname
                            + "?verifyServerCertificate=false&useSSL=false&useUnicode=yes&characterEncoding=UTF-8",
                    username, password);
            LOGGER.info(reconnected ? "Reconnected to MySQL!" : "Connected to MySQL!");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Could not open MySQL connection: ", ex);
        }
    }

    public void closeConnection() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Could not close MySQL connection: ", e);
            }
        }
    }

    public Connection getConnection() {
        if (!isConnected()) {
            this.openConnection();
        }
        return connection;
    }

    public boolean isConnected() {
        try {
            return !(connection == null || connection.isClosed() || !connection.isValid(5));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Could not check MySQL connection: ", ex);
            return false;
        }
    }

    public void update(String sql, Object... vars) {
        PreparedStatement ps = prepareStatement(sql, vars);
        if (ps == null) {
            LOGGER.log(Level.WARNING, "Could not execute SQL (PreparedStatement was null): " + sql);
            return;
        }
        try {
            ps.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not execute SQL (" + sql + "): ", e);
        } finally {
            try {
                ps.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public void execute(String sql, Object... vars) {
        executor.execute(() -> update(sql, vars));
    }

    public PreparedStatement prepareStatement(String query, Object... vars) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            for (int i = 0; i < vars.length; i++) {
                ps.setObject(i + 1, vars[i]);
            }
            return ps;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not Prepare Statement: ", e);
        }
        return null;
    }

    public CachedRowSet query(String query, Object... vars) {
        CachedRowSet rowSet = null;
        try {
            Future<CachedRowSet> future = executor.submit(() -> {
                PreparedStatement ps = prepareStatement(query, vars);
                if (ps == null) {
                    return null;
                }
                try {
                    ResultSet rs = ps.executeQuery();
                    CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                    crs.populate(rs);
                    rs.close();
                    ps.close();
                    if (crs.size() > 0) {
                        crs.beforeFirst();
                        return crs;
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Could not Execute Query: ", e);
                }
                return null;
            });
            rowSet = future.get();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not Call FutureTask: ", e);
        }
        return rowSet;
    }

    private static void closeQuietly(CachedRowSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
