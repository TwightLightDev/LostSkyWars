package org.twightlight.skywars.modules.recentgames.hook;

import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.Replay;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.recentgames.GameData;
import org.twightlight.skywars.utils.ZipUtils;
import org.twightlight.skywars.world.WorldServer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReplayHook {
    private Map<WorldServer<?>, ReplayHolder> replaysMap = new HashMap<>();

    public ReplayAPI getReplayAPI() {
        return ReplayAPI.getInstance();
    }

    public Replay getReplay(WorldServer<?> server) {
        if (replaysMap.getOrDefault(server, null) != null) {
            return replaysMap.get(server).getReplay();
        }
        return null;
    }

    public ReplayHolder getReplayHolder(WorldServer<?> server) {
        return replaysMap.getOrDefault(server, null);
    }

    public void record(WorldServer<?> server) {
        Replay replay = getReplayAPI().recordReplay(server.getServerName() + "_" + System.nanoTime(), server.getInitialPlayers());
        replaysMap.put(server, new ReplayHolder(replay, server.getConfig().getWorldName()));
    }

    public void play(GameData data, Player p) {
        ReplayData replayData = data.getReplay();

        File zipFile = new File("plugins/LostSkyWars/maps", replayData.getArenaId() + ".zip");

        File worldFolder = new File(Bukkit.getWorldContainer(), replayData.getWorldName());

        if (!worldFolder.exists()) {
            try {
                ZipUtils.unzip(zipFile, worldFolder);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            WorldCreator wc = WorldCreator.name(replayData.getWorldName());
            wc.generateStructures(false);
            World world = wc.createWorld();

            world.setTime(0L);
            world.setStorm(false);
            world.setThundering(false);
            world.setAutoSave(false);
            world.setAnimalSpawnLimit(0);
            world.setWaterAnimalSpawnLimit(0);
            world.setKeepSpawnInMemory(false);
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("mobGriefing", "false");
        }

        getReplayAPI().playReplay(replayData.getReplayId(), p);
    }
}
