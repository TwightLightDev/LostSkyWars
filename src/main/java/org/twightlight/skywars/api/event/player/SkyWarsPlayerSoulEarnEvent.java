package org.twightlight.skywars.api.event.player;

import org.bukkit.entity.Player;
import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.arena.Arena;

public class SkyWarsPlayerSoulEarnEvent extends SkyWarsEvent {

    private Arena server;
    private Player player;
    private int amount;
    private int baseAmount;

    public SkyWarsPlayerSoulEarnEvent(Arena server, Player player, int amount) {
        this.server = server;
        this.player = player;
        this.amount = amount;
        this.baseAmount = amount;

    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Arena getServer() {
        return server;
    }

    public Player getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }

    public int getBaseAmount() {
        return baseAmount;
    }
}
