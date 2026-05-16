package org.twightlight.skywars.integration.citizens.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.commands.SubCommand;
import org.twightlight.skywars.integration.citizens.npc.DeliveryNPC;
import org.twightlight.skywars.mojang.InvalidMojangException;
import org.twightlight.skywars.mojang.Mojang;

public class DeliveryNPCCommand extends SubCommand {

    public DeliveryNPCCommand() {
        super("deliverynpc");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 0) {
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                player.sendMessage(" \n§dDeliveryNPC - Help\n \n§6/lsw deliverynpc add <id> §f- §7Spawn an DeliveryMan.\n§6/lsw deliverynpc remove <id> §f- §7Remove an DeliveryMan.\n§6/lsw deliverynpc setskin <player> §f- §7Set DeliveryMan skin.\n ");
            } else {
                player.sendMessage(" \n§dDeliveryNPC - Help\n \n§6/lsw deliverynpc add <id> §f- §7Spawn an DeliveryMan.\n§6/lsw deliverynpc remove <id> §f- §7Remove an DeliveryMan.\n ");
            }
            return;
        }

        String action = args[0];
        if (action.equalsIgnoreCase("add")) {
            if (args.length < 2) {
                player.sendMessage("§cUse /lsw deliverynpc add <id>");
                return;
            }

            DeliveryNPC npc = DeliveryNPC.getById(args[1]);
            if (npc != null) {
                player.sendMessage("§5[LostSkyWars] §cAlready exists an DeliveryMan with id \"" + args[1] + "\"!");
                return;
            }

            Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
            DeliveryNPC.add(args[1], location);
            player.sendMessage("§5[LostSkyWars] §aDeliveryMan added successfully!");
        } else if (action.equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                player.sendMessage("§cUse /lsw deliverynpc remove <id>");
                return;
            }

            DeliveryNPC npc = DeliveryNPC.getById(args[1]);
            if (npc == null) {
                player.sendMessage("§5[LostSkyWars] §cCannot found an DeliveryMan with id \"" + args[1] + "\"!");
                return;
            }

            DeliveryNPC.remove(npc);
            player.sendMessage("§5[LostSkyWars] §aDeliveryMan removed successfully!");
        } else if (action.equalsIgnoreCase("setskin") && Core.MODE == CoreMode.MULTI_ARENA) {
            if (args.length < 2) {
                player.sendMessage("§cUse /lsw deliverynpc setskin <player>");
                return;
            }

            String skin = args[1];
            player.sendMessage("§5[LostSkyWars] §aFetching " + skin + "'s skin.");
            try {
                String id = Mojang.getUUID(skin);
                if (id == null) {
                    player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
                    return;
                }

                String prop = Mojang.getSkinProperty(id);
                if (prop == null) {
                    player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
                    return;
                }

                Language.lobby$npcs$deliveryman$skin_value = prop.split(" : ")[1];
                Language.lobby$npcs$deliveryman$skin_signature = prop.split(" : ")[2];

                Language.reload();
                DeliveryNPC.listNPCs().forEach(DeliveryNPC::spawn);
                player.sendMessage("§5[LostSkyWars] §aSkin fetched and applied successfully!");
            } catch (InvalidMojangException ex) {
                player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
            }
        } else {
            if (Core.MODE == CoreMode.MULTI_ARENA) {
                player.sendMessage(" \n§dDeliveryNPC - Help\n \n§6/lsw deliverynpc add <id> §f- §7Spawn an DeliveryMan.\n§6/lsw deliverynpc remove <id> §f- §7Remove an DeliveryMan.\n§6/lsw deliverynpc setskin <player> §f- §7Set DeliveryMan skin.\n ");
            } else {
                player.sendMessage(" \n§dDeliveryNPC - Help\n \n§6/lsw deliverynpc add <id> §f- §7Spawn an DeliveryMan.\n§6/lsw deliverynpc remove <id> §f- §7Remove an DeliveryMan.\n ");
            }
        }
    }

    @Override
    public String getUsage() {
        return "deliverynpc";
    }

    @Override
    public String getDescription() {
        return "Manage DeliveryMan NPCs.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }
}
