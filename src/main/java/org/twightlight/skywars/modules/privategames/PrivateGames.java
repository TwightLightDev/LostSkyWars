package org.twightlight.skywars.modules.privategames;

import org.bukkit.Bukkit;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.modules.privategames.database.Storage;
import org.twightlight.skywars.modules.privategames.listeners.PlayerClickInventory;
import org.twightlight.skywars.modules.privategames.listeners.PlayerJoin;
import org.twightlight.skywars.modules.privategames.listeners.ServerManagement;

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
        Bukkit.getPluginManager().registerEvents(new PlayerClickInventory(), Main.getInstance());

    }

    private static void initDatabase() {
        storage = new Storage();
    }

    private static void initCommands() {}

    public static Storage getStorage() {
        return storage;
    }
}
