package org.twightlight.skywars.hook;

import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.LostSkyWarsExpansion;
import org.twightlight.skywars.api.LostSkyWarsPlusExpansion;
import org.twightlight.skywars.hook.worldedit.WEHelper;
import org.twightlight.skywars.utils.Logger;
import org.twightlight.skywars.utils.Logger.Level;

public class WorldEditHook {

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
