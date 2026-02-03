package org.twightlight.skywars.modules.privategames.database;

import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.privategames.User;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private Map<Player, User> users = new HashMap<>();
    private SQLite database;

    public Storage() {
        database = new SQLite(SkyWars.getInstance(), "privategames");
    }

    public User getUser(Player p) {
        return users.getOrDefault(p, null);
    }

    public void addUser(Player p, User u) {
        users.put(p, u);
    }

    public SQLite getDatabase() {
        return database;
    }

    public User removeUser(User user) {
        return users.remove(user.getPlayer());
    }

    public Map<Player, User> getUsers() {
        return users;
    }
}
