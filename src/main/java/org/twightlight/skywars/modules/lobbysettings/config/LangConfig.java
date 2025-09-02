package org.twightlight.skywars.modules.lobbysettings.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.libs.yaml.YamlWrapper;

import java.util.Arrays;


public class LangConfig extends YamlWrapper {
    public LangConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();
        yml.addDefault("lobbysettings.general.no-permission", "&cYou don't have enough permission!");
        yml.addDefault("lobbysettings.general.command-not-found", "&fUnknown command. Type \"/help\" for help.");

        yml.addDefault("lobbysettings.fly.on", list(
                "&9&l-------------------------------------------",
                "&aFly enabled!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("lobbysettings.fly.off", list(
                "&9&l-------------------------------------------",
                "&aFly disabled!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.speed.on", list(
                "&9&l-------------------------------------------",
                "&aSet your speed boost to &d[{amplifier}]&a!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.speed.off", list(
                "&9&l-------------------------------------------",
                "&aRemoved your speed boost!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.jumpboost.on", list(
                "&9&l-------------------------------------------",
                "&aSet your jump boost to &d[{amplifier}]&a!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.jumpboost.off", list(
                "&9&l-------------------------------------------",
                "&aRemoved your jump boost!",
                "&9&l-------------------------------------------"
        ));


        yml.addDefault("lobbysettings.vanish.on", list(
                "&9&l-------------------------------------------",
                "&aYou are now &c&lVanished&a!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.vanish.off", list(
                "&9&l-------------------------------------------",
                "&aYou are now &bVisible&a!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.scoreboard.on", list(
                "&9&l-------------------------------------------",
                "&aThe scoreboard is now visible.!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.scoreboard.off", list(
                "&9&l-------------------------------------------",
                "&aThe scoreboard has been hidden!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.particles.on", list(
                "&9&l-------------------------------------------",
                "&aParticles are now visible.!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.particles.off", list(
                "&9&l-------------------------------------------",
                "&aParticles has been hidden!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.chat.on", list(
                "&9&l-------------------------------------------",
                "&aChat are now visible.!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.chat.off", list(
                "&9&l-------------------------------------------",
                "&aChat has been hidden!",
                "&9&l-------------------------------------------"
        ));


        yml.options().copyDefaults(true);
        save();
    }

    private java.util.List<String> list(String... lines) {
        return Arrays.asList(lines);
    }
}
