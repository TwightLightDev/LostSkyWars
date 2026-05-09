package org.twightlight.skywars.api.event.game;

import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.arena.Arena;

public class SkyWarsDoomEvent extends SkyWarsEvent {

    private Arena server;

    public SkyWarsDoomEvent(Arena server) {
        this.server = server;
    }

    public Arena getServer() {
        return server;
    }
}
