package org.twightlight.skywars.api.event.game;

import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;

public class SkyWarsGameStartEvent extends SkyWarsEvent {

    private SkyWarsServer server;

    public SkyWarsGameStartEvent(SkyWarsServer server) {
        this.server = server;
    }

    public SkyWarsServer getServer() {
        return server;
    }
}
