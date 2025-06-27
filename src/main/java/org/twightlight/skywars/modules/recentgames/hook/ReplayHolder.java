package org.twightlight.skywars.modules.recentgames.hook;

import me.jumper251.replay.replaysystem.Replay;

public class ReplayHolder {
    private Replay replay;
    private String worldName;

    public String getWorldName() {
        return worldName;
    }

    public Replay getReplay() {
        return replay;
    }

    public ReplayHolder(Replay replay, String worldName) {
        this.replay = replay;
        this.worldName = worldName;
    }
}
