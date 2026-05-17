package org.twightlight.skywars.commands;

import org.twightlight.skywars.bungee.core.Core;
import org.twightlight.skywars.bungee.core.CoreMode;

public class CommandsManager {

    public static void setupCommands() {
        new StatsCommand();
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            new WatchCommand();
            new MenuCommand();
        }
        new SkyWarsCommand();
    }
}
