package org.twightlight.skywars.api.event.player;

import org.bukkit.entity.Player;
import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.arena.Arena;

public class SkyWarsPlayerQuitEvent extends SkyWarsEvent {

    private Arena server;
    private Player player;

    public SkyWarsPlayerQuitEvent(Arena server, Player player) {
        this.server = server;
        this.player = player;
    }

    public Arena getServer() {
        return server;
    }

    public Player getPlayer() {
        return player;
    }
}
