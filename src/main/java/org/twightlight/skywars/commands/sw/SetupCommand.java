package org.twightlight.skywars.commands.sw;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.commands.SubCommand;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsCage;
import org.twightlight.skywars.setup.api.Menu;
import org.twightlight.skywars.setup.cage.CageSetupSession;
import org.twightlight.skywars.setup.chests.Browser;
import org.twightlight.skywars.setup.kits.KitsSetup;
import org.twightlight.skywars.config.YamlWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            player.sendMessage(" \n§dSetup - Help\n \n§6/lsw setup kits <id> §f- §7Open kit setup menu.\n \n§6/lsw setup chesttypes §f- §7Open chesttypes setup menu.\n \n§6/lsw setup cage §f- §7Manage your cage setup.\n ");
            return;
        }

        String action = args[0];
        if (action.equalsIgnoreCase("kits")) {
            if (args.length < 2) {
                player.sendMessage(" \n§dSetup - Help\n \n§6/lsw setup kits <id> §f- §7Open kit setup menu.\n ");
                return;
            }
            YamlWrapper config = YamlWrapper.getConfig( "kits", "plugins/LostSkyWars");
            if (config != null) {
                Menu menu = KitsSetup.init(config, args[1]);
                menu.open(player);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFile not found!"));
            }
        } else if (action.equalsIgnoreCase("chesttypes")) {
            Browser.init(YamlWrapper.getConfig("chesttypes")).open(player);
        } else if (action.equalsIgnoreCase("cage")) {
            CageSetupSession session = CageSetupSession.getSessionFromUUID(player.getUniqueId());

            if (session != null) {
                if (args.length < 2) {
                    session.sendProgress();
                    return;
                }

                String field = args[1].toLowerCase();
                switch (field) {
                    case "name":
                        if (args.length < 3) {
                            player.sendMessage("§cUse /lsw setup cage name <name>");
                            return;
                        }
                        session.setName(args[2]);
                        player.sendMessage("§aName set to: §e" + args[2]);
                        session.sendProgress();
                        break;

                    case "rarity":
                        if (args.length < 3) {
                            player.sendMessage("§cUse /lsw setup cage rarity <rarity>");
                            return;
                        }
                        try {
                            CosmeticRarity rarity = CosmeticRarity.valueOf(args[2].toUpperCase());
                            session.setRarity(rarity);
                            player.sendMessage("§aRarity set to: §e" + rarity.name());
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage("§cInvalid rarity! Use: " + Arrays.toString(CosmeticRarity.values()));
                        }
                        session.sendProgress();
                        break;

                    case "permission":
                        if (args.length < 3) {
                            player.sendMessage("§cUse /lsw setup cage permission <permission>");
                            return;
                        }
                        session.setPerm(args[2]);
                        player.sendMessage("§aPermission set to: §e" + args[2]);
                        session.sendProgress();
                        break;

                    case "icon":
                        if (player.getItemInHand() != null) {
                            session.setIcon(player.getItemInHand());
                            player.sendMessage("§aIcon set to item in hand: §e" + player.getItemInHand().getType().name());
                        } else {
                            player.sendMessage("§cHold an item in your hand to set as icon!");
                        }
                        session.sendProgress();
                        break;

                    case "type":
                        if (args.length < 3) {
                            player.sendMessage("§cUse /lsw setup cage type <STATIC/ANIMATED>");
                            return;
                        }
                        try {
                            SkyWarsCage.CageType type = SkyWarsCage.CageType.valueOf(args[2].toUpperCase());
                            session.setCageType(type);
                            player.sendMessage("§aCage type set to: §e" + type.name());
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage("§cInvalid type! Use STATIC or ANIMATED.");
                        }
                        session.sendProgress();
                        break;

                    case "refresh":
                        if (args.length < 3) {
                            player.sendMessage("§cUse /lsw setup cage refresh <ticks>");
                            return;
                        }
                        try {
                            long interval = Long.parseLong(args[2]);
                            session.setRefreshInterval(interval);
                            player.sendMessage("§aRefresh interval set to: §e" + interval + " ticks");
                        } catch (NumberFormatException ex) {
                            player.sendMessage("§cRefresh interval must be a number!");
                        }
                        session.sendProgress();
                        break;

                    case "exit":
                        session.exit();
                        player.sendMessage("§cCage setup has been cancelled!");
                        break;

                    case "finish":
                        List<String> missing = new ArrayList<>();
                        if (session.getName() == null) missing.add("Name");
                        if (session.getRarity() == null) missing.add("Rarity");
                        if (session.getPermission() == null) missing.add("Permission");
                        if (session.getIcon() == null) missing.add("Icon");
                        if (session.getCageType() == null) missing.add("Cage Type");

                        if (!missing.isEmpty()) {
                            player.sendMessage("§cYou must set all fields before finishing!");
                            player.sendMessage("§7Missing: §e" + String.join(", ", missing));
                            break;
                        }

                        session.end();
                        player.sendMessage("§aCage setup finished and saved successfully!");
                        break;

                    default:
                        player.sendMessage("§cUnknown setup option. Use /lsw setup cage to see progress.");
                        break;
                }
            } else {
                player.sendMessage("§cYou are not in a cage setup session!");
                player.sendMessage("§cUse '/lsw createcage <name>' first!");
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
