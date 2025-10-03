package org.twightlight.skywars.hook.guilds.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class LevelConfig extends YamlWrapper {
    public LevelConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();

        for (int i = 1 ; i <= 10; i++) {
            yml.addDefault("level."+ i +".material", "DIAMOND");
            yml.addDefault("level."+ i +".requiredXP", 2460 + (540 * (i * i)));
            yml.addDefault("level."+ i +".donation_limit", 800 + (220 * i));
            yml.addDefault("level."+ i +".ratio", BigDecimal.valueOf(0.70D + (0.05D * i)).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        yml.addDefault("level.max.material", "DIAMOND_BLOCK");
        yml.addDefault("level.max.requiredXP", 999999);
        yml.addDefault("level.max.donation_limit", 3500);
        yml.addDefault("level.max.ratio", 1.25);
        yml.options().copyDefaults(true);
        save();
    }

    private java.util.List<String> list(String... lines) {
        return java.util.Arrays.asList(lines);
    }
}
