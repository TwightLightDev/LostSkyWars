package org.twightlight.skywars.arena;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.ui.chest.SkyWarsChest;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class RollBackManager {

    private static BukkitTask task;
    private static final List<Arena> queue = new ArrayList<>();

    public static void rollBack(Arena server) {
        if (queue.contains(server)) return;

        queue.add(server);

        if (task == null) {
            task = new BukkitRunnable() {
                Arena rollbacking = null;

                @Override
                public void run() {
                    if (rollbacking != null) {
                        if (!server.isPrivate()) {
                            rollbacking.setState(SkyWarsState.ROLLBACKING);
                            rollbacking.getConfig().reload(rollbacking);
                            rollbacking.getChests().forEach(SkyWarsChest::reset);
                            rollbacking.setTimer(Language.game$countdown$start + 1);
                            rollbacking.getTimerTask().reset();
                            rollbacking = null;
                            return;
                        } else {
                            Arena.removeArena(server);

                        }
                    }

                    if (!queue.isEmpty()) {
                        rollbacking = queue.remove(0);
                    } else {
                        cancel();
                        task = null;
                    }
                }
            }.runTaskTimer(SkyWars.getInstance(), 0, 1);
        }
    }

}
