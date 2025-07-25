package org.twightlight.skywars.modules;


import org.twightlight.skywars.SkyWars;

public class Modules {

    protected SkyWars skywars;

    public Modules() {
        skywars = SkyWars.getInstance();
    }

    public SkyWars getPlugin() {
        return skywars;
    }
}
