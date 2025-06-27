package org.twightlight.skywars.modules.recentgames;

import org.bukkit.Bukkit;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.modules.recentgames.commands.RecentGamesCommand;
import org.twightlight.skywars.modules.recentgames.database.SQLite;
import org.twightlight.skywars.modules.recentgames.hook.ReplayHook;
import org.twightlight.skywars.modules.recentgames.listeners.*;
import org.twightlight.skywars.utils.Logger;

public class RecentGames {

    private static SQLite database;
    private static ReplayHook replayHook = null;
    public static Logger LOGGER = new Logger();

    public static void setupRecentGames() {
        initHooks();
        initListeners();
        initDatabase();
        initCommands();
    }

    private static void initListeners() {
        Bukkit.getPluginManager().registerEvents(new GameEndEvent(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new GameStartEvent(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEvent(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerClickInventory(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new ReplayFinishEvent(), Main.getInstance());

    }

    private static void initDatabase() {
        database = new SQLite(Main.getInstance(), "recentgames");
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
