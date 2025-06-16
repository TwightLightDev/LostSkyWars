package org.twightlight.skywars.api.event.game;

import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;

public class SkyWarsDoomEvent extends SkyWarsEvent {

    private SkyWarsServer server;

    public SkyWarsDoomEvent(SkyWarsServer server) {
        this.server = server;
    }

    public SkyWarsServer getServer() {
        return server;
    }
}
