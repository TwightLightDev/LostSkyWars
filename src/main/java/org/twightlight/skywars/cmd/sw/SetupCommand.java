package org.twightlight.skywars.cmd.sw;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.cmd.SubCommand;
import org.twightlight.skywars.setup.Menu;
import org.twightlight.skywars.setup.kits.KitsSetupMenu;
import org.twightlight.skywars.utils.ConfigUtils;

public class SetupCommand extends SubCommand {

    public SetupCommand() {
        super("setup");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(" \n§dSetup - Help\n \n§6/lsw setup kits <id> <normal/insane/ranked> §f- §7Open kit setup menu.\n ");
            return;
        }

        String action = args[0];
        if (action.equalsIgnoreCase("kits")) {
            if (args.length < 3) {
                player.sendMessage("§cUse /lsw setup kits <id> <normal/insane/ranked>");
                return;
            }
            if (!args[2].equals("normal") && !args[2].equals("insane") && !args[2].equals("ranked")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFile not found!"));
                return;
            }
            ConfigUtils config = ConfigUtils.getConfig(args[2]+"kits", "plugins/LostSkyWars/kits");
            if (config != null) {
                Menu menu = KitsSetupMenu.init(config, args[1]);
                menu.open(player);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFile not found!"));
            }
        }
    }

    @Override
    public String getUsage() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return "Fast setup through gui.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }
}
