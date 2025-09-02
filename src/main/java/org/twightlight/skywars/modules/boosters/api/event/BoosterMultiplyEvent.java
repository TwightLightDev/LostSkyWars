package org.twightlight.skywars.modules.boosters.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.users.User;

import java.awt.*;

public class BoosterMultiplyEvent extends Event {
    private User user;
    private Booster.Currency currency;
    private int totalProfit;

    public BoosterMultiplyEvent(Booster.Currency currency, User player, int totalProfit) {
        this.user = player;
        this.currency = currency;
        this.totalProfit = totalProfit;
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public Booster.Currency getCurrency() {
        return currency;
    }

    public User getUser() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
