package org.twightlight.skywars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.listeners.entity.EntityListener;
import org.twightlight.skywars.listeners.entity.FriendlyMobListener;
import org.twightlight.skywars.listeners.entity.ItemFrameManager;
import org.twightlight.skywars.listeners.player.*;
import org.twightlight.skywars.listeners.server.PluginMessageListener;
import org.twightlight.skywars.listeners.server.ServerListener;
import org.twightlight.skywars.listeners.skywars.SkyWarsDeath;
import org.twightlight.skywars.utils.LostLogger;

public class Listeners implements Listener {

    public static final LostLogger LOGGER = Main.LOGGER.getModule("Listeners");

    public static void setupListeners() {
        Plugin plugin = Main.getInstance();
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new EntityListener(), plugin);

        pm.registerEvents(new AsyncPlayerChatListener(), plugin);
        pm.registerEvents(new InventoryClickListener(), plugin);
        pm.registerEvents(new PlayerDeathListener(), plugin);
        pm.registerEvents(new PlayerInteractListener(), plugin);
        pm.registerEvents(new PlayerLoginListener(), plugin);
        pm.registerEvents(new PlayerJoinListener(), plugin);
        pm.registerEvents(new PlayerQuitListener(), plugin);
        pm.registerEvents(new PlayerRestListener(), plugin);
        pm.registerEvents(new InventoryCloseListener(), plugin);
        pm.registerEvents(new FriendlyMobListener(), plugin);
        pm.registerEvents(new ProjectileLaunchEvent(), plugin);
        pm.registerEvents(new SkyWarsDeath(), plugin);
        pm.registerEvents(new ItemFrameManager(), plugin);

        pm.registerEvents(new ServerListener(), plugin);

        if (Core.MODE != CoreMode.MULTI_ARENA) {
            Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "LostSWAPI", new PluginMessageListener());
        }
    }
}
