package org.twightlight.skywars.api.event.game;

import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.api.server.SkyWarsTeam;

public class SkyWarsGameEndEvent extends SkyWarsEvent {

    private SkyWarsServer server;
    private SkyWarsTeam winner;

    public SkyWarsGameEndEvent(SkyWarsServer server, SkyWarsTeam winner) {
        this.server = server;
        this.winner = winner;
    }

    public SkyWarsServer getServer() {
        return server;
    }

    public SkyWarsTeam getWinnerTeam() {
        return winner;
    }

    public boolean hasWinner() {
        return winner != null;
    }
}
