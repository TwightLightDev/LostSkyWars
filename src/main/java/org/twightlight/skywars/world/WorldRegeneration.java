package org.twightlight.skywars.world;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.api.server.SkyWarsState;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class WorldRegeneration {

    private static BukkitTask task;
    private static final List<WorldServer<?>> queue = new ArrayList<>();

    public static void rollBack(WorldServer<?> server) {
        if (queue.contains(server) && server.isPrivate()) return;

        queue.add(server);

        if (task == null) {
            task = new BukkitRunnable() {
                WorldServer<?> rollbacking = null;

                @Override
                public void run() {
                    if (rollbacking != null) {
                        rollbacking.setState(SkyWarsState.ROLLBACKING);
                        rollbacking.getConfig().reload();
                        rollbacking.setTimer(Language.game$countdown$start + 1);
                        rollbacking.getTask().reset();
                        rollbacking.setState(SkyWarsState.WAITING);
                        rollbacking = null;
                        return;
                    }

                    if (!queue.isEmpty()) {
                        rollbacking = queue.remove(0);
                    } else {
                        cancel();
                        task = null;
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 1);
        }
    }

}
