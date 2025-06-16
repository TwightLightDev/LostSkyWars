package org.twightlight.skywars.api.event.game;

import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;

public class SkyWarsChestRefillEvent extends SkyWarsEvent {

    private SkyWarsServer server;

    public SkyWarsChestRefillEvent(SkyWarsServer server) {
        this.server = server;
    }

    public SkyWarsServer getServer() {
        return server;
    }
}
