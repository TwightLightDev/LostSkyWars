package org.twightlight.skywars.menu.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.twightlight.skywars.SkyWars;

public class PagedPlayerMenu extends PagedMenu implements Listener {

    protected Player player;

    public PagedPlayerMenu(Player player, String name) {
        this(player, name, 1);
    }

    public PagedPlayerMenu(Player player, String name, int rows) {
        super(name, rows);
        this.player = player;
    }

    public void open() {
        player.openInventory(this.menus.get(0).getInventory());
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, SkyWars.getInstance());
    }

    public void openPrevious() {
        if (this.currentPage == 1) {
            return;
        }

        this.currentPage--;
        player.openInventory(this.menus.get(this.currentPage - 1).getInventory());
    }

    public void openNext() {
        if (this.currentPage + 1 > this.menus.size()) {
            return;
        }

        this.currentPage++;
        player.openInventory(this.menus.get(this.currentPage - 1).getInventory());
    }
}
