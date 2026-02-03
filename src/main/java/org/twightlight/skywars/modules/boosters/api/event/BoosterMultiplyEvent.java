package org.twightlight.skywars.modules.boosters.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterData;
import org.twightlight.skywars.modules.boosters.users.User;

import java.util.List;

public class BoosterMultiplyEvent extends Event {
    private User user;
    private Booster.Currency currency;
    private int totalProfit;
    private List<BoosterData> boostersData;

    public BoosterMultiplyEvent(Booster.Currency currency, User player, int totalProfit, List<BoosterData> boostersData) {
        this.user = player;
        this.currency = currency;
        this.totalProfit = totalProfit;
        this.boostersData = boostersData;
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

    public List<BoosterData> getBoostersData() {
        return boostersData;
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
