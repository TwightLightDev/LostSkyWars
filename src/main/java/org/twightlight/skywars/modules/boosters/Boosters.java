package org.twightlight.skywars.modules.boosters;

import org.twightlight.skywars.modules.Modules;
import org.twightlight.skywars.modules.boosters.config.MainConfig;
import org.twightlight.skywars.modules.libs.yaml.YamlWrapper;
import org.twightlight.skywars.modules.boosters.config.LangConfig;
import org.twightlight.skywars.modules.boosters.database.SQLite;
import org.twightlight.skywars.modules.boosters.listeners.PlayerJoin;
import org.twightlight.skywars.modules.boosters.listeners.SkyWars;

public class Boosters extends Modules {
    private static SQLite database;
    private static YamlWrapper lang;
    private static YamlWrapper config;
    private static Boosters instance;

    public Boosters() {
        super();
        instance = this;
        initListeners();
        initDatabase();
        initCommands();
        initConfig();
        initServerManager();
    }

    public static Boosters getInstance() {
        return instance;
    }


    private void initListeners() {
        this.registerEvents(new PlayerJoin());
        this.registerEvents(new SkyWars());
    }

    private void initDatabase() {
        database = new SQLite(getPlugin(), "boosters");
        database.createServerData();
    }

    private void initCommands() {

    }

    public static SQLite getDatabase() {
        return database;
    }

    private static void initServerManager() {
        ServerManager.init();
    }

    private void initConfig() {
        lang = new LangConfig(getPlugin(), "language", getPlugin().getDataFolder().getPath() + "/modules/boosters");
        config = new MainConfig(getPlugin(), "config", getPlugin().getDataFolder().getPath() + "/modules/boosters");
    }

    public static YamlWrapper getLanguage() {
        return lang;
    }

    public static YamlWrapper getConfig() {
        return config;
    }
}
