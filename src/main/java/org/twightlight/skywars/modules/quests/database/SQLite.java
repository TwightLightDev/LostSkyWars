package org.twightlight.skywars.modules.quests.database;

import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;


public class SQLite {
    private Connection connection;
    private Plugin plugin;
    private String dbname;
    private String questsrefreshing;
    private String profilesdbname;
    private SQLHelper helper;
    public SQLite(Plugin plugin, String name) {
        this.plugin = plugin;
        this.dbname = name;
        this.profilesdbname = name + "_profiles";
        this.questsrefreshing = "refreshing_" + name;
        this.helper = new SQLHelper(name);
        connect();
    }

    public SQLHelper getSQLHelper() {
        return helper;
    }

    public void connect() {
        this.connection = getConnection();
        try {
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(" CREATE TABLE IF NOT EXISTS "
                    + dbname +
                    " ( " +
                    "player TEXT PRIMARY KEY, " +
                    "questsdata TEXT DEFAULT '{}', " +
                    "challengesdata TEXT DEFAULT '{}'" +
                    "); ");

            statement.executeUpdate(" CREATE TABLE IF NOT EXISTS "
                    + profilesdbname +
                    " ( " +
                    "player TEXT PRIMARY KEY, " +
                    "completedquests INTEGER DEFAULT 0, " +
                    "completedchallenges INTEGER DEFAULT 0, " +
                    "autoaccept INTEGER DEFAULT 0, " +
                    "challengesleft INTEGER DEFAULT 0, " +
                    "nextchallengesrefresh TEXT DEFAULT ''" +
                    "); ");

            statement.executeUpdate(" CREATE TABLE IF NOT EXISTS "
                    + questsrefreshing +
                    " ( " +
                    "quest_id TEXT PRIMARY KEY, " +
                    "next_refresh TEXT DEFAULT '0'" +
                    "); ");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        File modules = new File(plugin.getDataFolder().getPath() + "/modules/quests");
        if (!modules.exists()) {
            modules.mkdirs();
        }
        File dataFile = new File(plugin.getDataFolder().getPath() + "/modules/quests", dbname + ".db");
        if (!dataFile.exists())
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        try {
            if (this.connection != null && !this.connection.isClosed())
                return this.connection;
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
            return this.connection;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}

