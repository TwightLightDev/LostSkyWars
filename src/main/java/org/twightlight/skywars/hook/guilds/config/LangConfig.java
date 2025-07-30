package org.twightlight.skywars.hook.guilds.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.YamlWrapper;

import java.util.Arrays;


public class LangConfig extends YamlWrapper {
    public LangConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();

        yml.addDefault("guilds.donation.success", Arrays.asList(
                "&b&m-----------------------------------------------------",
                "            &e&lDONATION SUMMARY",
                "&7You donated &6{pcoins} Personal Coins&7!",
                "&7You received &b{gcoins} Guild Coins&7!",
                "&7You received &2{gexp} Guild EXP&7!",
                "&b&m-----------------------------------------------------"
        ));
        yml.addDefault("guilds.donation.limit-reached", Arrays.asList(
                "&b&m-----------------------------------------------------",
                "&cYou have reached donation limit!",
                "&b&m-----------------------------------------------------"
        ));
        yml.addDefault("guilds.donation.not-in-guild", Arrays.asList(
                "&b&m-----------------------------------------------------",
                "&cYou are not in a guild!",
                "&b&m-----------------------------------------------------"
        ));
        yml.addDefault("guilds.donation.lack-of-money", Arrays.asList(
                "&b&m-----------------------------------------------------",
                "&cYou don't have enough money!",
                "&b&m-----------------------------------------------------"
        ));
        yml.addDefault("guilds.donation.minimum", Arrays.asList(
                "&b&m-----------------------------------------------------",
                "&cYou must donate at least &6100 Personal Coins&c!",
                "&b&m-----------------------------------------------------"
        ));
        yml.addDefault("guilds.donation.help.general", Arrays.asList(
                "&b&m-----------------------------------------------------",
                "&aGuild Donation Commands:",
                "&e/guilddonation help - &bPrints this help message.",
                "&e/guilddonation donate <amount> - &bDonate to your guild.",
                "&e/guilddonation menu - &bOpen the donation menu.",
                "&e/guilddonation shop - &bView the guild shop.",
                "&b&m-----------------------------------------------------"
        ));
        yml.addDefault("guilds.donation.help.coins", Arrays.asList(
                "&b&m-----------------------------------------------------",
                "&aGuild Coins Commands:",
                "&e/guildcoins give <player> <amount> - &bGive coins to a player.",
                "&e/guildcoins set <player> <amount> - &bSet coins of a player.",
                "&e/guildcoins take <player> <amount> - &bTake coins from a player.",
                "&e/guildcoins reset <player> - &bReset player's balance.",
                "&e/guildcoins bal <player> - &bView a player's balance.",
                "&b&m-----------------------------------------------------"
        ));
        yml.addDefault("guilds.guild-coin.balance", Arrays.asList(
                "&b&m-----------------------------------------------------",
                "&eYour balance: {amount} &aGuild Coin(s)&e.",
                "&b&m-----------------------------------------------------"
        ));
        yml.options().copyDefaults(true);
        save();
    }

    private java.util.List<String> list(String... lines) {
        return java.util.Arrays.asList(lines);
    }
}
