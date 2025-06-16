package org.twightlight.skywars.database;

import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreDatabase;
import org.twightlight.skywars.database.player.StatsContainer;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.LostLogger;

import javax.sql.rowset.CachedRowSet;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public abstract class Database {

    public abstract Map<String, StatsContainer> loadStats(UUID id, String table, String name);

    public abstract void saveStats(UUID id, String table, Map<String, StatsContainer> map);

    public abstract Account loadAccount(UUID id, String name);

    public abstract Account loadOffline(String name);

    public abstract Account unloadAccount(UUID id);

    public abstract Account getAccount(UUID id);

    public abstract Collection<Account> listAccounts();

    public abstract CachedRowSet query(String query, Object... vars);

    private static Database instance;
    public static final LostLogger LOGGER = Core.getCoreLogger().getModule("Database");

    public static void setupDatabase() {
        CoreDatabase config = Core.getCoreDatabase();
        String type = config.getName();
        if (type.equalsIgnoreCase("MySQL")) {
            instance = new MySQLDatabase();
        } else {
            instance = new SQLiteDatabase();
        }

        try {
            Class.forName("org.bukkit.Bukkit");
            BukkitLoader.start();
        } catch (Exception ex) {
        }
    }

    public static Database getInstance() {
        return instance;
    }
}
