package org.twightlight.skywars.integration.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.integration.citizens.npc.DeliveryNPC;
import org.twightlight.skywars.integration.citizens.npc.ShopkeeperNPC;
import org.twightlight.skywars.integration.citizens.npc.StatsNPC;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.menu.lobby.DeliveryManMenu;
import org.twightlight.skywars.menu.lobby.StatsNPCMenu;
import org.twightlight.skywars.menu.play.PlayMenu;
import org.twightlight.skywars.menu.shop.ShopMenu;
import org.twightlight.skywars.player.Account;

public class CitizensIntegration {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("CitizensHook");

    private static NPCRegistry registry;

    public static void setupCitizens() {
        LOGGER.log(Level.INFO, "Citizens found, hooking...");

        registry = CitizensAPI.createNamedNPCRegistry("LostSkyWars", new EmptyDatastore());

        DeliveryNPC.setupDeliveryNPCs();
        ShopkeeperNPC.setupShopkeeperNPCs();
        if (SkyWars.protocollib) {
            StatsNPC.setupStatsNPCs();
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onNPCClick(NPCLeftClickEvent evt) {
                handleNPCClick(evt.getClicker(), evt.getNPC());
            }

            @EventHandler
            public void onNPCClick(NPCRightClickEvent evt) {
                Player player = evt.getClicker();
                if (evt.getNPC().data().has("deliveryman")) {
                    new DeliveryManMenu(player);
                } else {
                    handleNPCClick(player, evt.getNPC());
                }
            }
        }, SkyWars.getInstance());
    }

    private static void handleNPCClick(Player player, net.citizensnpcs.api.npc.NPC npc) {
        if (npc.data().has("play-npc")) {
            String category = npc.data().get("play-npc");
            new PlayMenu(player, category);
        } else if (npc.data().has("ranked-npc")) {
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account != null) {
                if (account.getLevel() >= Language.options$ranked$required$level) {
                    handleRankedPlay(player, account);
                    return;
                }
                player.sendMessage(Language.options$ranked$required$message);
            }
        } else if (npc.data().has("duels-npc")) {
            handleDuelsPlay(player);
        } else if (npc.data().has("shopkeeper")) {
            new ShopMenu(player);
        } else if (npc.data().has("profile")) {
            new StatsNPCMenu(player);
        }
    }

    private static void handleRankedPlay(Player player, Account account) {
        String groupId = "ranked_solo";
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            Arena server = Arena.findRandom(groupId);
            if (server != null) {
                player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                server.connect(account);
            }
        } else {
            CoreLobbies.writeMinigame(player, groupId, "all");
        }
    }

    private static void handleDuelsPlay(Player player) {
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) return;
        String groupId = "duels";
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            Arena server = Arena.findRandom(groupId);
            if (server != null) {
                player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                server.connect(account);
            }
        } else {
            CoreLobbies.writeMinigame(player, groupId, "all");
        }
    }

    public static void destroyCitizens() {
        LOGGER.log(Level.INFO, "Citizens found, destroying NPCs...");

        DeliveryNPC.listNPCs().forEach(DeliveryNPC::destroy);
        ShopkeeperNPC.listNPCs().forEach(ShopkeeperNPC::destroy);
        StatsNPC.listNPCs().forEach(StatsNPC::destroy);
    }

    public static NPCRegistry getRegistry() {
        return registry;
    }
}
