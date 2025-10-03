package org.twightlight.skywars.modules.privategames.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;

import java.util.Arrays;


public class LangConfig extends YamlWrapper {
    public LangConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();

        yml.addDefault("privategames.party.not_found", Arrays.asList(
                "&9&l&m----------------------------",
                "&cYou are not currently in a party.",
                "&9&l&m----------------------------"
        ));
        yml.addDefault("privategames.no_permission", Arrays.asList(
                "&9&l&m----------------------------",
                "&cYou do not have permission to use this command! You need",
                "&bMVP &cor higher.",
                "&9&l&m----------------------------"
        ));
        yml.addDefault("privategames.party.not_leader", Arrays.asList(
                "&9&l&m----------------------------",
                "&cOnly the Party leader can start a match.",
                "&9&l&m----------------------------"
        ));
        yml.addDefault("privategames.toggle.enable", Arrays.asList(
                "&9&l&m----------------------------",
                "&7{player} &aenabled Private Game",
                "&9&l&m----------------------------"
        ));
        yml.addDefault("privategames.toggle.disable", Arrays.asList(
                "&9&l&m----------------------------",
                "&7{player} &cdisabled Private Game",
                "&9&l&m----------------------------"
        ));
        yml.addDefault("privategames.help", list(
                "§dPrivateGames - Help",
                "&6/privategames help &f- &7Prints this help message.",
                "&6/privategames gui &f- &7Open the private games menu.",
                "&6/privategames toggle &f- &7Enable/disable private games."
        ));
        yml.addDefault("privategames.modifiers.message", list(
                "&6-----------------------------------------------------",
                "{player} &a&lhas enabled private game modifiers!",
                "{modifiers}",
                "&6-----------------------------------------------------"
        ));

        yml.addDefault("privategames.modifiers.format", "&6- {modifier}");
        yml.addDefault("privategames.modifiers.format_with_option", "&6- {modifier}: &e{selected}");

        yml.options().copyDefaults(true);
        save();
    }

    private java.util.List<String> list(String... lines) {
        return Arrays.asList(lines);
    }
}
