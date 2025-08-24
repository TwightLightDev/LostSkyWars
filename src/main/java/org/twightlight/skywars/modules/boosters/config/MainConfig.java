package org.twightlight.skywars.modules.boosters.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.libs.yaml.YamlWrapper;

public class MainConfig extends YamlWrapper {
    public MainConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();
        yml.addDefault("general.active.cap.3", "boosters.active.3");
        yml.addDefault("general.active.cap.2", "boosters.active.2");
        yml.addDefault("general.active.cap.1", "boosters.active.1");
        yml.addDefault("general.queue.cap.3", "boosters.queue.3");
        yml.addDefault("general.queue.cap.2", "boosters.queue.2");
        yml.addDefault("general.queue.cap.1", "boosters.queue.1");

        yml.options().copyDefaults(true);
        save();
    }
}
