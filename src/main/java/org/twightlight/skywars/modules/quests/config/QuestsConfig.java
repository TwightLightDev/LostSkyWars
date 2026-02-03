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
        set(path + ".first-win.settings.type", "lsw_game_win");
        set(path + ".first-win.settings.condition", 1);
        set(path + ".first-win.settings.schedule.unit", "DAYS");
        set(path + ".first-win.settings.schedule.value", 1);

        set(path + ".first-win.placeholders.display-name", "Daily Quest: First Win of the Day");
        set(path + ".first-win.placeholders.short-description", "Win a game of Sky Wars");

        set(path + ".first-win.placeholders.complete", Arrays.asList(
                "",
                "&a{displayname} Completed!",
                " &8+&d250 &7Sky Wars Experience",
                " &8+&6250 &7Sky Wars Coins",
                ""
        ));

        set(path + ".first-win.placeholders.start", Arrays.asList(
                "&aYou started the &6{displayname} &aquest!"
        ));

        set(path + ".first-win.menu.quest-not-started.material", "PAPER");
        set(path + ".first-win.menu.quest-not-started.name", "&aDaily Quest: First Win of the Day");
        set(path + ".first-win.menu.quest-not-started.lore", Arrays.asList(
                "&7Win a game of Bed Wars",
                "&r ",
                "&7Rewards:",
                "&8+&d250 Bed Wars Experience",
                "&8+&6250 Bed Wars Coins",
                "&r ",
                "&8&oDaily Quests can be completed",
                "&8&oonce every day.",
                "&r ",
                "{status}"
        ));

        set(path + ".first-win.menu.quest-started.material", "PAPER");
        set(path + ".first-win.menu.quest-started.name", "&aDaily Quest: First Win of the Day");
        set(path + ".first-win.menu.quest-started.lore", Arrays.asList(
                "&7Win a game of Bed Wars",
                "&b(&6{currentprogress}&b/&6{requiredprogress}&b)",
                "&r ",
                "&7Rewards:",
                "&8+&d250 Bed Wars Experience",
                "&8+&6250 Bed Wars Coins",
                "&r ",
                "&8&oDaily Quests can be completed",
                "&8&oonce every day.",
                "&r ",
                "{status}"
        ));

        set(path + ".first-win.menu.quest-completed.material", "EMPTY_MAP");
        set(path + ".first-win.menu.quest-completed.name", "&aDaily Quest: First Win of the Day");
        set(path + ".first-win.menu.quest-completed.lore", Arrays.asList(
                "&7Win a game of Bed Wars",
                "&r ",
                "&7Rewards:",
                "&8+&b250 Bed Wars Experience",
                "&r ",
                "&8&oDaily Quests can be completed",
                "&8&oonce every day.",
                "&r ",
                "{status}"
        ));

        set(path + ".first-win.menu.slot", 14);
        set(path + ".first-win.menu.pages", Arrays.asList(1));

        set(path + ".first-win.rewards", Arrays.asList(
                "[exp] 250",
                "[coins] 250",
                "[message] &d+250 Bed Wars Experience (Quest Bonus)!",
                "[message] &6+250 Bed Wars Coins (Quest Bonus)!"
        ));
        yml.options().copyDefaults(true);
        save();
    }
}
