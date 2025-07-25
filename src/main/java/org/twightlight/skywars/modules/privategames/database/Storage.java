package org.twightlight.skywars.modules.privategames.database;

import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.privategames.PrivateGamesUser;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private Map<Player, PrivateGamesUser> users = new HashMap<>();
    private SQLite database;

    public Storage() {
        database = new SQLite(SkyWars.getInstance(), "privategames");
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
