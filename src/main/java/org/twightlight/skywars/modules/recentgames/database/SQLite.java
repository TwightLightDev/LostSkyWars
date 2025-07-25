package org.twightlight.skywars.modules.recentgames.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;


public class SQLite {
    private Connection connection;
    private Plugin plugin;
    private String dbname;

    public SQLite(Plugin plugin, String name) {
        this.plugin = plugin;
        this.dbname = name;
        connect();
    }

    public void connect() {
        this.connection = getConnection();
        try {
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(" CREATE TABLE IF NOT EXISTS " + dbname + " ( player TEXT PRIMARY KEY, games TEXT DEFAULT ''); ");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        File modules = new File(plugin.getDataFolder().getPath() + "/modules/recentgames");
        if (!modules.exists()) {
            modules.mkdirs();
        }
        File dataFile = new File(plugin.getDataFolder().getPath() + "/modules/recentgames", dbname + ".db");
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

    public void createPlayerData(OfflinePlayer p) {
        try (Connection connection = getConnection();
             PreparedStatement checkPs = connection.prepareStatement("SELECT player FROM " + dbname + " WHERE player = ?");
             PreparedStatement insertPs = connection.prepareStatement("INSERT INTO " + dbname + " (player, games) VALUES (?, ?)")) {

            checkPs.setString(1, p.getUniqueId().toString());
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                insertPs.setString(1, p.getUniqueId().toString());
                insertPs.setString(2, "[]");
                insertPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(OfflinePlayer player, String column, TypeToken<T> typeToken, T fallback) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM " + dbname + " WHERE player = ?")) {

            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getObject(column) == null) {
                    return fallback;
                }

                Type type = typeToken.getType();

                if (type == Integer.class || type == int.class) {
                    return (T) Integer.valueOf(rs.getInt(column));
                } else if (type == Double.class || type == double.class) {
                    return (T) Double.valueOf(rs.getDouble(column));
                } else if (type == String.class) {
                    return (T) rs.getString(column);
                } else {
                    String json = rs.getString(column);
                    try {
                        Gson gson = new Gson();
                        return gson.fromJson(json, type);
                    } catch (JsonSyntaxException e) {
                        Bukkit.getLogger().warning("Invalid JSON in database for " + column + ": " + json);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fallback;
    }


    public <T> boolean pullData(OfflinePlayer player, T data, String column) {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE " + dbname + " SET " + column + "=? WHERE player=?")) {

            if (data instanceof Integer || data instanceof Double) {
                ps.setObject(1, data);
            } else if (data instanceof String) {
                ps.setString(1, (String) data);
            } else {
                ps.setString(1, new Gson().toJson(data));
            }

            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean addColumn(String columnName, String columnType, String defaultValue) {

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            if (!columnExists(columnName)) {

                String statement = "ALTER TABLE " + dbname + " ADD COLUMN " + columnName + " " + columnType +
                        (defaultValue != null ? " DEFAULT " + defaultValue : "") + ";";
                stmt.executeUpdate(statement);
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            return false;
        }
    }

    private boolean columnExists(String columnName) throws SQLException {
        Connection conn = getConnection();
        String query = "PRAGMA table_info(" + dbname + ")";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
