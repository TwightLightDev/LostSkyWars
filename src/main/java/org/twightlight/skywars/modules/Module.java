package org.twightlight.skywars.modules;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;

public class Module {

    protected SkyWars skywars;
    private String name;
    protected Logger LOGGER;

    public Module(String name) {
        skywars = SkyWars.getInstance();
        LOGGER = SkyWars.LOGGER.getModule(name);
        this.name = name;
    }

    public SkyWars getPlugin() {
        return skywars;
    }

    public void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, skywars);
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public String getName() {
        return name;
    }
}
