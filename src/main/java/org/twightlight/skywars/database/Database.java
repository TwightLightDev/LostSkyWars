package org.twightlight.skywars.database;

import org.twightlight.skywars.database.providers.MySQLDatabase;
import org.twightlight.skywars.database.providers.SQLiteDatabase;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.bungee.core.Core;
import org.twightlight.skywars.bungee.core.CoreDatabase;
import org.twightlight.skywars.database.player.ValueContainer;
import org.twightlight.skywars.player.Account;

import javax.sql.rowset.CachedRowSet;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class Database {

    /**
     * Loads profile data for a player (includes elo and brave_points).
     */
    public abstract Map<String, ValueContainer> loadProfile(UUID uuid, String name);

    /**
     * Saves profile data for a player (includes elo and brave_points).
     */
    public abstract void saveProfile(UUID uuid, Map<String, ValueContainer> data);

    /**
     * Loads stats for a player in a specific group (no elo/brave_points).
     */
    public abstract Map<String, ValueContainer> loadStats(UUID uuid, String groupId, String name);

    /**
     * Saves stats for a player in a specific group (no elo/brave_points).
     */
    public abstract void saveStats(UUID uuid, String groupId, Map<String, ValueContainer> data);

    /**
     * Loads cosmetic ownership data for a player.
     */
    public abstract Map<String, ValueContainer> loadCosmetics(UUID uuid, String name);

    /**
     * Saves cosmetic ownership data for a player.
     */
    public abstract void saveCosmetics(UUID uuid, Map<String, ValueContainer> data);

    /**
     * Loads selection data for a player.
     */
    public abstract Map<String, ValueContainer> loadSelections(UUID uuid, String name);

    /**
     * Saves selection data for a player.
     */
    public abstract void saveSelections(UUID uuid, Map<String, ValueContainer> data);

    /**
     * Loads an account from cache or creates it.
     */
    public abstract Account loadAccount(UUID id, String name);

    public abstract CompletableFuture<Account> loadAccountOffline(String name);

    public abstract CompletableFuture<Account> getAccountOffline(UUID uuid);

    public abstract Account unloadOfflineAccount(UUID uuid);

    public abstract Account unloadAccount(UUID id);

    public abstract Account getAccount(UUID id);

    public abstract Collection<Account> listAccounts();

    public abstract Collection<Account> listOfflineAccounts();

    public abstract CachedRowSet query(String query, Object... vars);

    public abstract Account cacheAccount(Account account);

    public abstract Account uncacheAccount(UUID id);

    /**
     * Runs data migration from old tables to new tables.
     */
    public abstract void migrateFromLegacy();

    private static Database instance;
    public static final Logger LOGGER = Core.getCoreLogger().getModule("Database");

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
