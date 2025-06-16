package org.twightlight.skywars;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.cmd.Commands;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.database.SQLiteDatabase;
import org.twightlight.skywars.holograms.Holograms;
import org.twightlight.skywars.hook.BoxesHook;
import org.twightlight.skywars.hook.CitizensHook;
import org.twightlight.skywars.hook.PlaceholderAPIHook;
import org.twightlight.skywars.hook.ProtocolLibHook;
import org.twightlight.skywars.leaderboards.LeaderBoard;
import org.twightlight.skywars.level.Level;
import org.twightlight.skywars.listeners.Listeners;
import org.twightlight.skywars.menu.ConfigMenu;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.privategames.PrivateGames;
import org.twightlight.skywars.rank.Rank;
import org.twightlight.skywars.rank.TagUtils;
import org.twightlight.skywars.ranked.Ranked;
import org.twightlight.skywars.ui.SkyWarsChest.ChestType;
import org.twightlight.skywars.utils.KanaoUpdater;
import org.twightlight.skywars.utils.LostLogger;
import org.twightlight.skywars.utils.LostLogger.LostLevel;
import org.twightlight.skywars.utils.MinecraftVersion;
import org.twightlight.skywars.well.AngelOfDeath;
import org.twightlight.skywars.well.WellNPC;
import org.twightlight.skywars.world.WorldServer;

import static org.twightlight.skywars.bungee.Core.MODE;

public class Main extends JavaPlugin {

    private static Main instance;
    private static boolean validInit;
    public static boolean citizens = true, lostparties = true, lostboxes = true, vault = true, placeholderapi = true, slimeworldmanager = true, protocollib = true;
    public static Object economy;
    public static final LostLogger LOGGER = new LostLogger();

    public Main() {
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
            LOGGER.log(LostLevel.SEVERE, "This server version (" + MinecraftVersion.getCurrentVersion().getVersion() + ") is not compatible with LostSkyWars.");
            LOGGER.log(LostLevel.INFO, "Compatible builds: v1_8_R3, v1_12_R1");
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
            LOGGER.log(LostLevel.WARNING, "Enable BungeeCord mode in spigot.yml 'settings.bungeecord'");
            return;
        }

        Rank.setupRanks();
        Level.setupLevels();
        Ranked.setupRanked();
        PrivateGames.setupPrivateGames();
        if (MODE != CoreMode.LOBBY) {
            ChestType.setupTypes();
        }
        ConfigMenu.setupMenus();
        Language.setupLanguage();

        Holograms.setup(Main.getInstance());
        if (MODE != CoreMode.LOBBY) {
            WorldServer.setupServers();
        }

        if (MODE != CoreMode.MULTI_ARENA) {
            CoreLobbies.setupLobbies();
        }

        Cosmetic.setupCosmetics();

        this.setupVault();
        this.setupPlaceholderAPI();
        this.setupBoxes();
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
            new KanaoUpdater(this, 1);
            LOGGER.log(LostLevel.INFO, "Server Mode: " + Core.MODE.name());
        });
        LOGGER.log(LostLevel.INFO, "The plugin has been enabled!");
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
        LOGGER.log(LostLevel.INFO, "The plugin has been disabled!");
    }

    private void cancelBungee(boolean table) {
        this.setEnabled(false);
        if (!table) {
            LOGGER.log(LostLevel.WARNING, "You can't use SQLite for Bungee Mode. Use MySQL instead of SQLite!");
        } else {
            LOGGER.log(LostLevel.WARNING, "Unable to find Table with BungeeFiles.");
            LOGGER.log(LostLevel.WARNING, "Setup your BungeeCord with LostSkyWars first before using that!");
        }
    }

    private void setupBoxes() {
        try {
            Class.forName("io.github.losteddev.boxes.Main");
            BoxesHook.setupBoxes();
        } catch (ClassNotFoundException ex) {
            lostboxes = false;
            LOGGER.log(LostLevel.WARNING, "LostBoxes not found, disabling Mystery Boxes.");
        }
    }

    private void setupParties() {
        try {
            Class.forName("io.github.losteddev.parties.Main");
            LOGGER.log(LostLevel.INFO, "[PartiesHook] LostParties found, hooking...");
        } catch (ClassNotFoundException ex) {
            lostparties = false;
            LOGGER.log(LostLevel.WARNING, "LostParties not found, disabling Parties.");
        }
    }

    private void setupCitizens() {
        try {
            Class.forName("net.citizensnpcs.api.CitizensAPI");
            CitizensHook.setupCitizens();
        } catch (ClassNotFoundException ex) {
            citizens = false;
            LOGGER.log(LostLevel.WARNING, "Citizens not found, disabling NPCs.");
        }
    }

    private void setupProtocolLib() {
        try {
            Class.forName("com.comphenix.protocol.ProtocolLibrary");
            ProtocolLibHook.setupProtocolLib();
        } catch (ClassNotFoundException ex) {
            protocollib = false;
            LOGGER.log(LostLevel.WARNING, "ProtocolLib not found, disabling StatsNPCs.");
        }
    }

    private void setupPlaceholderAPI() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            PlaceholderAPIHook.setupPlaceHolderAPI();
        } catch (ClassNotFoundException ex) {
            placeholderapi = false;
            LOGGER.log(LostLevel.WARNING, "PlaceHolderAPI not found, disabling support.");
        }
    }


    private void setupVault() {
        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            LOGGER.log(LostLevel.INFO, "[VaultHook] Vault found, hooking...");
            try {
                economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
            } catch (Exception ex) {
                vault = false;
                LOGGER.log(LostLevel.WARNING, "[VaultHook] EconomyProvider not found, disabling custom Economy.");
            }
        } catch (Exception ex) {
            vault = false;
            LOGGER.log(LostLevel.WARNING, "Vault not found, disabling custom Economy.");
        }
    }

    public static Main getInstance() {
        return instance;
    }
}
