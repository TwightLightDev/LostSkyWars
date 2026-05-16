package org.twightlight.skywars.integration.guilds;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.integration.guilds.commands.GuildCoinsCommand;
import org.twightlight.skywars.integration.guilds.commands.GuildsDonationCommand;
import org.twightlight.skywars.integration.guilds.config.LangConfig;
import org.twightlight.skywars.integration.guilds.config.LevelConfig;
import org.twightlight.skywars.integration.guilds.database.GuildDonationDB;
import org.twightlight.skywars.integration.guilds.donation.Donator;
import org.twightlight.skywars.integration.guilds.listeners.InventoryClick;
import org.twightlight.skywars.integration.guilds.listeners.InventoryManager;
import org.twightlight.skywars.integration.guilds.listeners.PlayerEvent;
import org.twightlight.skywars.integration.guilds.papi.GuildsDonationExpansion;
import org.twightlight.skywars.integration.guilds.papi.GuildsShopExpansion;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;

import java.io.File;
import java.util.ArrayList;

public class GuildsIntegration {
    private static GuildDonationDB external_db;
    public static final Logger LOGGER = SkyWars.LOGGER.getModule("GuildsHook");
    public static BukkitTask autoSave;
    public static YamlWrapper levelConfig;
    public static YamlWrapper langConfig;
    public static void setupGuilds() {
        LOGGER.log(Logger.Level.INFO, "Guilds found, hooking...");
        Bukkit.getPluginManager().registerEvents(new org.twightlight.skywars.integration.guilds.listeners.SkyWars(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerEvent(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new InventoryManager(), SkyWars.getInstance());

        external_db = new GuildDonationDB(SkyWars.getInstance(), "guilds");
        startAutoSave();
        loadConfig();
        setupPlaceHolderAPI();
        initCommands();
    }

    public static GuildDonationDB getExternalDB() {
        return external_db;
    }

    public static void startAutoSave() {
        autoSave = Bukkit.getScheduler().runTaskTimer(SkyWars.getInstance(), () -> {
            new ArrayList<>(Donator.getDonatorList().values()).forEach(Donator::saveData);
        }, 0L, 6000L);
    }

    private static void loadConfig() {
        File modules = new File(SkyWars.getInstance().getDataFolder().getPath() + "/modules/guilds_expansion");
        if (!modules.exists()) {
            modules.mkdirs();
        }
        levelConfig = new LevelConfig(SkyWars.getInstance(), "level", SkyWars.getInstance().getDataFolder().getPath() + "/modules/guilds_expansion");
        langConfig = new LangConfig(SkyWars.getInstance(), "language", SkyWars.getInstance().getDataFolder().getPath() + "/modules/guilds_expansion");
    }

    public static YamlWrapper getLevelConfig() {
        return levelConfig;
    }

    public static YamlWrapper getLanguage() {
        return langConfig;
    }

    private static void setupPlaceHolderAPI() {
        if (SkyWars.placeholderapi) {
            new GuildsDonationExpansion().register();
            new GuildsShopExpansion().register();
        }
    }

    private static void initCommands() {
        new GuildsDonationCommand();
        new GuildCoinsCommand();
    }
}
