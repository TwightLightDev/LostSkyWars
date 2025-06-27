package org.twightlight.skywars.modules.recentgames.listeners;

import me.jumper251.replay.api.ReplaySessionFinishEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.utils.FileUtils;

import java.io.File;

public class ReplayFinishEvent implements Listener {
    @EventHandler
    public void onReplayFinish(ReplaySessionFinishEvent e) {
        Player p = e.getPlayer();

        if (p.getWorld().getPlayers().isEmpty()) {
            File oldWorldFolder = new File(Bukkit.getWorldContainer(), p.getWorld().getName());
            Bukkit.unloadWorld(e.getPlayer().getWorld(), false);
            FileUtils.deleteFile(oldWorldFolder);
        }
    }
}
