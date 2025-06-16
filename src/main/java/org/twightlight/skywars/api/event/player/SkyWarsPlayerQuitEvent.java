package org.twightlight.skywars.api.event.player;

import org.bukkit.entity.Player;
import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;

public class SkyWarsPlayerQuitEvent extends SkyWarsEvent {

    private SkyWarsServer server;
    private Player player;

    public SkyWarsPlayerQuitEvent(SkyWarsServer server, Player player) {
        this.server = server;
        this.player = player;
    }

    public SkyWarsServer getServer() {
        return server;
    }

    public Player getPlayer() {
        return player;
    }
}
