package org.twightlight.skywars.menu.api;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.SkyWars;

public abstract class UpdatableMenu extends Menu implements Listener {

    private BukkitTask task;

    public UpdatableMenu(String name) {
        this(name, 21);
    }

    public UpdatableMenu(String name, int rows) {
        super(name, rows);
    }

    public void register(long updateEveryTicks) {
        Bukkit.getPluginManager().registerEvents(this, SkyWars.getInstance());
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(SkyWars.getInstance(), 0, updateEveryTicks);
    }

    public void cancel() {
        this.task.cancel();
        this.task = null;
    }

    public abstract void update();
}
