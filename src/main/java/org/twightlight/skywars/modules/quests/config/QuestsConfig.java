package org.twightlight.skywars.modules.quests.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;

import java.util.Arrays;

public class QuestsConfig extends YamlWrapper {

    public QuestsConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();
        String path = "quests";
        addDefault(path + ".first-win.settings.type", "lsw_game_win");
        addDefault(path + ".first-win.settings.condition", 1);
        addDefault(path + ".first-win.settings.schedule.unit", "DAYS");
        addDefault(path + ".first-win.settings.schedule.value", 1);

        addDefault(path + ".first-win.placeholders.display-name", "Daily Quest: First Win of the Day");
        addDefault(path + ".first-win.placeholders.short-description", "Win a game of Sky Wars");

        addDefault(path + ".first-win.placeholders.complete", Arrays.asList(
                "",
                "&a{displayname} Completed!",
                " &8+&d250 &7Sky Wars Experience",
                " &8+&6250 &7Sky Wars Coins",
                ""
        ));

        addDefault(path + ".first-win.placeholders.start", Arrays.asList(
                "&aYou started the &6{displayname} &aquest!"
        ));

        addDefault(path + ".first-win.menu.quest-not-started.material", "PAPER");
        addDefault(path + ".first-win.menu.quest-not-started.name", "&aDaily Quest: First Win of the Day");
        addDefault(path + ".first-win.menu.quest-not-started.flags", Arrays.asList("HIDE_ATTRIBUTES", "HIDE_ENCHANTS", "HIDE_UNBREAKABLE"));
        addDefault(path + ".first-win.menu.quest-not-started.lore", Arrays.asList(
                "&7Win a game of Sky Wars",
                "&r ",
                "&7Rewards:",
                "&8+&d250 Sky Wars Experience",
                "&8+&6250 Sky Wars Coins",
                "&r ",
                "&8&oDaily Quests can be completed",
                "&8&oonce every day.",
                "&r ",
                "&aClick to start this quest!"
        ));

        addDefault(path + ".first-win.menu.quest-starting.material", "PAPER");
        addDefault(path + ".first-win.menu.quest-starting.name", "&aDaily Quest: First Win of the Day");
        addDefault(path + ".first-win.menu.quest-starting.flags", Arrays.asList("HIDE_ATTRIBUTES", "HIDE_ENCHANTS", "HIDE_UNBREAKABLE"));
        addDefault(path + ".first-win.menu.quest-starting.lore", Arrays.asList(
                "&7Win a game of Sky Wars",
                "&b(&6{currentprogress}&b/&6{requiredprogress}&b)",
                "&r ",
                "&7Rewards:",
                "&8+&d250 Sky Wars Experience",
                "&8+&6250 Sky Wars Coins",
                "&r ",
                "&8&oDaily Quests can be completed",
                "&8&oonce every day.",
                "&r ",
                "&aYou have already completed this quest!"
        ));

        addDefault(path + ".first-win.menu.quest-completed.material", "EMPTY_MAP");
        addDefault(path + ".first-win.menu.quest-completed.name", "&aDaily Quest: First Win of the Day");
        addDefault(path + ".first-win.menu.quest-completed.flags", Arrays.asList("HIDE_ATTRIBUTES", "HIDE_ENCHANTS", "HIDE_UNBREAKABLE"));

        addDefault(path + ".first-win.menu.quest-completed.lore", Arrays.asList(
                "&7Win a game of Sky Wars",
                "&r ",
                "&7Rewards:",
                "&8+&b250 Sky Wars Experience",
                "&r ",
                "&8&oDaily Quests can be completed",
                "&8&oonce every day.",
                "&r ",
                "&aYou have completed this quest!"
        ));

        addDefault(path + ".first-win.menu.slot", 11);
        addDefault(path + ".first-win.menu.pages", Arrays.asList(1));

        addDefault(path + ".first-win.rewards", Arrays.asList(
                "[exp] 250",
                "[coins] 250",
                "[message] &d+250 Sky Wars Experience (Quest Bonus)!",
                "[message] &6+250 Sky Wars Coins (Quest Bonus)!"
        ));
        yml.options().copyDefaults(true);
        save();
    }
}
