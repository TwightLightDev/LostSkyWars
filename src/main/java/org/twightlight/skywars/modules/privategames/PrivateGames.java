package org.twightlight.skywars.modules.privategames;

import org.bukkit.Bukkit;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.Module;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.privategames.commands.PrivateGamesCommand;
import org.twightlight.skywars.modules.privategames.config.LangConfig;
import org.twightlight.skywars.modules.privategames.database.Storage;
import org.twightlight.skywars.modules.privategames.listeners.PlayerClickInventory;
import org.twightlight.skywars.modules.privategames.listeners.PlayerJoin;
import org.twightlight.skywars.modules.privategames.listeners.PlayerQuitEvent;
import org.twightlight.skywars.modules.privategames.listeners.ServerManagement;

public class PrivateGames extends Module {

    private static Storage storage;
    private static YamlWrapper lang;

    public PrivateGames() {
        super("PrivateGames");
        initListeners();
        initDatabase();
        initCommands();
        initConfig();
        LOGGER.log(Logger.Level.INFO, "PrivateGames module has been successfully loaded!");

    }

    public static void disable() {
        storage.getUsers().clear();
        storage = null;

        lang = null;
    }


    private void initListeners() {
        LOGGER.log(Logger.Level.INFO, "Loading Listeners...");

        Bukkit.getPluginManager().registerEvents(new ServerManagement(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerClickInventory(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitEvent(), SkyWars.getInstance());

    }

    private void initDatabase() {
        LOGGER.log(Logger.Level.INFO, "Loading Database...");

        storage = new Storage();
    }

    private void initCommands() {
        LOGGER.log(Logger.Level.INFO, "Loading Commands...");

        new PrivateGamesCommand();
    }

    public static Storage getStorage() {
        return storage;
    }

    private void initConfig() {
        LOGGER.log(Logger.Level.INFO, "Loading Configs...");

        lang = new LangConfig(getPlugin(), "language", getPlugin().getDataFolder().getPath() + "/modules/privategames");
    }

    public static YamlWrapper getLanguage() {
        return lang;
    }
}
