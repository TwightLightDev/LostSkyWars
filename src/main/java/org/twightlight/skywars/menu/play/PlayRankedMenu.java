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
import org.twightlight.skywars.ranked.Ranked;
import org.twightlight.skywars.ui.SkyWarsMode;
import org.twightlight.skywars.ui.SkyWarsType;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;
import org.twightlight.skywars.world.WorldServer;

import java.util.HashMap;
import java.util.Map;

public class PlayRankedMenu extends PlayerMenu {

    private static final ConfigMenu config = ConfigMenu.getByName("playranked");

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
                            if (menu.equalsIgnoreCase("playsolo")) {
                                if (Core.MODE == CoreMode.MULTI_ARENA) {
                                    WorldServer<?> server = WorldServer.findRandom(SkyWarsMode.SOLO, SkyWarsType.RANKED);
                                    if (server != null) {
                                        player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getWorld().getName()));
                                        server.connect(account);
                                    }
                                } else {
                                    CoreLobbies.writeMinigame(player, "SOLO_RANKED", "all");
                                }
                            } else if (menu.equalsIgnoreCase("mapssolo")) {
                                new MapsSelectorMenu(player, SkyWarsMode.SOLO, SkyWarsType.RANKED);
                            } else if (menu.equalsIgnoreCase("playdoubles")) {
                                if (Core.MODE == CoreMode.MULTI_ARENA) {
                                    WorldServer<?> server = WorldServer.findRandom(SkyWarsMode.DOUBLES, SkyWarsType.RANKED);
                                    if (server != null) {
                                        player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getWorld().getName()));
                                        server.connect(account);
                                    }
                                } else {
                                    CoreLobbies.writeMinigame(player, "DOUBLES_RANKED", "all");
                                }
                            } else if (menu.equalsIgnoreCase("mapsdoubles")) {
                                new MapsSelectorMenu(player, SkyWarsMode.DOUBLES, SkyWarsType.RANKED);
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

    private Map<ItemStack, ConfigAction> map = new HashMap<>();

    public PlayRankedMenu(Player player) {
        super(player, config.getTitle(), config.getRows());
        Account account = Database.getInstance().getAccount(player.getUniqueId());

        int playing_solo = CoreLobbies.SOLO_RANKED, playing_doubles = CoreLobbies.DOUBLES_RANKED;
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            for (WorldServer<?> server : WorldServer.listServers()) {
                if (server.getType().equals(SkyWarsType.RANKED)) {
                    if (server.getMode().equals(SkyWarsMode.SOLO)) {
                        playing_solo += server.getOnline();
                    } else {
                        playing_doubles += server.getOnline();
                    }
                }
            }
        }

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                stack = stack.replace("{points}", StringUtils.formatNumber(Ranked.getPoints(account)));
                stack = stack.replace("{league}", Ranked.getLeague(account).getName());
                stack = stack.replace("{players_solo}", StringUtils.formatNumber(playing_solo));
                stack = stack.replace("{players_doubles}", StringUtils.formatNumber(playing_doubles));

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
