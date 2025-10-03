package org.twightlight.skywars.hook;

import org.bukkit.Bukkit;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.ui.enums.SkyWarsMode;
import org.twightlight.skywars.arena.ui.enums.SkyWarsType;
import org.twightlight.skywars.hook.decenthologram.commands.LeaderboardsCommand;
import org.twightlight.skywars.hook.decenthologram.holograms.Leaderboard;
import org.twightlight.skywars.hook.decenthologram.listeners.HologramReloadEvent;
import org.twightlight.skywars.hook.decenthologram.listeners.PlayerJoinEvent;
import org.twightlight.skywars.hook.decenthologram.listeners.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DecentHologramsHook {
    public static Logger LOGGER = SkyWars.LOGGER.getModule("DecentHologramsHook");
    public static List<Leaderboard> leaderboards = new ArrayList<>();
    public static CompletableFuture<Boolean> loadStatus = new CompletableFuture<>();

    public static void setupDecentHolograms() {
        LOGGER.log(Logger.Level.INFO, "DecentHolograms found, hooking...");

        loadCommands();
        loadLeaderboards();
        loadListeners();
        loadStatus.complete(true);
    }

    private static void loadCommands() {
        LOGGER.log(Logger.Level.INFO, "Loading Commands...");

        new LeaderboardsCommand();
    }

    public static void disable() {
        leaderboards.clear();
        loadStatus = new CompletableFuture<>();
    }

    public static void loadLeaderboards() {
        LOGGER.log(Logger.Level.INFO, "Loading Leaderboards...");
        leaderboards.clear();
        String[] stats = new String[] {"kills", "wins", "assists", "games"};
        for (Leaderboard.Mode mode : Leaderboard.Mode.values()) {
            for (String stat : stats) {
                Leaderboard leaderboard = new Leaderboard("skywars_leaderboard_" + mode.name().toLowerCase() + "_" + stat, mode);
                leaderboards.add(leaderboard);
            }
        }
        LOGGER.log(Logger.Level.INFO, "Loaded " + leaderboards.size() + " leaderboards!");
    }

    private static void loadListeners() {
        LOGGER.log(Logger.Level.INFO, "Loading Listeners...");

        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new HologramReloadEvent(), SkyWars.getInstance());

    }

    public static List<Leaderboard> getLeaderboards(Leaderboard.Mode mode) {
        return leaderboards.stream().filter(leaderboard -> leaderboard.getMode() == mode).collect(Collectors.toList());
    }

    public static List<Leaderboard> getLeaderboards() {
        return leaderboards;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
