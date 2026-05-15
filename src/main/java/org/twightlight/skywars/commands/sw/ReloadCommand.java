package org.twightlight.skywars.commands.sw;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.commands.SubCommand;
import org.twightlight.skywars.cosmetics.visual.categories.*;

@SuppressWarnings("deprecation")
public class ReloadCommand extends SubCommand {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("LoadCommand");

    public ReloadCommand() {
        super("reload");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(" \n§dReload - Help\n \n§6/lsw reload kits §f- §7Reload all kits cache. \n§6/lsw reload projectiletrails §f- §7Reload all projectiletrails. \n§6/lsw reload balloons §f- §7Reload all balloons. \n§6/lsw reload deathcries §f- §7Reload all deathcries. \n§6/lsw reload killeffects §f- §7Reload all killeffects. \n§6/lsw reload killmessages §f- §7Reload all killmessages. \n§6/lsw reload killeffects §f- §7Reload all killeffects. \n§6/lsw reload sprays §f- §7Reload all sprays. \n§6/lsw reload victorydances §f- §7Reload all victorydances. \n§6/lsw reload cages §f- §7Reload all cages. ");
            return;
        }

        if (args[0].equalsIgnoreCase("kits")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsKit.class);

            SkyWarsKit.setupKits();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all kit's cache!"));
        } else if (args[0].equalsIgnoreCase("projectiletrails")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsTrail.class);

            SkyWarsTrail.setupProjectileTrails();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all projectile trails!"));
        } else if (args[0].equalsIgnoreCase("cages")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsCage.class);

            SkyWarsCage.setupCages();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all cages!"));
        } else if (args[0].equalsIgnoreCase("deathcries")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsDeathCry.class);

            SkyWarsDeathCry.setupDeathCries();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all deathcries!"));
        } else if (args[0].equalsIgnoreCase("killeffects")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsKillEffect.class);

            SkyWarsKillEffect.setupKillEffects();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all killeffects!"));
        } else if (args[0].equalsIgnoreCase("killmessages")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsKillMessage.class);

            SkyWarsKillMessage.setupKM();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all killmessages!"));
        } else if (args[0].equalsIgnoreCase("sprays")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsSpray.class);

            SkyWarsSpray.setupSprays();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all sprays!"));
        } else if (args[0].equalsIgnoreCase("victorydances")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsVictoryDance.class);

            SkyWarsVictoryDance.setupVictoryDances();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all victorydances!"));
        } else if (args[0].equalsIgnoreCase("balloons")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsBalloon.class);

            SkyWarsBalloon.setupBallons();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all balloons!"));
        }
    }

    @Override
    public void perform(Player player, String[] args) {
    }

    @Override
    public String getUsage() {
        return "reload <module>";
    }

    @Override
    public String getDescription() {
        return "Reload a specific module.";
    }

    @Override
    public boolean onlyForPlayer() {
        return false;
    }
}
