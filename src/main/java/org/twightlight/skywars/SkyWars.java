package org.twightlight.skywars;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.twightlight.skywars.integration.packetevents.PacketEventsIntegration;
import org.twightlight.skywars.integration.placeholderapi.PlaceholderAPIIntegration;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.api.adapters.WorldLoaderAdapter;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.arena.ui.chest.ChestType;
import org.twightlight.skywars.arena.worldloaders.InternalLoader;
import org.twightlight.skywars.arena.worldloaders.SlimeLoader;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.commands.Commands;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.database.providers.SQLiteDatabase;
import org.twightlight.skywars.fun.customitems.CustomItemsManager;
import org.twightlight.skywars.integration.battlepass.BattlePassIntegration;
import org.twightlight.skywars.integration.boxes.BoxesIntegration;
import org.twightlight.skywars.integration.citizens.CitizensIntegration;
import org.twightlight.skywars.integration.decenthologram.DecentHologramsIntegration;
import org.twightlight.skywars.integration.guilds.GuildsIntegration;
import org.twightlight.skywars.integration.protocollib.ProtocolLibIntegration;
import org.twightlight.skywars.integration.worldedit.WorldEditIntegration;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.friends.Friends;
import org.twightlight.skywars.modules.lobbysettings.LobbySettings;
import org.twightlight.skywars.modules.privategames.PrivateGames;
import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.recentgames.RecentGames;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.player.rank.Rank;
import org.twightlight.skywars.player.rank.TagUtils;
import org.twightlight.skywars.systems.holograms.Holograms;
import org.twightlight.skywars.systems.well.AngelOfDeath;
import org.twightlight.skywars.systems.well.WellNPC;
import org.twightlight.skywars.utils.bukkit.MinecraftVersion;

import java.io.File;

import static org.twightlight.skywars.bungee.Core.MODE;

public class SkyWars extends JavaPlugin {

    private static SkyWars instance;
    private static boolean validInit;
    public static boolean packetevents = true, citizens = true, lostparties = true, lostboxes = true, vault = true, placeholderapi = true, battlepass = true, protocollib = true, decentHolograms = true, guilds = true, we = true;
    public static Object economy;
    public static final Logger LOGGER = new Logger();
    private WorldLoaderAdapter adapter;

    public SkyWars() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupPacketEvents();
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
        org.twightlight.skywars.player.level.Level.setupLevels();
        GroupManager.setup();
        CustomItemsManager.load();
        new PrivateGames();
        new RecentGames();
        new Friends();
        new LobbySettings();
        new Boosters();
        new Quests();
        if (MODE != CoreMode.LOBBY) {
            ChestType.setupTypes();
        }
        MenuConfig.setupMenus();
        Language.setupLanguage();

        Holograms.setup(SkyWars.getInstance());
        if (MODE != CoreMode.LOBBY) {
            Arena.setupServers();
        }

        if (MODE != CoreMode.MULTI_ARENA) {
            CoreLobbies.setupLobbies();
        }

        VisualCosmetic.setupCosmetics();

        this.setupVault();
        this.setupPlaceholderAPI();
        this.setupBoxes();
        this.setupBattlePass();
        this.setupGuilds();
        this.setupWE();
        this.setupDecentHolograms();
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
                CitizensIntegration.destroyCitizens();
            }
            if (protocollib) {
                ProtocolLibIntegration.destroyProtocolLib();
            }
            if (lostboxes) {
                BoxesIntegration.destroyBoxes();
            }
            WellNPC.listNPCs().forEach(WellNPC::destroy);
            AngelOfDeath.listNPCs().forEach(AngelOfDeath::destroy);

            Database.getInstance().listAccounts().forEach(account -> {
                Database.getInstance().unloadAccount(account.getUniqueId());
                account.save();
                account.destroy();
            });

            if (decentHolograms) {
                DecentHologramsIntegration.disable();
            }

            Boosters.disable();
            Friends.disable();
            LobbySettings.disable();
            PrivateGames.disable();
            RecentGames.disable();
            CustomItemsManager.disable();
            if (packetevents) {
                PacketEventsIntegration.disable();
            }
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
            BoxesIntegration.setupBoxes();
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
            CitizensIntegration.setupCitizens();
        } catch (ClassNotFoundException ex) {
            citizens = false;
            LOGGER.log(Level.WARNING, "Citizens not found, disabling NPCs.");
        }
    }

    private void setupProtocolLib() {
        try {
            Class.forName("com.comphenix.protocol.ProtocolLibrary");
            ProtocolLibIntegration.setupProtocolLib();
        } catch (ClassNotFoundException ex) {
            protocollib = false;
            LOGGER.log(Level.WARNING, "ProtocolLib not found, disabling StatsNPCs.");
        }
    }

    private void setupPlaceholderAPI() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            PlaceholderAPIIntegration.setupPlaceHolderAPI();
        } catch (ClassNotFoundException ex) {
            placeholderapi = false;
            LOGGER.log(Level.WARNING, "PlaceHolderAPI not found, disabling support.");
        }
    }

    private void setupBattlePass() {
        try {
            Class.forName("io.github.battlepass.BattlePlugin");
            BattlePassIntegration.setupBattlePass();
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.WARNING, "BattlePass not found, disabling support.");
        }
    }

    private void setupDecentHolograms() {
        try {
            Class.forName("eu.decentsoftware.holograms.plugin.DecentHologramsPlugin");
            Bukkit.getScheduler().runTaskLater(this, DecentHologramsIntegration::setupDecentHolograms, 100L);
        } catch (ClassNotFoundException ex) {
            decentHolograms = false;
            LOGGER.log(Level.WARNING, "DecentHolograms not found, disabling support.");
        }
    }

    private void setupPacketEvents() {
        if (Bukkit.getPluginManager().getPlugin("packetevents") != null) {
            PacketEventsIntegration.setupPacketEvents();
        } else {
            packetevents = false;
            LOGGER.log(Level.WARNING, "PacketEvents not found, disabling support.");
        }
    }

    private void setupGuilds() {
        try {
            Class.forName("me.leoo.guilds.bukkit.Guilds");
            GuildsIntegration.setupGuilds();
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
            WorldEditIntegration.setupWorldEdit();
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
