package org.twightlight.skywars.api.event.player;

import org.bukkit.entity.Player;
import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.arena.Arena;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SkyWarsPlayerXpGainEvent extends SkyWarsEvent {

    private Arena server;
    private Player player;
    private double amount;
    private XpSource source;
    private List<Consumer<List<Object>>> final_task = new ArrayList<>();
    private double baseAmount;

    public SkyWarsPlayerXpGainEvent(Arena server, Player player, double amount) {
        this.server = server;
        this.player = player;
        this.amount = amount;
        this.source = XpSource.CUSTOM;
        this.baseAmount = amount;

    }

    public SkyWarsPlayerXpGainEvent(Arena server, Player player, double amount, XpSource source) {
        this.server = server;
        this.player = player;
        this.amount = amount;
        this.source = source;
    }

    public Arena getServer() {
        return server;
    }

    public Player getPlayer() {
        return player;
    }

    public double getAmount() {
        return amount;
    }

    public XpSource getSource() {
        return source;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void addFinalTask(Consumer<List<Object>> final_task) {
        this.final_task.add(final_task);
    }

    public List<Consumer<List<Object>>> getFinalTask() {
        return final_task;
    }

    public enum XpSource {
        KILL,
        WIN,
        PLAY,
        CUSTOM
    }

    public double getBaseAmount() {
        return baseAmount;
    }
}
