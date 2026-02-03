package org.twightlight.skywars.hook.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.ui.enums.SkyWarsMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.menu.lobby.DeliveryManMenu;
import org.twightlight.skywars.menu.lobby.StatsNPCMenu;
import org.twightlight.skywars.menu.play.PlayDuelsMenu;
import org.twightlight.skywars.menu.play.PlayMenu;
import org.twightlight.skywars.menu.play.PlayRankedMenu;
import org.twightlight.skywars.menu.shop.ShopMenu;
import org.twightlight.skywars.player.Account;

public class CitizensHook {

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("CitizensHook");

    private static NPCRegistry registry;

    public static void setupCitizens() {
        LOGGER.log(Level.INFO, "Citizens found, hooking...");

        registry = CitizensAPI.createNamedNPCRegistry("LostSkyWars", new EmptyDatastore());

        PlayNPC.setupPlayNPCs();
        RankedNPC.setupRankedNPCs();
        DuelsNPC.setupDuelsNPCs();
        DeliveryNPC.setupDeliveryNPCs();
        ShopkeeperNPC.setupShopkeeperNPCs();
        if (SkyWars.protocollib) {
            StatsNPC.setupStatsNPCs();
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onNPCClick(NPCLeftClickEvent evt) {
                Player player = evt.getClicker();
                if (evt.getNPC().data().has("play-npc")) {
                    new PlayMenu(player, SkyWarsMode.fromName(evt.getNPC().data().get("play-npc")));
                } else if (evt.getNPC().data().has("ranked-npc")) {
                    Account account = Database.getInstance().getAccount(player.getUniqueId());
                    if (account != null) {
                        if (account.getLevel() >= Language.options$ranked$required$level) {
                            new PlayRankedMenu(player);
                            return;
                        }

                        player.sendMessage(Language.options$ranked$required$message);
                    }
                } else if (evt.getNPC().data().has("duels-npc")) {
                    new PlayDuelsMenu(player);
                } else if (evt.getNPC().data().has("shopkeeper")) {
                    new ShopMenu(player);
                } else if (evt.getNPC().data().has("profile")) {
                    new StatsNPCMenu(player);
                }
            }

            @EventHandler
            public void onNPCClick(NPCRightClickEvent evt) {
                Player player = evt.getClicker();
                if (evt.getNPC().data().has("play-npc")) {
                    new PlayMenu(player, SkyWarsMode.fromName(evt.getNPC().data().get("play-npc")));
                } else if (evt.getNPC().data().has("ranked-npc")) {
                    Account account = Database.getInstance().getAccount(player.getUniqueId());
                    if (account != null) {
                        if (account.getLevel() >= Language.options$ranked$required$level) {
                            new PlayRankedMenu(player);
                            return;
                        }

                        player.sendMessage(Language.options$ranked$required$message);
                    }
                } else if (evt.getNPC().data().has("duels-npc")) {
                    new PlayDuelsMenu(player);
                } else if (evt.getNPC().data().has("deliveryman")) {
                    new DeliveryManMenu(player);
                } else if (evt.getNPC().data().has("shopkeeper")) {
                    new ShopMenu(player);
                } else if (evt.getNPC().data().has("profile")) {
                    new StatsNPCMenu(player);
                }
            }
        }, SkyWars.getInstance());
    }

    public static void destroyCitizens() {
        LOGGER.log(Level.INFO, "Citizens found, destroying NPCs...");

        PlayNPC.listNPCs().forEach(PlayNPC::destroy);
        RankedNPC.listNPCs().forEach(RankedNPC::destroy);
        DuelsNPC.listNPCs().forEach(DuelsNPC::destroy);
        DeliveryNPC.listNPCs().forEach(DeliveryNPC::destroy);
        ShopkeeperNPC.listNPCs().forEach(ShopkeeperNPC::destroy);
        StatsNPC.listNPCs().forEach(StatsNPC::destroy);
    }

    public static NPCRegistry getRegistry() {
        return registry;
    }
}
