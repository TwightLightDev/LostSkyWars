package org.twightlight.skywars.modules.boosters.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.users.User;

public class BoosterQueueEvent extends Event {
    private User user;
    private Booster booster;

    public BoosterQueueEvent(Booster booster, User player) {
        this.user = player;
        this.booster = booster;
    }


    public Booster getBooster() {
        return booster;
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
