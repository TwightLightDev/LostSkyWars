package org.twightlight.skywars.cmd.sw;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.cmd.SubCommand;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsTrail;
import org.twightlight.skywars.cosmetics.skywars.kits.InsaneSkyWarsKit;
import org.twightlight.skywars.cosmetics.skywars.kits.NormalSkyWarsKit;
import org.twightlight.skywars.cosmetics.skywars.kits.RankedSkyWarsKit;
import org.twightlight.skywars.utils.LostLogger;

@SuppressWarnings("deprecation")
public class ReloadCommand extends SubCommand {

    public static final LostLogger LOGGER = Main.LOGGER.getModule("LoadCommand");

    public ReloadCommand() {
        super("reload");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(" \n§dReload - Help\n \n§6/lsw reload kits §f- §7Reload all kits cache. \n§6/lsw reload projectiletrails §f- §7Reload all projectiletrails. ");
            return;
        }

        if (args[0].equalsIgnoreCase("kits")) {
            CosmeticServer.SKYWARS.removeByType(NormalSkyWarsKit.class);
            CosmeticServer.SKYWARS.removeByType(InsaneSkyWarsKit.class);
            CosmeticServer.SKYWARS.removeByType(RankedSkyWarsKit.class);

            NormalSkyWarsKit.setupKits();
            InsaneSkyWarsKit.setupKits();
            RankedSkyWarsKit.setupKits();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all kit's cache!"));
        } else if (args[0].equalsIgnoreCase("projectiletrails")) {
            CosmeticServer.SKYWARS.removeByType(SkyWarsTrail.class);

            SkyWarsTrail.setupProjectileTrails();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reloaded all projectile trails!"));
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
