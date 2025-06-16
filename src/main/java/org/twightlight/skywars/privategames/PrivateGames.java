package org.twightlight.skywars.privategames;

import org.bukkit.Bukkit;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.privategames.database.Storage;
import org.twightlight.skywars.privategames.listeners.PlayerJoin;
import org.twightlight.skywars.privategames.listeners.ServerManagement;

public class PrivateGames {

    private static Storage storage;

    public static void setupPrivateGames() {
        initListeners();
        initDatabase();
        initCommands();
    }

    private static void initListeners() {
        Bukkit.getPluginManager().registerEvents(new ServerManagement(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), Main.getInstance());

    }

    private static void initDatabase() {
        storage = new Storage();
    }

    private static void initCommands() {}

    public static Storage getStorage() {
        return storage;
    }
}
