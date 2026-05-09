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
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreLobbies;
import org.twightlight.skywars.bungee.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.menu.ConfigMenu;
import org.twightlight.skywars.menu.ConfigMenu.ConfigAction;
import org.twightlight.skywars.menu.ConfigMenu.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.modules.privategames.PrivateGames;
import org.twightlight.skywars.modules.privategames.User;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;

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
                                String normalGroupId = resolveNormalGroupId();
                                if (Core.MODE == CoreMode.MULTI_ARENA) {
                                    Arena server = Arena.findRandom(normalGroupId);
                                    if (server != null) {
                                        User user = PrivateGames.getStorage().getUser(player);
                                        if (user != null && user.isEnablePrivateGame()) {
                                            user.connect(account, server);
                                        } else {
                                            server.connect(account);
                                        }
                                        player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                                    }
                                } else {
                                    CoreLobbies.writeMinigame(player, normalGroupId, "all");
                                }
                            } else if (menu.equalsIgnoreCase("mapsnormal")) {
                                new MapsSelectorMenu(player, resolveNormalGroupId());
                            } else if (menu.equalsIgnoreCase("playinsane")) {
                                String insaneGroupId = resolveInsaneGroupId();
                                if (Core.MODE == CoreMode.MULTI_ARENA) {
                                    Arena server = Arena.findRandom(insaneGroupId);
                                    if (server != null) {
                                        User user = PrivateGames.getStorage().getUser(player);
                                        if (user != null && user.isEnablePrivateGame()) {
                                            user.connect(account, server);
                                        } else {
                                            server.connect(account);
                                        }
                                        player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                                    }
                                } else {
                                    CoreLobbies.writeMinigame(player, insaneGroupId, "all");
                                }
                            } else if (menu.equalsIgnoreCase("mapsinsane")) {
                                new MapsSelectorMenu(player, resolveInsaneGroupId());
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

    private String category;
    private Map<ItemStack, ConfigAction> map = new HashMap<>();

    /**
     * @param player   the player
     * @param category "solo" or "doubles" — determines which normal/insane groups to use
     */
    public PlayMenu(Player player, String category) {
        super(player, config.getTitle(), config.getRows());
        this.category = category;

        String normalGroupId = resolveNormalGroupId();
        String insaneGroupId = resolveInsaneGroupId();

        int playingNormal = CoreLobbies.getPlayerCount(normalGroupId);
        int playingInsane = CoreLobbies.getPlayerCount(insaneGroupId);
        if (Core.MODE == CoreMode.MULTI_ARENA) {
            for (Arena server : Arena.listServers()) {
                String gid = server.getGroup().getId();
                if (gid.equals(normalGroupId)) {
                    playingNormal += server.getOnline();
                } else if (gid.equals(insaneGroupId)) {
                    playingInsane += server.getOnline();
                }
            }
        }

        String modeName = category.equals("doubles") ? Language.options$mode$doubles : Language.options$mode$solo;

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                stack = stack.replace("{mode}", modeName);
                stack = stack.replace("{players_normal}", StringUtils.formatNumber(playingNormal));
                stack = stack.replace("{players_insane}", StringUtils.formatNumber(playingInsane));

                this.setItem(entry.getKey(), BukkitUtils.deserializeItemStack(stack));
                this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
            }
        }

        this.open();
        this.register();
    }

    private String resolveNormalGroupId() {
        if (category.equals("doubles")) return "doubles";
        return "solo";
    }

    private String resolveInsaneGroupId() {
        if (category.equals("doubles")) return "doubles_insane";
        return "solo_insane";
    }

    public void cancel() {
        map.clear();
        map = null;
        category = null;
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
