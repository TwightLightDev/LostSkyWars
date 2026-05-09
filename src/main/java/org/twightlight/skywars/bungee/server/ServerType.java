package org.twightlight.skywars.bungee.server;

public enum ServerType {

    LOBBY,
    ARENA;

    private static final ServerType[] VALUES = ServerType.values();

    public static ServerType fromName(String name) {
        if (name == null) return null;
        for (ServerType type : VALUES) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        if (name.equalsIgnoreCase("LOBBY")) return LOBBY;
        return ARENA;
    }
}
