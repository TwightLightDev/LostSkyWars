package org.twightlight.skywars.commands;

import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreMode;

public class Commands {

    public static void setupCommands() {
        new StatsCommand();
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            new WatchCommand();
            new PlayCommand();
            new MenuCommand();
        }
        new SkyWarsCommand();
    }
}
