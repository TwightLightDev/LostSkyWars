package org.twightlight.skywars.modules.recentgames;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class User {
    private UUID uuid;

    private GameData viewingGame = null;

    private static Map<UUID, User> userMap = new HashMap<>();

    public User(Player p) {
        this.uuid = p.getUniqueId();
        userMap.put(uuid, this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public static User getUser(Player p) {
        return userMap.getOrDefault(p.getUniqueId(), null);
    }

    public List<GameData> getData() {
        return RecentGames.getDatabase().getData(getPlayer(), "games", new TypeToken<List<GameData>>() {}, Collections.emptyList());
    }

    public void addGame(GameData data, int cap) {
        List<GameData> oldData = getData();
        List<GameData> newData = new ArrayList<>(cap);

        newData.add(data);
        for (GameData game : oldData) {
            if (newData.size() >= cap) break;
            newData.add(game);
        }

        RecentGames.getDatabase().updateData(getPlayer(), newData, "games");
    }

    public void setViewingGame(GameData data) {
        viewingGame = data;
    }

    public GameData getViewingGame() {
        return viewingGame;
    }

    public static User removeUser(User user) {
        return userMap.remove(user.uuid);
    }

    public static Map<UUID, User> getUsers() {
        return userMap;
    }
}
