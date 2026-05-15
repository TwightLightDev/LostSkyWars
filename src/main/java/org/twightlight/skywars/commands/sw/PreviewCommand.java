package org.twightlight.skywars.commands.sw;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.commands.SubCommand;
import org.twightlight.skywars.cosmetics.PreviewableCosmetic;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;
import org.twightlight.skywars.utils.StringUtils;

public class PreviewCommand extends SubCommand {

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("cosmeticspreview");

    public PreviewCommand() {
        super("preview");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(" \n§dPreview - Help\n \n§6/lsw player-location cages §f- §7Set cages player location. \n§6/lsw player-location sprays §f- §7Set sprays player location. \n§6/lsw player-location balloons §f- §7Set balloons player location. \n§6/lsw player-location killeffects §f- §7Set killeffects player location. \n§6/lsw player-location trails §f- §7Set trails player location. \n§6/lsw preview cages <small/big> §f- §7Set cages preview location. \n§6/lsw preview sprays §f- §7Set sprays preview location. \n§6/lsw preview balloons §f- §7Set balloons preview location. \n§6/lsw preview killeffects <attacker/victim> §f- §7Set killeffects preview location. \n§6/lsw preview trails §f- §7Set trails preview location. \n§6/lsw preview reload §f- §7Reload all preview config. ");
            return;
        }

        if (args[0].equalsIgnoreCase("player-location")) {
            if (args.length < 2) {
                player.sendMessage(StringUtils.formatColors("&aYou must type a valid cosmetic type!"));

                return;
            }

            if (args[1].equalsIgnoreCase("cages")) {
                CONFIG.set("player-location.cages", BukkitUtils.serializeLocation(player.getLocation()));
                player.sendMessage(StringUtils.formatColors("&aSuccessfully saved cages location!"));

            } else if (args[1].equalsIgnoreCase("sprays")) {
                CONFIG.set("player-location.sprays", BukkitUtils.serializeLocation(player.getLocation()));
                player.sendMessage(StringUtils.formatColors("&aSuccessfully saved sprays location!"));
            } else if (args[1].equalsIgnoreCase("balloons")) {
                CONFIG.set("player-location.balloons", BukkitUtils.serializeLocation(player.getLocation()));
                player.sendMessage(StringUtils.formatColors("&aSuccessfully saved balloons location!"));
            } else if (args[1].equalsIgnoreCase("killeffects")) {
                CONFIG.set("player-location.killeffects", BukkitUtils.serializeLocation(player.getLocation()));
                player.sendMessage(StringUtils.formatColors("&aSuccessfully saved killeffects location!"));
            } else if (args[1].equalsIgnoreCase("trails")) {
                CONFIG.set("player-location.trails", BukkitUtils.serializeLocation(player.getLocation()));
                player.sendMessage(StringUtils.formatColors("&aSuccessfully saved trails location!"));
            } else {
                player.sendMessage(StringUtils.formatColors("&aYou must type a valid cosmetic type!"));
            }

        } else if (args[0].equalsIgnoreCase("preview-location")) {
            if (args.length < 2) {
                player.sendMessage(StringUtils.formatColors("&aYou must type a valid cosmetic type!"));

                return;
            }

            if (args[1].equalsIgnoreCase("cages")) {
                if (args.length < 3) {
                    CONFIG.set("preview-location.cages.small", BukkitUtils.serializeLocation(player.getLocation()));
                    player.sendMessage(StringUtils.formatColors("&aSuccessfully saved small cages location!"));

                    return;
                }

                if (args[2].equalsIgnoreCase("small")) {
                    CONFIG.set("preview-location.cages.small", BukkitUtils.serializeLocation(player.getLocation()));
                    player.sendMessage(StringUtils.formatColors("&aSuccessfully saved small cages location!"));
                } else if (args[2].equalsIgnoreCase("big")) {
                    CONFIG.set("preview-location.cages.big", BukkitUtils.serializeLocation(player.getLocation()));
                    player.sendMessage(StringUtils.formatColors("&aSuccessfully saved big cages location!"));
                }

            } else if (args[1].equalsIgnoreCase("sprays")) {
                CONFIG.set("preview-location.sprays", BukkitUtils.serializeLocation(player.getLocation()));
                player.sendMessage(StringUtils.formatColors("&aSuccessfully saved sprays location!"));
            } else if (args[1].equalsIgnoreCase("balloons")) {
                CONFIG.set("preview-location.balloons", BukkitUtils.serializeLocation(player.getLocation()));
                player.sendMessage(StringUtils.formatColors("&aSuccessfully saved balloons location!"));
            } else if (args[1].equalsIgnoreCase("killeffects")) {
                if (args.length < 3) {
                    CONFIG.set("preview-location.killeffects.attacker", BukkitUtils.serializeLocation(player.getLocation()));
                    player.sendMessage(StringUtils.formatColors("&aSuccessfully saved attacker killeffects location!"));

                    return;
                }

                if (args[2].equalsIgnoreCase("attacker")) {
                    CONFIG.set("preview-location.killeffects.attacker", BukkitUtils.serializeLocation(player.getLocation()));
                    player.sendMessage(StringUtils.formatColors("&aSuccessfully saved attacker killeffects location!"));
                } else if (args[2].equalsIgnoreCase("victim")) {
                    CONFIG.set("preview-location.killeffects.victim", BukkitUtils.serializeLocation(player.getLocation()));
                    player.sendMessage(StringUtils.formatColors("&aSuccessfully saved victim killeffects location!"));
                }
            } else if (args[1].equalsIgnoreCase("trails")) {
                CONFIG.set("preview-location.trails", BukkitUtils.serializeLocation(player.getLocation()));
                player.sendMessage(StringUtils.formatColors("&aSuccessfully saved trails location!"));
            } else {
                player.sendMessage(StringUtils.formatColors("&aYou must type a valid cosmetic type!"));
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            PreviewableCosmetic.getPreviewConfig().reload();
            player.sendMessage(StringUtils.formatColors("&aSuccessfully reloaded preview config!"));

        }
    }

    @Override
    public String getUsage() {
        return "setlobby";
    }

    @Override
    public String getDescription() {
        return "Set lobby location of your server.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }

    public static Location getSpawnLocation() {
        if (CONFIG.contains("lobby")) {
            return BukkitUtils.deserializeLocation(CONFIG.getString("lobby"));
        }

        return Bukkit.getWorlds().get(0).getSpawnLocation();
    }
}
