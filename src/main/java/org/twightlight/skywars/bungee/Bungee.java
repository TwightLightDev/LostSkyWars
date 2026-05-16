package org.twightlight.skywars.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.bungee.server.ServerManager;

public class Bungee extends Plugin {

    private static Bungee instance;
    public static final Logger LOGGER = new Logger();

    public Bungee() {
        instance = this;
    }

    @Override
    public void onEnable() {
        BungeeFiles.setupFiles();
        ServerManager.getManager().enable();

        LOGGER.log(Level.INFO, "The plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        instance = null;
        LOGGER.log(Level.INFO, "The plugin has been disabled!");
    }

    public static Bungee getInstance() {
        return instance;
    }
}
