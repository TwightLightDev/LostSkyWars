package org.twightlight.skywars.modules.recentgames;

import org.bukkit.Bukkit;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.Module;
import org.twightlight.skywars.modules.recentgames.commands.RecentGamesCommand;
import org.twightlight.skywars.modules.recentgames.database.SQLite;
import org.twightlight.skywars.modules.recentgames.hook.ReplayHook;
import org.twightlight.skywars.modules.recentgames.listeners.*;
import org.twightlight.skywars.utils.Logger;

public class RecentGames extends Module {

    private static SQLite database;
    private static ReplayHook replayHook = null;

    public RecentGames() {
        super("RecentGames");
        initHooks();
        initListeners();
        initDatabase();
        initCommands();
        LOGGER.log(Logger.Level.INFO, "RecentGame module has been successfully loaded!");
    }

    private void initListeners() {
        LOGGER.log(Logger.Level.INFO, "Loading Listeners...");

        Bukkit.getPluginManager().registerEvents(new GameEndEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new GameStartEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerClickInventory(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new ReplayFinishEvent(), SkyWars.getInstance());

    }

    private void initDatabase() {
        LOGGER.log(Logger.Level.INFO, "Loading Database...");

        database = new SQLite(SkyWars.getInstance(), "recentgames");
    }

    private void initHooks() {
        LOGGER.log(Logger.Level.INFO, "Loading Available Integrations...");

        if (Bukkit.getPluginManager().getPlugin("AdvancedReplay") != null) {
            replayHook = new ReplayHook();
            LOGGER.log(Logger.Level.INFO, "[AdvancedReplayHook] AdvancedReplay found, hooking...");
        }
    }

    private void initCommands() {
        LOGGER.log(Logger.Level.INFO, "Loading Commands...");
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
