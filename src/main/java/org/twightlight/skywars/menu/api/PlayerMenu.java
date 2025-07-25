package org.twightlight.skywars.menu.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.twightlight.skywars.SkyWars;

public class PlayerMenu extends Menu implements Listener {

    protected Player player;

    public PlayerMenu(Player player, String title) {
        this(player, title, 6);
    }

    public PlayerMenu(Player player, String title, int rows) {
        super(title, rows);
        this.player = player;
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, SkyWars.getInstance());
    }

    public void open() {
        this.player.openInventory(getInventory());
    }

    public Player getPlayer() {
        return player;
    }
}
