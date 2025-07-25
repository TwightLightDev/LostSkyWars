package org.twightlight.skywars.modules.privategames;

import org.bukkit.Bukkit;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.Modules;
import org.twightlight.skywars.modules.privategames.commands.PrivateGamesCommand;
import org.twightlight.skywars.modules.privategames.database.Storage;
import org.twightlight.skywars.modules.privategames.listeners.PlayerClickInventory;
import org.twightlight.skywars.modules.privategames.listeners.PlayerJoin;
import org.twightlight.skywars.modules.privategames.listeners.ServerManagement;

public class PrivateGames extends Modules {

    private static Storage storage;

    public PrivateGames() {
        initListeners();
        initDatabase();
        initCommands();
    }


    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new ServerManagement(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerClickInventory(), SkyWars.getInstance());

    }

    private void initDatabase() {
        storage = new Storage();
    }

    private void initCommands() {
        new PrivateGamesCommand();
    }

    public static Storage getStorage() {
        return storage;
    }
}
