package org.twightlight.skywars;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.twightlight.skywars.api.adapters.WorldLoaderAdapter;
import org.twightlight.skywars.arena.worldloaders.InternalLoader;
import org.twightlight.skywars.arena.worldloaders.SlimeLoader;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.cmd.Commands;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.database.SQLiteDatabase;
import org.twightlight.skywars.holograms.Holograms;
import org.twightlight.skywars.hook.*;
import org.twightlight.skywars.leaderboards.LeaderBoard;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.menu.ConfigMenu;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.modules.privategames.PrivateGames;
import org.twightlight.skywars.rank.Rank;
import org.twightlight.skywars.rank.TagUtils;
import org.twightlight.skywars.ranked.Ranked;
import org.twightlight.skywars.modules.recentgames.RecentGames;
import org.twightlight.skywars.ui.chest.ChestType;
import org.twightlight.skywars.utils.Logger;
import org.twightlight.skywars.utils.Logger.Level;
import org.twightlight.skywars.utils.MinecraftVersion;
import org.twightlight.skywars.well.AngelOfDeath;
import org.twightlight.skywars.well.WellNPC;
import org.twightlight.skywars.arena.Arena;

import java.io.File;

import static org.twightlight.skywars.bungee.Core.MODE;

public class SkyWars extends JavaPlugin {

    private static SkyWars instance;
    private static boolean validInit;
    public static boolean citizens = true, lostparties = true, lostboxes = true, vault = true, placeholderapi = true, battlepass = true, protocollib = true, guilds = true, we = true;
    public static Object economy;
    public static final Logger LOGGER = new Logger();
    private WorldLoaderAdapter adapter;

