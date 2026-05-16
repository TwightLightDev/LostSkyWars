package org.twightlight.skywars.arena.ui.enums;

public enum SkyWarsState {
    NONE("None"),
    WAITING("Waiting"),
    STARTING("Starting"),
    INGAME("InGame"),
    ENDED("Ended"),
    ROLLBACKING("Rollbacking");

    private String name;

    SkyWarsState(String name) {
        this.name = name;
    }

    public boolean canJoin() {
        return this == WAITING;
    }

    public String getName() {
        return name;
    }
}
