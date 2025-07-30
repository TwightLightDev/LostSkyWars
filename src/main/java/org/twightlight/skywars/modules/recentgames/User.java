package org.twightlight.skywars.modules.recentgames;

import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.util.*;

public class User {
    private Player p;

    private static Map<Player, User> userMap = new HashMap<>();
    public User(Player p) {
        this.p = p;
        userMap.put(p, this);
    }

    public Player getPlayer() {
        return p;
    }

    public static User getUser(Player p) {
        return userMap.getOrDefault(p, null);
    }

    public List<GameData> getData() {
        return RecentGames.getDatabase().getData(p, "games", new TypeToken<List<GameData>>() {}, Collections.emptyList());
    }

    public void addGame(GameData data, int cap) {
        List<GameData> oldData = getData();
        List<GameData> newData = new ArrayList<>(cap);

        newData.add(data);
        for (GameData game : oldData) {
            if (newData.size() >= cap) break;
            newData.add(game);
        }

        RecentGames.getDatabase().updateData(p, newData, "games");
    }
}
