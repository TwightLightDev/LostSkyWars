package org.twightlight.skywars.modules.boosters.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.libs.yaml.YamlWrapper;

import java.util.Arrays;


public class LangConfig extends YamlWrapper {
    public LangConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();
        yml.addDefault("boosters.general.no-permission", "&cYou don't have enough permission!");
        yml.addDefault("boosters.general.command-not-found", "&fUnknown command. Type \"/help\" for help.");

        yml.addDefault("boosters.display.item-name", "&b{time} Booster &f- {color}{amplifier}x {currency}");
        yml.addDefault("boosters.display.item-lore", list("{status}"));
        yml.addDefault("boosters.status.in-storage", list("&aClick to activate this booster!"));
        yml.addDefault("boosters.status.in-queue", list("&aThis booster is in position #{pos}!", "", "&cRight Click to remove this booster from queue!", "&eShift Right Click to promote to top!"));
        yml.addDefault("boosters.status.in-activate", list("&aTime left: {timeleft}", "", "&cRight Click to remove this booster!", "&8(Note that there will be no refund!)"));

        yml.options().copyDefaults(true);
        save();
    }

    private java.util.List<String> list(String... lines) {
        return Arrays.asList(lines);
    }
}
