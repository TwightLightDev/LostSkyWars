package org.twightlight.skywars.modules.boosters.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;

import java.util.Arrays;


public class LangConfig extends YamlWrapper {
    public LangConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();

        yml.addDefault("boosters.display.item-name", "&b{time} Booster &f- {color}{amplifier}x {currency}");
        yml.addDefault("boosters.display.item-lore", list("{status}"));
        yml.addDefault("boosters.status.in-storage", list("&eClick to activate this booster!"));
        yml.addDefault("boosters.status.in-storage-queue", list("&eClick to add this booster to queue!"));
        yml.addDefault("boosters.status.in-personal-queue", list("&aThis booster is in position #{pos}!", "&aBooster's owner: {owner}", "", "&eRight Click to remove this booster from queue!", "&eShift Right Click to promote to top!"));
        yml.addDefault("boosters.status.in-network-queue", list("&aThis booster is in position #{pos}!", "&aBooster's owner: {owner}", "", "{canremove}"));
        yml.addDefault("boosters.status.in-activate", list("&aTime left: {timeleft}", "&aBooster's owner: {owner}", "", "{canremove}", "&8(Note that there will be no refund!)"));
        yml.addDefault("boosters.status.can-remove", "&eRight Click to remove this booster!");
        yml.addDefault("boosters.status.cannot-remove", "&cThis is not your booster!");

        yml.addDefault("messages.boosters.active", list(
                "&9&l-------------------------------------------",
                "&aYou activated {booster}&a booster!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("messages.boosters.deactivate", list(
                "&9&l-------------------------------------------",
                "&aYou deactivated {booster}&a booster!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("messages.boosters.unqueue", list(
                "&9&l-------------------------------------------",
                "&aYou removed a {booster}&a booster from queue!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("messages.boosters.queue", list(
                "&9&l-------------------------------------------",
                "&aYou added {booster}&a booster to queue!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("messages.boosters.give", list(
                "&9&l-------------------------------------------",
                "&aYou gave x{amount} {booster} booster to {player}!",
                "&9&l-------------------------------------------"
        ));

        yml.addDefault("messages.commands.help", list(
                "§dBoosters - Help",
                "&6/boosters help &f- &7Prints this help message.",
                "{cmds}"
        ));

        yml.addDefault("messages.commands.usages.menu", "&6/boosters menu &f- &7Open the boosters menu.");
        yml.addDefault("messages.commands.usages.give", "&6/boosters give <player> <id> <amount> &f- &7Give booster to a player.");
        yml.addDefault("messages.commands.usages.give", "&6/boosters reload &f- &7Reload all configs.");

        yml.addDefault("messages.commands.no-permission", "&cYou don't have enough permission!");
        yml.addDefault("messages.commands.command-not-found", "&fUnknown command. Type \"/help\" for help.");

        yml.options().copyDefaults(true);
        save();
    }

    private java.util.List<String> list(String... lines) {
        return Arrays.asList(lines);
    }
}
