package org.twightlight.skywars.modules.boosters;

import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.modules.Module;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.commands.BoostersCommand;
import org.twightlight.skywars.modules.boosters.config.BoostersConfig;
import org.twightlight.skywars.modules.boosters.config.LangConfig;
import org.twightlight.skywars.modules.boosters.config.MainConfig;
import org.twightlight.skywars.modules.boosters.config.MenuConfig;
import org.twightlight.skywars.modules.boosters.database.SQLite;
import org.twightlight.skywars.modules.boosters.listeners.PlayerClickInventory;
import org.twightlight.skywars.modules.boosters.listeners.PlayerJoinEvent;
import org.twightlight.skywars.modules.boosters.listeners.PlayerQuitEvent;
import org.twightlight.skywars.modules.boosters.listeners.SkyWars;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.ServerUser;

public class Boosters extends Module {
    private static SQLite database;
    private static YamlWrapper lang;
    private static YamlWrapper config;
    private static YamlWrapper bconfig;
    private static YamlWrapper menu;
    private static Boosters instance;
    private static BoostersCommand commandManager;
    public Boosters() {
        super("Boosters");
        instance = this;
        initConfig();
        initListeners();
        initDatabase();
        initCommands();
        initServerUser();
        BoosterManager.init();
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
        this.registerEvents(new PlayerClickInventory());

    }

    private void initDatabase() {
        LOGGER.log(Logger.Level.INFO, "Loading Database...");
        database = new SQLite(getPlugin(), "boosters");
        database.createNetworkData();
    }

    private void initCommands() {
        LOGGER.log(Logger.Level.INFO, "Loading Commands...");
        commandManager = new BoostersCommand();
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
        menu = new MenuConfig(getPlugin(), "menu", getPlugin().getDataFolder().getPath() + "/modules/boosters", "boosters");
        menu.reload();
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

    public static BoostersCommand getCommandManager() {
        return commandManager;
    }

    public static YamlWrapper getMenuConfig() {
        return menu;
    }

}
