package org.twightlight.skywars.integration.worldedit;

import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;

public class WorldEditIntegration {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("WorldEditHook");
    public static WEHelper helper;

    public static void setupWorldEdit() {
        helper = new WEHelper();
        LOGGER.log(Level.INFO, "WorldEdit found, hooking...");
    }

    public static WEHelper getHelper() {
        return helper;
    }
}
