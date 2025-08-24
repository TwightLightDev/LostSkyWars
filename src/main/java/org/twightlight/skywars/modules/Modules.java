package org.twightlight.skywars.modules;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.twightlight.skywars.SkyWars;

public class Modules {

    protected SkyWars skywars;

    public Modules() {
        skywars = SkyWars.getInstance();
    }

    public SkyWars getPlugin() {
        return skywars;
    }

    public void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, skywars);
    }
}
