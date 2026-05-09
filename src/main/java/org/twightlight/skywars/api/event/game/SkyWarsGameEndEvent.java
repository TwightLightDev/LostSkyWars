package org.twightlight.skywars.api.event.game;

import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.SkyWarsTeam;

public class SkyWarsGameEndEvent extends SkyWarsEvent {

    private Arena server;
    private SkyWarsTeam winner;

    public SkyWarsGameEndEvent(Arena server, SkyWarsTeam winner) {
        this.server = server;
        this.winner = winner;
    }

    public Arena getServer() {
        return server;
    }

    public SkyWarsTeam getWinnerTeam() {
        return winner;
    }

    public boolean hasWinner() {
        return winner != null;
    }
}
