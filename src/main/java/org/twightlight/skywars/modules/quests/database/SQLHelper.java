package org.twightlight.skywars.modules.quests.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.quests.User;
import org.twightlight.skywars.modules.quests.quests.Quest;

import java.lang.reflect.Type;
import java.sql.*;

public class SQLHelper {

    private String dbname;
    private String questsrefreshing;
    private String profilesdbname;
    private Gson GSON = new Gson();

    public SQLHelper(String name) {
        this.dbname = name;
        this.profilesdbname = name + "_profiles";
        this.questsrefreshing = "refreshing_" + name;
    }

    public Connection getConnection() {
        return Quests.getInstance().getDatabase().getConnection();
    }

    public void createPlayerData(OfflinePlayer p) {
        try (Connection connection = getConnection();
             PreparedStatement checkPs = connection.prepareStatement("SELECT player FROM " + dbname + " WHERE player = ?");
             PreparedStatement insertPs = connection.prepareStatement("INSERT INTO " + dbname +
                     " (player, questsdata, challengesdata) VALUES (?, ?, ?)")) {

            checkPs.setString(1, p.getUniqueId().toString());
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                insertPs.setString(1, p.getUniqueId().toString());
                insertPs.setString(2, "{}");
                insertPs.setString(3, "{}");

                insertPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = getConnection();
             PreparedStatement checkPs = connection.prepareStatement("SELECT player FROM " + profilesdbname + " WHERE player = ?");
             PreparedStatement insertPs = connection.prepareStatement("INSERT INTO " + profilesdbname +
                     " (player, completedquests, completedchallenges, autoaccept, challengesleft, nextchallengesrefresh) VALUES (?, ?, ?, ?, ?, ?)")) {

            checkPs.setString(1, p.getUniqueId().toString());
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                insertPs.setString(1, p.getUniqueId().toString());
                insertPs.setInt(2, 0);
                insertPs.setInt(3, 0);
                insertPs.setInt(4, 0);
                insertPs.setInt(5, 0);
                insertPs.setInt(6, '0');

                insertPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createQuestData(Quest quest) {
        try (Connection connection = getConnection();
             PreparedStatement checkPs = connection.prepareStatement("SELECT quest_id FROM " + questsrefreshing + " WHERE quest_id = ?");
             PreparedStatement insertPs = connection.prepareStatement("INSERT INTO " + questsrefreshing +
                     " (quest_id, next_refresh) VALUES (?, ?)")) {

            checkPs.setString(1, quest.getId());
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                insertPs.setString(1, quest.getId());
                insertPs.setString(2, "0");

                insertPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String table, String selectKey, String selectValue, String column, TypeToken<T> typeToken, T fallback) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " +selectKey+ " = ?")) {

            ps.setString(1, selectValue);
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


    public <T> boolean updateData(String table, String selectKey, String selectValue, T data, String column) {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE " + table + " SET " + column + "=? WHERE "+selectKey+"=?")) {

            if (data instanceof Integer || data instanceof Double) {
                ps.setObject(1, data);
            } else if (data instanceof String) {
                ps.setString(1, (String) data);
            } else {
                ps.setString(1, new Gson().toJson(data));
            }

            ps.setString(2, selectValue);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean addColumn(String table, String columnName, String columnType, String defaultValue) {

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            if (!columnExists(table, columnName)) {

                String statement = "ALTER TABLE " + table + " ADD COLUMN " + columnName + " " + columnType +
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

    private boolean columnExists(String table, String columnName) throws SQLException {
        Connection conn = getConnection();
        String query = "PRAGMA table_info(" + table + ")";
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


    public long getNextRefresh(Quest quest) {
        String s = getData(questsrefreshing, "quest_id", quest.getId(), "next_refresh", new TypeToken<String>() {
        }, "0");
        return Long.parseLong(s);
    }

    public void setNextRefresh(Quest quest, long value) {
        updateData(questsrefreshing, "quest_id", quest.getId(), value + "", "next_refresh");
    }

    public void setAutoAccept(User user, boolean autoaccept) {
        int i = autoaccept ? 1 : 0;

        updateData(profilesdbname, "player", user.getPlayer().getUniqueId().toString(), i, "autoaccept");
    }

    public String getProgressDBName() {
        return dbname;
    }

    public String getProfilesDBName() {
        return profilesdbname;
    }

    public String getQuestsRefreshingDBName() {
        return questsrefreshing;
    }
}
