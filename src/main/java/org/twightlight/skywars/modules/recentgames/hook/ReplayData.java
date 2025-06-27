package org.twightlight.skywars.modules.recentgames.hook;

public class ReplayData {
    private String arenaId;
    private String replayId;
    private String worldName;

    public ReplayData(String a, String b, String c) {
        arenaId = a;
        replayId = b;
        worldName = c;
    }

    public String getReplayId() {
        return replayId;
    }

    public String getArenaId() {
        return arenaId;
    }

    public String getWorldName() {
        return worldName;
    }
}
