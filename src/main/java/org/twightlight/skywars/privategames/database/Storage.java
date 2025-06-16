package org.twightlight.skywars.privategames.database;

import org.bukkit.entity.Player;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.privategames.PrivateGamesUser;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private Map<Player, PrivateGamesUser> users = new HashMap<>();
    private SQLite database;

    public Storage() {
        database = new SQLite(Main.getInstance(), "privategames");
    }

    public PrivateGamesUser getUser(Player p) {
        return users.getOrDefault(p, null);
    }

    public void addUser(Player p, PrivateGamesUser u) {
        users.put(p, u);
    }

    public SQLite getDatabase() {
        return database;
    }
}
