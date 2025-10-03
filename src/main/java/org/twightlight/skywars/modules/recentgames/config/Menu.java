package org.twightlight.skywars.modules.recentgames.config;

import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.api.yaml.MenuConfig;

import java.util.Arrays;

public class Menu extends MenuConfig {

    public Menu(Plugin pl, String name, String dir, String module) {
        super(pl, name, dir, module);
        addDefault("menu.name", "&7Recent Games");
        addDefault("menu.size", 36);
        addDefault("menu.paginated-slot", "10,12,14,16");
        addDefault("menu.items.close.material", "BARRIER");
        addDefault("menu.items.close.name", "&cClose");
        addDefault("menu.items.close.slot", 31);

        addDefault("menu.items.no-game.material", "BEDROCK");
        addDefault("menu.items.no-game.name", "&cYou don't have any recent game!");
        addDefault("menu.items.no-game.slot", 13);

        addDefault("menu.items.next-page.material", "ARROW");
        addDefault("menu.items.next-page.name", "&aNext page");
        addDefault("menu.items.next-page.slot", 35);

        addDefault("menu.items.previous-page.material", "ARROW");
        addDefault("menu.items.previous-page.name", "&aPrevious page");
        addDefault("menu.items.previous-page.slot", 27);


        addDefault("menu.items.game-item.material", "PAPER");
        addDefault("menu.items.game-item.name", "&f{arena}");
        addDefault("menu.items.game-item.lore", Arrays.asList("&7Result: &a{result}", "&7Start Time: &a{startTime}",
                "&7Duration: &a{duration}", "", "&eClick to watch replay!"));

        finish();
    }
}
