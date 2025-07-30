package org.twightlight.skywars.api.event.player;

import org.bukkit.entity.Player;
import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;

public class SkyWarsPlayerCoinEarnEvent extends SkyWarsEvent {

    private SkyWarsServer server;
    private Player player;
    private int amount;

    public SkyWarsPlayerCoinEarnEvent(SkyWarsServer server, Player player, int amount) {
        this.server = server;
        this.player = player;
        this.amount = amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public SkyWarsServer getServer() {
        return server;
    }

    public Player getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }
}
