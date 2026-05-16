package org.twightlight.skywars.modules.lobbysettings;

import org.bukkit.Bukkit;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.modules.Module;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.lobbysettings.commands.LobbySettingsCommand;
import org.twightlight.skywars.modules.lobbysettings.config.LangConfig;
import org.twightlight.skywars.modules.lobbysettings.database.SQLite;
import org.twightlight.skywars.modules.lobbysettings.listeners.ParticleReceiveEvent;
import org.twightlight.skywars.modules.lobbysettings.listeners.PlayerJoinEvent;
import org.twightlight.skywars.modules.lobbysettings.listeners.PlayerQuitEvent;
import org.twightlight.skywars.modules.lobbysettings.listeners.WorldChangeEvent;
import org.twightlight.skywars.modules.lobbysettings.papi.PlaceholderAPI;

public class LobbySettings extends Module {
    private static SQLite storage;
    private static YamlWrapper lang;
    private static LobbySettingsCommand commandManager;
    private static LobbySettings instance;

    public LobbySettings() {
        super("LobbySettings");
        initListeners();
        initDatabase();
        initCommands();
        initConfig();
        new PlaceholderAPI().register();
        instance = this;
        LOGGER.log(Logger.Level.INFO, "LobbySettings module has been successfully loaded!");

    }

    public static LobbySettings getInstance() {
        return instance;
    }

    public static void disable() {
        storage = null;

        lang = null;

        commandManager = null;

        User.getUsers().clear();
    }


    private void initListeners() {
        LOGGER.log(Logger.Level.INFO, "Loading Listeners...");

        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new WorldChangeEvent(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitEvent(), getPlugin());

        ParticleReceiveEvent.init();
    }

    private void initDatabase() {
        LOGGER.log(Logger.Level.INFO, "Loading Database...");

        storage = new SQLite(getPlugin(), "lobbysettings");
    }

    private void initCommands() {
        LOGGER.log(Logger.Level.INFO, "Loading Commands...");

        commandManager = new LobbySettingsCommand();
    }

    public static SQLite getDatabase() {
        return storage;
    }

    private void initConfig() {
        LOGGER.log(Logger.Level.INFO, "Loading Configs...");
        lang = new LangConfig(getPlugin(), "language", getPlugin().getDataFolder().getPath() + "/modules/lobbysettings");
    }

    public static YamlWrapper getLanguage() {
        return lang;
    }

    public static LobbySettingsCommand getCommandManager() {
        return commandManager;
    }
}