    public SkyWars() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!this.getConfig().contains("servermode")) {
            this.getConfig().set("servermode", CoreMode.MULTI_ARENA.name());
            saveConfig();
        }
        try {
            Core.MODE = CoreMode.valueOf(this.getConfig().get("servermode").toString());
        } catch (Exception ex) {
        }
        if (!NMS.setupNMS()) {
            this.setEnabled(false);
            LOGGER.log(Level.SEVERE, "This server version (" + MinecraftVersion.getCurrentVersion().getVersion() + ") is not compatible with LostSkyWars.");
            LOGGER.log(Level.INFO, "Compatible builds: v1_8_R3, v1_12_R1");
            return;
        }

        if (Bukkit.getServer().getSpawnRadius() != 0) {
            Bukkit.getServer().setSpawnRadius(0);
        }

        Database.setupDatabase();

        if (MODE != CoreMode.MULTI_ARENA && Database.getInstance() instanceof SQLiteDatabase) {
            this.cancelBungee(false);
            return;
        }

        if (MODE != CoreMode.MULTI_ARENA && Database.getInstance().query("SELECT * FROM `lostskywars_files`") == null) {
            this.cancelBungee(true);
            return;
        }

        if (MODE != CoreMode.MULTI_ARENA && !Bukkit.getServer().spigot().getConfig().getBoolean("settings.bungeecord")) {
            this.setEnabled(false);
            LOGGER.log(Level.WARNING, "Enable BungeeCord mode in spigot.yml 'settings.bungeecord'");
            return;
        }
        setupAdapter();
        File modules = new File("plugins/LostSkyWars/modules");
        if (!modules.exists()) {
            modules.mkdirs();
        }
        Rank.setupRanks();
        org.twightlight.skywars.level.Level.setupLevels();
        Ranked.setupRanked();
        new PrivateGames();
        new RecentGames();
        new Friends();
        new LobbySettings();
        new Boosters();
        if (MODE != CoreMode.LOBBY) {
            ChestType.setupTypes();
        }
        ConfigMenu.setupMenus();
        Language.setupLanguage();

        Holograms.setup(SkyWars.getInstance());
        if (MODE != CoreMode.LOBBY) {
            Arena.setupServers();
        }

        if (MODE != CoreMode.MULTI_ARENA) {
            CoreLobbies.setupLobbies();
        }

        Cosmetic.setupCosmetics();

        this.setupVault();
        this.setupPlaceholderAPI();
        this.setupBoxes();
        this.setupBattlePass();
        this.setupGuilds();
        this.setupWE();
        if (MODE == CoreMode.MULTI_ARENA) {
            this.setupParties();
        } else {
            lostparties = false;
        }
        if (MODE != CoreMode.ARENA) {
            this.setupProtocolLib();
            this.setupCitizens();
        } else {
            protocollib = false;
            citizens = false;
        }

        Commands.setupCommands();
        Listeners.setupListeners();

        if (MODE != CoreMode.ARENA) {
            LeaderBoard.setupLeaderBoards();

            WellNPC.setupWellNPCs();
            AngelOfDeath.setupAngels();
        }

        validInit = true;
        this.getServer().getScheduler().runTask(this, () -> {
            LOGGER.log(Level.INFO, "Server Mode: " + Core.MODE.name());
        });
        LOGGER.log(Level.INFO, "The plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (validInit) {
            TagUtils.reset();
            Holograms.close();

            if (citizens) {
                CitizensHook.destroyCitizens();
            }
            if (protocollib) {
                ProtocolLibHook.destroyProtocolLib();
            }
            if (lostboxes) {
                BoxesHook.destroyBoxes();
            }
            WellNPC.listNPCs().forEach(WellNPC::destroy);
            AngelOfDeath.listNPCs().forEach(AngelOfDeath::destroy);

            Database.getInstance().listAccounts().forEach(account -> {
                Database.getInstance().unloadAccount(account.getUniqueId());
                account.save();
                account.destroy();
            });

            HandlerList.unregisterAll(this);
            Bukkit.getScheduler().cancelTasks(this);
        }

        instance = null;
        LOGGER.log(Level.INFO, "The plugin has been disabled!");
    }

    private void cancelBungee(boolean table) {
        this.setEnabled(false);
        if (!table) {
            LOGGER.log(Level.WARNING, "You can't use SQLite for Bungee Mode. Use MySQL instead of SQLite!");
        } else {
            LOGGER.log(Level.WARNING, "Unable to find Table with BungeeFiles.");
            LOGGER.log(Level.WARNING, "Setup your BungeeCord with LostSkyWars first before using that!");
        }
    }

    private void setupBoxes() {
        try {
            Class.forName("io.github.losteddev.boxes.Main");
            BoxesHook.setupBoxes();
        } catch (ClassNotFoundException ex) {
            lostboxes = false;
            LOGGER.log(Level.WARNING, "LostBoxes not found, disabling Mystery Boxes.");
        }
    }

    private void setupParties() {
        try {
            Class.forName("io.github.losteddev.parties.Main");
            LOGGER.log(Level.INFO, "[PartiesHook] LostParties found, hooking...");
        } catch (ClassNotFoundException ex) {
            lostparties = false;
            LOGGER.log(Level.WARNING, "LostParties not found, disabling Parties.");
        }
    }

    private void setupCitizens() {
        try {
            Class.forName("net.citizensnpcs.api.CitizensAPI");
            CitizensHook.setupCitizens();
        } catch (ClassNotFoundException ex) {
            citizens = false;
            LOGGER.log(Level.WARNING, "Citizens not found, disabling NPCs.");
        }
    }

    private void setupProtocolLib() {
        try {
            Class.forName("com.comphenix.protocol.ProtocolLibrary");
            ProtocolLibHook.setupProtocolLib();
        } catch (ClassNotFoundException ex) {
            protocollib = false;
            LOGGER.log(Level.WARNING, "ProtocolLib not found, disabling StatsNPCs.");
        }
    }

    private void setupPlaceholderAPI() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            PlaceholderAPIHook.setupPlaceHolderAPI();
        } catch (ClassNotFoundException ex) {
            placeholderapi = false;
            LOGGER.log(Level.WARNING, "PlaceHolderAPI not found, disabling support.");
        }
    }

    private void setupBattlePass() {
        try {
            Class.forName("io.github.battlepass.BattlePlugin");
            BattlePassHook.setupBattlePass();
        } catch (ClassNotFoundException ex) {
            battlepass = false;
            LOGGER.log(Level.WARNING, "BattlePass not found, disabling support.");
        }
    }

    private void setupGuilds() {
        try {
            Class.forName("me.leoo.guilds.bukkit.Guilds");
            GuildsHook.setupGuilds();
        } catch (ClassNotFoundException ex) {
            guilds = false;
            LOGGER.log(Level.WARNING, "Guilds not found, disabling support.");
        }
    }


    private void setupVault() {
        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            LOGGER.log(Level.INFO, "[VaultHook] Vault found, hooking...");
            try {
                economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
            } catch (Exception ex) {
                vault = false;
                LOGGER.log(Level.WARNING, "[VaultHook] EconomyProvider not found, disabling custom Economy.");
            }
        } catch (Exception ex) {
            vault = false;
            LOGGER.log(Level.WARNING, "Vault not found, disabling custom Economy.");
        }
    }

    private void setupAdapter() {
        Plugin swmPlugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        if (swmPlugin == null) {
            adapter = new InternalLoader(this);

        } else {
            adapter = new SlimeLoader(this);
        }
    }

    private void setupWE() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (plugin != null) {
            WorldEditHook.setupWorldEdit();
        } else {
            we = false;
            LOGGER.log(Level.WARNING, "WorldEdit not found, disabling support...");
        }
    }

    public WorldLoaderAdapter getWorldLoader() {
        return adapter;
    }

    public static SkyWars getInstance() {
        return instance;
    }
}
