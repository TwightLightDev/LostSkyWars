package org.twightlight.skywars.modules.boosters.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.users.User;

public class BoosterActiveEvent extends Event {
    private User user;
    private Booster booster;

    public BoosterActiveEvent(Booster booster, User player) {
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
