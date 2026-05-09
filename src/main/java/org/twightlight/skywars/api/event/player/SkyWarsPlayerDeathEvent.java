package org.twightlight.skywars.api.event.player;

import org.bukkit.entity.Player;
import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.arena.Arena;

public class SkyWarsPlayerDeathEvent extends SkyWarsEvent {

    private Arena server;
    private Player player;
    private Player killer;
    private SkyWarsDeathCause cause;
    private String message;

    public SkyWarsPlayerDeathEvent(Arena server, Player player, Player killer, SkyWarsDeathCause cause, String killMessage) {
        this.server = server;
        this.player = player;
        this.killer = killer;
        this.cause = cause;
        message = killMessage;
    }

    public Arena getServer() {
        return server;
    }

    public SkyWarsDeathCause getCause() {
        return cause;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getKiller() {
        return killer;
    }

    public boolean isKilled() {
        return killer != null;
    }

    public enum SkyWarsDeathCause {
        SUICIDE,
        SUICIDE_VOID,
        KILLED_MELEE,
        KILLED_VOID,
        KILLED_BOW,
        KILLED_MOB;
    }

    public String getKillMessage() {
        return message;
    }

    public void setKillMessage(String message) {
        this.message = message;
    }
}
