package org.twightlight.skywars.utils;

import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.bungee.Core;

public class KanaoUpdater {

    public static final LostLogger LOGGER = Core.getCoreLogger().getModule("Updater");

    public KanaoUpdater(Plugin plugin, int resourceId) {
        LOGGER.info("Checking updates..");
        LOGGER.info("You're using the latest version of this plugin.");
    }
}
