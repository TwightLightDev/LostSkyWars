package org.twightlight.skywars.bungee.server.balancer.server;

import org.twightlight.skywars.api.server.SkyWarsState;

public class ArenaServer extends BungeeServer {

    private String map;
    private String groupId;
    private SkyWarsState state;

    public ArenaServer(String serverId, int maxPlayers, SkyWarsState state) {
        super(serverId, maxPlayers);
        this.state = state;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setState(SkyWarsState state) {
        this.state = state;
    }

    @Override
    public boolean canBeSelected() {
        return super.canBeSelected() && !isInProgress();
    }

    public boolean isInProgress() {
        return state != SkyWarsState.WAITING;
    }

    public String getMap() {
        return this.map;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public SkyWarsState getState() {
        return this.state;
    }
}
