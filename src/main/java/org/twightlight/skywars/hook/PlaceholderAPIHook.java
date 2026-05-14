package org.twightlight.skywars.hook;

import org.twightlight.skywars.Logger;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.LostSkyWarsExpansion;

public class PlaceholderAPIHook {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("PlaceholderAPIHook");

    public static void setupPlaceHolderAPI() {
        new LostSkyWarsExpansion().register();
        LOGGER.log(Level.INFO, "PlaceholderAPI found, hooking...");
    }
}
