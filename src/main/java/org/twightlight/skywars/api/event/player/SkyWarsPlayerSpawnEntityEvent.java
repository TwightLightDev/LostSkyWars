package org.twightlight.skywars.api.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.twightlight.skywars.api.event.SkyWarsEvent;
import org.twightlight.skywars.api.server.SkyWarsServer;

public class SkyWarsPlayerSpawnEntityEvent extends SkyWarsEvent {

    private Player player;
    private Entity mob;

    public SkyWarsPlayerSpawnEntityEvent(Player player, Entity mob) {
        this.player = player;
        this.mob = mob;
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getEntity() {
        return mob;
    }
}
