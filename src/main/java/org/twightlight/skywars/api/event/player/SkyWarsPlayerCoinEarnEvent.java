package org.twightlight.skywars.api.event.player;

import org.bukkit.entity.Player;
import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;

public class SkyWarsPlayerCoinEarnEvent extends SkyWarsEvent {

    private SkyWarsServer server;
    private Player player;
    private int amount;
    private int baseAmount;
    private CoinSource coinSource;

    public SkyWarsPlayerCoinEarnEvent(SkyWarsServer server, Player player, int amount) {
        this.server = server;
        this.player = player;
        this.amount = amount;
        this.baseAmount = amount;
        this.coinSource = CoinSource.CUSTOM;
    }

    public SkyWarsPlayerCoinEarnEvent(SkyWarsServer server, Player player, int amount, CoinSource source) {
        this.server = server;
        this.player = player;
        this.amount = amount;
        this.coinSource = source;
    }

    public CoinSource getCoinSource() {
        return coinSource;
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

    public int getBaseAmount() {
        return baseAmount;
    }

    public enum CoinSource {
        KILL,
        WIN,
        PLAY,
        CUSTOM
    }
}
