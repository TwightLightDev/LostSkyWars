package org.twightlight.skywars.modules.lobbysettings;

import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.Modules;
import org.twightlight.skywars.modules.libs.yaml.YamlWrapper;
import org.twightlight.skywars.modules.lobbysettings.commands.LobbySettingsCommand;
import org.twightlight.skywars.modules.lobbysettings.config.LangConfig;
import org.twightlight.skywars.modules.lobbysettings.database.SQLite;
import org.twightlight.skywars.modules.lobbysettings.listeners.ParticleSentEvent;
import org.twightlight.skywars.modules.lobbysettings.listeners.PlayerJoinEvent;
import org.twightlight.skywars.modules.lobbysettings.listeners.WorldChangeEvent;
import org.twightlight.skywars.modules.lobbysettings.papi.PlaceholderAPI;

public class LobbySettings extends Modules {
    private static SQLite storage;
    private static YamlWrapper lang;

    public LobbySettings() {
        super();
        initListeners();
        initDatabase();
        initCommands();
        initConfig();
        new PlaceholderAPI().register();
    }


    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new WorldChangeEvent(), getPlugin());
        ParticleSentEvent.init();
    }

    private void initDatabase() {
        storage = new SQLite(getPlugin(), "lobbysettings");
    }

    private void initCommands() {
        new LobbySettingsCommand();
    }

    public static SQLite getDatabase() {
        return storage;
    }

    private void initConfig() {
        lang = new LangConfig(getPlugin(), "language", getPlugin().getDataFolder().getPath() + "/modules/lobbysettings");
    }

    public static YamlWrapper getLanguage() {
        return lang;
    }
}
