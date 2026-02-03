package org.twightlight.skywars.modules.quests.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;

import java.util.Arrays;

public class LangConfig extends YamlWrapper {

    public LangConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();
        String path = "messages";

        yml.addDefault(path + ".quest-already-started", Arrays.asList("&cYou've already started the &6{displayname} &cquest!", "&eStatus:", "&b1. &7{description} &b(&6{currentprogress}&b/&6{requiredprogress}&b)"));
        yml.addDefault(path + ".quest-completed", "&cYou can't start this quest right now!");
        yml.addDefault(path + ".on-click-challenge", "&cYou don't have to manually start Challenges! Join a game and complete them!");
        yml.addDefault(path + ".need-rank", "&cYou need to be &bMVP&c+ &cto use this feature!");
        yml.addDefault(path + ".commands.no-permission", "&cYou don't have enough permission!");
        yml.addDefault(path + ".commands.command-not-found", "&fUnknown command. Type \"/help\" for help.");

        yml.options().copyDefaults(true);
        save();
    }
}
