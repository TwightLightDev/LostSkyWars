package org.twightlight.skywars.api.event.game;

import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.arena.Arena;

public class SkyWarsGameStartEvent extends SkyWarsEvent {

    private Arena server;

    public SkyWarsGameStartEvent(Arena server) {
        this.server = server;
    }

    public Arena getServer() {
        return server;
    }
}
