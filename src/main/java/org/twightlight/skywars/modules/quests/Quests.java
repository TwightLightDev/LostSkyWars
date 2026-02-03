package org.twightlight.skywars.modules.quests;

import org.twightlight.skywars.Logger;
import org.twightlight.skywars.modules.Module;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.boosters.commands.BoostersCommand;
import org.twightlight.skywars.modules.quests.commands.QuestsCommand;
import org.twightlight.skywars.modules.quests.config.LangConfig;
import org.twightlight.skywars.modules.quests.config.MenuConfig;
import org.twightlight.skywars.modules.quests.config.QuestsConfig;
import org.twightlight.skywars.modules.quests.database.SQLite;
import org.twightlight.skywars.modules.quests.listeners.PlayerClickInventory;
import org.twightlight.skywars.modules.quests.listeners.PlayerJoinEvent;
import org.twightlight.skywars.modules.quests.listeners.PlayerQuitEvent;
import org.twightlight.skywars.modules.quests.listeners.skywars.SkyWarsGameEndEvent;
import org.twightlight.skywars.modules.quests.managers.ChallengesManager;
import org.twightlight.skywars.modules.quests.managers.QuestsManager;
import org.twightlight.skywars.modules.quests.managers.TickingManager;

public class Quests extends Module {

    private QuestsManager questsManager;
    private ChallengesManager challengesManager;
    private SQLite sqLite;
    private TickingManager tickingManager;
    private QuestsCommand commandManager;
    private YamlWrapper questsConfig;
    private YamlWrapper mainConfig;
    private YamlWrapper langConfig;
    private YamlWrapper menuConfig;
    private static Quests instance;

    public Quests() {
        super("Quests");
        instance = this;
        setupConfigs();
        setupDatabase();
        setupManagers();
        setupListeners();
        tickingManager = new TickingManager();
        LOGGER.log(Logger.Level.INFO, "Ticking service started!");
        LOGGER.log(Logger.Level.INFO, "Loading Commands...");
        commandManager = new QuestsCommand();
    }

    public TickingManager getTickingManager() {
        return tickingManager;
    }

    public static Quests getInstance() {
        return instance;
    }

    private void setupConfigs() {
        questsConfig = new QuestsConfig(getPlugin(), "quests", getPlugin().getDataFolder().getPath() + "/modules/quests");
        langConfig = new LangConfig(getPlugin(), "language", getPlugin().getDataFolder().getPath() + "/modules/quests");
        menuConfig = new MenuConfig(getPlugin(), "menu", getPlugin().getDataFolder().getPath() + "/modules/quests", "quests");
        menuConfig.reload();
    }

    private void setupManagers() {
        questsManager = new QuestsManager();
        LOGGER.log(Logger.Level.INFO, "Quests manager started!");
        challengesManager = new ChallengesManager();
        LOGGER.log(Logger.Level.INFO, "Challenges manager started!");

    }

    private void setupDatabase() {
        LOGGER.log(Logger.Level.INFO, "Loading database...");
        sqLite = new SQLite(getPlugin(), "quests");
    }

    private void setupListeners() {
        registerEvents(new PlayerJoinEvent());
        registerEvents(new PlayerQuitEvent());
        registerEvents(new SkyWarsGameEndEvent());
        registerEvents(new PlayerClickInventory());
    }

    public QuestsManager getQuestsManager() {
        return questsManager;
    }

    public ChallengesManager getChallengesManager() {
        return challengesManager;
    }

    public SQLite getDatabase() {
        return sqLite;
    }

    public YamlWrapper getLangConfig() {
        return langConfig;
    }

    public YamlWrapper getMainConfig() {
        return mainConfig;
    }

    public YamlWrapper getMenuConfig() {
        return menuConfig;
    }

    public YamlWrapper getQuestsConfig() {
        return questsConfig;
    }

    public QuestsCommand getCommandManager() {
        return commandManager;
    }
}
