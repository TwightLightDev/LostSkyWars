package org.twightlight.skywars.modules.boosters;

import org.twightlight.skywars.modules.Module;
import org.twightlight.skywars.modules.boosters.commands.BoostersCommand;
import org.twightlight.skywars.modules.boosters.config.BoostersConfig;
import org.twightlight.skywars.modules.boosters.config.MainConfig;
import org.twightlight.skywars.modules.boosters.listeners.PlayerQuitEvent;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.ServerUser;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.boosters.config.LangConfig;
import org.twightlight.skywars.modules.boosters.database.SQLite;
import org.twightlight.skywars.modules.boosters.listeners.PlayerJoinEvent;
import org.twightlight.skywars.modules.boosters.listeners.SkyWars;
import org.twightlight.skywars.Logger;

public class Boosters extends Module {
    private static SQLite database;
    private static YamlWrapper lang;
    private static YamlWrapper config;
    private static YamlWrapper bconfig;

    private static Boosters instance;
    private BoostersCommand commandManager;
    public Boosters() {
        super("Boosters");
        instance = this;
        initConfig();
        initListeners();
        initDatabase();
        initCommands();
        initServerUser();
        LOGGER.log(Logger.Level.INFO, "Boosters module has been successfully loaded!");

    }

    public static void disable() {
        database = null;
        lang = null;
        config = null;
        bconfig = null;
        ServerUser.remove();
        PlayerUser.getUsers().clear();
        instance = null;
    }

    public static Boosters getInstance() {
        return instance;
    }


    private void initListeners() {
        LOGGER.log(Logger.Level.INFO, "Loading Listeners...");
        this.registerEvents(new PlayerJoinEvent());
        this.registerEvents(new SkyWars());
        this.registerEvents(new PlayerQuitEvent());
    }

    private void initDatabase() {
        LOGGER.log(Logger.Level.INFO, "Loading Database...");
        database = new SQLite(getPlugin(), "boosters");
        database.createNetworkData();
    }

    private void initCommands() {
        LOGGER.log(Logger.Level.INFO, "Loading Commands...");

    }

    public static SQLite getDatabase() {
        return database;
    }

    private static void initServerUser() {
        ServerUser.init();
    }

    private void initConfig() {
        LOGGER.log(Logger.Level.INFO, "Loading Configs...");
        lang = new LangConfig(getPlugin(), "language", getPlugin().getDataFolder().getPath() + "/modules/boosters");
        config = new MainConfig(getPlugin(), "config", getPlugin().getDataFolder().getPath() + "/modules/boosters");
        bconfig = new BoostersConfig(getPlugin(), "boosters", getPlugin().getDataFolder().getPath() + "/modules/boosters");
    }

    public static YamlWrapper getLanguage() {
        return lang;
    }

    public static YamlWrapper getConfig() {
        return config;
    }

    public static YamlWrapper getBoostersConfig() {
        return bconfig;
    }

    public BoostersCommand getCommandManager() {
        return commandManager;
    }
}
