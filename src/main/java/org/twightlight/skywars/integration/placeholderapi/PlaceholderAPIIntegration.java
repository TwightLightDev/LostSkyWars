package org.twightlight.skywars.integration.placeholderapi;

import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.LostSkyWarsExpansion;

public class PlaceholderAPIIntegration {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("PlaceholderAPIHook");

    public static void setupPlaceHolderAPI() {
        new LostSkyWarsExpansion().register();
        LOGGER.log(Level.INFO, "PlaceholderAPI found, hooking...");
    }
}
