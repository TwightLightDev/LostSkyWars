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
import org.twightlight.skywars.bungee.core.Core;
import org.twightlight.skywars.bungee.core.CoreLobbies;
import org.twightlight.skywars.bungee.core.CoreMode;
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
        if (npc.data().has("shopkeeper")) {
            new ShopMenu(player);
        } else if (npc.data().has("profile")) {
            new StatsNPCMenu(player);
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
