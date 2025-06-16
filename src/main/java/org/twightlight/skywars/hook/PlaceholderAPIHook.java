package org.twightlight.skywars.hook;

import org.twightlight.skywars.Main;
import org.twightlight.skywars.api.LostSkyWarsExpansion;
import org.twightlight.skywars.api.LostSkyWarsPlusExpansion;
import org.twightlight.skywars.utils.LostLogger;
import org.twightlight.skywars.utils.LostLogger.LostLevel;

public class PlaceholderAPIHook {

    public static final LostLogger LOGGER = Main.LOGGER.getModule("PlaceholderAPIHook");

    public static void setupPlaceHolderAPI() {
        new LostSkyWarsExpansion().register();
        new LostSkyWarsPlusExpansion().register();
        LOGGER.log(LostLevel.INFO, "PlaceholderAPI found, hooking...");
    }
}
