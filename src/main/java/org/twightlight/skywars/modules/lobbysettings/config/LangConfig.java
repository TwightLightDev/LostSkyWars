package org.twightlight.skywars.modules.lobbysettings.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;

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

        yml.addDefault("lobbysettings.fly.usage", "&6/lobbysettings fly <on/off> &f- &7Toggle fly mode.");

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

        yml.addDefault("lobbysettings.speed.usage", "&6/lobbysettings speed <level> &f- &7Modify your speed.");

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
        yml.addDefault("lobbysettings.jumpboost.usage", "&6/lobbysettings jumpboost <level> &f- &7Modify your jumpboost.");

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

        yml.addDefault("lobbysettings.vanish.usage", "&6/lobbysettings vanish <on/off> &f- &7Toggle vanish mode.");


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
        yml.addDefault("lobbysettings.scoreboard.usage", "&6/lobbysettings scoreboard <on/off> &f- &7Toggle scoreboard visibility.");

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
        yml.addDefault("lobbysettings.particles.usage", "&6/lobbysettings particles <on/off> &f- &7Toggle particles visibility.");

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
        yml.addDefault("lobbysettings.chat.usage", "&6/lobbysettings chat <on/off> &f- &7Toggle chat visibility.");

        yml.addDefault("lobbysettings.players.on", list(
                "&9&l-------------------------------------------",
                "&aPlayers are now visible.!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.players.off", list(
                "&9&l-------------------------------------------",
                "&aPlayers has been hidden!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("lobbysettings.players.usage", "&6/lobbysettings players <on/off> &f- &7Toggle players visibility.");

        yml.addDefault("lobbysettings.blood.on", list(
                "&9&l-------------------------------------------",
                "&aBlood are now visible.!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("lobbysettings.blood.off", list(
                "&9&l-------------------------------------------",
                "&aBlood has been hidden!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("lobbysettings.blood.usage", "&6/lobbysettings blood <on/off> &f- &7Toggle blood visibility.");


        yml.addDefault("lobbysettings.help", list(
                "§dLobbySettings - Help",
                "&6/lobbysettings menu &f- &7Open the lobby settings menu.",
                "{cmds}"
        ));
        yml.addDefault("lobbysettings.help.usage", "&6/lobbysettings help &f- &7Prints this help message.");


        yml.options().copyDefaults(true);
        save();
    }

    private java.util.List<String> list(String... lines) {
        return Arrays.asList(lines);
    }
}
