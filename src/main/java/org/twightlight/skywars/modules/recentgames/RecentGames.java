package org.twightlight.skywars.modules.recentgames;

import org.bukkit.Bukkit;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.Modules;
import org.twightlight.skywars.modules.recentgames.commands.RecentGamesCommand;
import org.twightlight.skywars.modules.recentgames.database.SQLite;
import org.twightlight.skywars.modules.recentgames.hook.ReplayHook;
import org.twightlight.skywars.modules.recentgames.listeners.*;
import org.twightlight.skywars.utils.Logger;

public class RecentGames extends Modules {

    private static SQLite database;
    private static ReplayHook replayHook = null;
    public static Logger LOGGER = new Logger();

    public RecentGames() {
        super();
        initHooks();
        initListeners();
        initDatabase();
        initCommands();
    }

    private static void initListeners() {
        Bukkit.getPluginManager().registerEvents(new GameEndEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new GameStartEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerClickInventory(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new ReplayFinishEvent(), SkyWars.getInstance());

    }

    private static void initDatabase() {
        database = new SQLite(SkyWars.getInstance(), "recentgames");
    }

    private static void initHooks() {
        if (Bukkit.getPluginManager().getPlugin("AdvancedReplay") != null) {
            replayHook = new ReplayHook();
            LOGGER.log(Logger.Level.INFO, "[AdvancedReplayHook] AdvancedReplay found, hooking...");
        }
    }

    private static void initCommands() {
        new RecentGamesCommand();
    }

    public static SQLite getDatabase() {
        return database;
    }

    public static ReplayHook getReplayHook() {
        return replayHook;
    }

    public static boolean hasReplayHook() {
        return replayHook != null;
    }
}
