package org.twightlight.skywars.menu.play;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.menu.ConfigMenu;
import org.twightlight.skywars.menu.ConfigMenu.ConfigAction;
import org.twightlight.skywars.menu.ConfigMenu.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.ui.SkyWarsMode;
import org.twightlight.skywars.ui.SkyWarsType;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;
import org.twightlight.skywars.world.WorldServer;

import java.util.HashMap;
import java.util.Map;

public class PlayMenu extends PlayerMenu {

    private static final ConfigMenu config = ConfigMenu.getByName("play");

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory().equals(getInventory())) {
            evt.setCancelled(true);

            if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
                ItemStack item = evt.getCurrentItem();
                Account account = Database.getInstance().getAccount(player.getUniqueId());
                if (account == null) {
                    player.closeInventory();
                    return;
                }

                if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
                    ConfigAction action = map.get(item);
                    if (action != null && !action.getType().equals("NOTHING")) {
                        if (action.getType().equals("OPEN")) {
                            String menu = action.getValue();
                            if (menu.equalsIgnoreCase("playnormal")) {
                                if (Core.MODE == CoreMode.MULTI_ARENA) {
                                    WorldServer<?> server = WorldServer.findRandom(mode, SkyWarsType.NORMAL);
                                    if (server != null) {
                                        player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                                        WorldServer<?> privateServer = server.cloneServer(true, server.getConfig().getId() + "_" + account.getPlayer().getUniqueId().toString());
                                        privateServer.connect(account);
                                    }
                                } else {
                                    CoreLobbies.writeMinigame(player, mode.name() + "_NORMAL", "all");
                                }
                            } else if (menu.equalsIgnoreCase("mapsnormal")) {
                                new MapsSelectorMenu(player, mode, SkyWarsType.NORMAL);
                            } else if (menu.equalsIgnoreCase("playinsane")) {
                                if (Core.MODE == CoreMode.MULTI_ARENA) {
                                    WorldServer<?> server = WorldServer.findRandom(mode, SkyWarsType.INSANE);
                                    if (server != null) {
                                        player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                                        server.connect(account);
                                    }
                                } else {
                                    CoreLobbies.writeMinigame(player, mode.name() + "_INSANE", "all");
                                }
                            } else if (menu.equalsIgnoreCase("mapsinsane")) {
                                new MapsSelectorMenu(player, mode, SkyWarsType.INSANE);
                            } else if (menu.equalsIgnoreCase("closeinv")) {
                                player.closeInventory();
                            }
                        } else {
                            player.closeInventory();
                            action.send(player);
                        }
                    }
                }
            }
        }
    }

    private SkyWarsMode mode;
    private Map<ItemStack, ConfigAction> map = new HashMap<>();

    public PlayMenu(Player player, SkyWarsMode mode) {
        super(player, config.getTitle(), config.getRows());
        this.mode = mode;

        int playing_normal = mode.equals(SkyWarsMode.SOLO) ? CoreLobbies.SOLO_NORMAL : CoreLobbies.DOUBLES_NORMAL,
                playing_insane = mode.equals(SkyWarsMode.SOLO) ? CoreLobbies.SOLO_INSANE : CoreLobbies.DOUBLES_INSANE;
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            for (WorldServer<?> server : WorldServer.listServers()) {
                if (server.getMode().equals(mode)) {
                    if (server.getType().equals(SkyWarsType.NORMAL)) {
                        playing_normal += server.getOnline();
                    } else if (server.getType().equals(SkyWarsType.INSANE)) {
                        playing_insane += server.getOnline();
                    }
                }
            }
        }

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                stack = stack.replace("{mode}", mode.getName());
                stack = stack.replace("{players_normal}", StringUtils.formatNumber(playing_normal));
                stack = stack.replace("{players_insane}", StringUtils.formatNumber(playing_insane));

                this.setItem(entry.getKey(), BukkitUtils.deserializeItemStack(stack));
                this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
            }
        }

        this.open();
        this.register();
    }

    public void cancel() {
        map.clear();
        map = null;
        mode = null;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        if (evt.getPlayer().equals(player)) {
            this.cancel();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent evt) {
        if (evt.getPlayer().equals(player) && evt.getInventory().equals(this.getInventory())) {
            this.cancel();
        }
    }
}
