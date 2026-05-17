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
import org.twightlight.skywars.bungee.core.Core;
import org.twightlight.skywars.bungee.core.CoreLobbies;
import org.twightlight.skywars.bungee.core.CoreMode;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.UpdatablePlayerPagedMenu;
import org.twightlight.skywars.modules.privategames.PrivateGames;
import org.twightlight.skywars.modules.privategames.User;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unchecked")
public class MapsSelectorMenu extends UpdatablePlayerPagedMenu {

    private static final MenuConfig config = MenuConfig.getByName("mapselector");

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory().equals(getCurrentInventory())) {
            evt.setCancelled(true);

            if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
                ItemStack item = evt.getCurrentItem();
                Account account = Database.getInstance().getAccount(player.getUniqueId());
                if (account == null) {
                    player.closeInventory();
                    return;
                }

                if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
                    ConfigAction action = actions.get(item);
                    if (action != null && !action.getType().equals("NOTHING")) {
                        if (action.getType().equals("OPEN")) {
                            String menu = action.getValue();
                            if (menu.equalsIgnoreCase("random")) {
                                if (Core.MODE == CoreMode.MULTI_ARENA) {
                                    Arena server = Arena.findRandom(groupId);
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
                                    CoreLobbies.writeMinigame(player, groupId, "all");
                                }
                            } else if (menu.equalsIgnoreCase("favorites")) {
                                if (can) {
                                    List<String> favorites = account.getSelectedContainer().getFavorites();
                                    List<String> playable = new ArrayList<>();
                                    for (String fav : favorites) {
                                        boolean canPlay = arenaMap.containsKey(fav) && !arenaMap.get(fav).isEmpty();
                                        if (Core.MODE != CoreMode.MULTI_ARENA) {
                                            canPlay = getBungeeMap().containsKey(fav) && getBungeeMap().get(fav) > 0;
                                        }

                                        if (canPlay) {
                                            playable.add(fav);
                                        }
                                    }

                                    if (playable.isEmpty()) {
                                        return;
                                    }

                                    if (Core.MODE == CoreMode.MULTI_ARENA) {
                                        for (Arena server : this.arenaMap.get(playable.get(ThreadLocalRandom.current().nextInt(playable.size())))) {
                                            if (server.getState().canJoin() && server.getAlive() < server.getMaxPlayers()) {
                                                player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                                                player.closeInventory();
                                                User user = PrivateGames.getStorage().getUser(player);
                                                if (user != null && user.isEnablePrivateGame()) {
                                                    user.connect(account, server);
                                                } else {
                                                    account.updateLastSelected();
                                                    server.connect(account);
                                                }
                                                break;
                                            }
                                        }
                                    } else {
                                        CoreLobbies.writeMinigame(player, groupId, playable.get(ThreadLocalRandom.current().nextInt(playable.size())));
                                    }

                                    playable.clear();
                                }
                            } else if (menu.equalsIgnoreCase("play")) {
                                new PlayMenu(player, groupId);
                            } else if (menu.equalsIgnoreCase("closeinv")) {
                                player.closeInventory();
                            }
                        } else {
                            player.closeInventory();
                            action.send(player);
                        }
                    } else if (evt.getSlot() == previousPage) {
                        openPrevious();
                    } else if (evt.getSlot() == nextPage) {
                        openNext();
                    } else {
                        String mapName = maps.get(item);
                        if (mapName != null) {
                            if (evt.isRightClick()) {
                                if (account.isFavoriteMap(mapName)) {
                                    account.removeFavoriteMap(mapName);
                                } else {
                                    account.addFavoriteMap(mapName);
                                }
                                return;
                            }

                            if (can) {
                                if (Core.MODE == CoreMode.MULTI_ARENA) {
                                    for (Arena server : this.arenaMap.get(mapName)) {
                                        if (server.getState().canJoin() && server.getAlive() < server.getMaxPlayers()) {
                                            player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getName()));
                                            player.closeInventory();
                                            User user = PrivateGames.getStorage().getUser(player);
                                            if (user != null && user.isEnablePrivateGame()) {
                                                user.connect(account, server);
                                            } else {
                                                account.updateLastSelected();
                                                server.connect(account);
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    CoreLobbies.writeMinigame(player, groupId, mapName);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String groupId;
    private boolean tick = true, can = true;
    private Map<ItemStack, String> maps = new HashMap<>();
    private Map<String, List<Arena>> arenaMap = new HashMap<>();
    private Map<ItemStack, ConfigAction> actions = new HashMap<>();

    public MapsSelectorMenu(Player player, String groupId) {
        super(player, config.getTitle().replace("{mode}", getDisplayName(groupId)).replace("{type}", ""), config.getRows());
        this.groupId = groupId;
        this.previousPage = 18;
        this.nextPage = 26;
        this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

        this.update();
        this.open();
        this.register(20L);
    }

    private static String getDisplayName(String groupId) {
        ArenaGroup group = GroupManager.get(groupId);
        return group != null ? group.getStrippedName() : groupId;
    }

    @Override
    public void update() {
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) {
            player.closeInventory();
            return;
        }

        if (!player.hasPermission("lostskywars.mapselector.infinite") && !account.canSelectMap()) {
            can = false;
        }

        String displayMode = getDisplayName(groupId);

        List<ItemStack> items = new ArrayList<>(), sub = new ArrayList<>();
        if (Core.MODE != CoreMode.MULTI_ARENA) {
            try {
                for (Map.Entry<String, Integer> entry : getBungeeMap().entrySet()) {
                    String color = can ? "&a" : "&c";

                    ItemStack item;
                    if (account.isFavoriteMap(entry.getKey())) {
                        item = BukkitUtils.deserializeItemStack(config.getAsString("map").replace("{color}", color).replace("{map}", entry.getKey())
                                .replace("{avaiable}", String.valueOf(entry.getValue())).replace("{bukkit_color}", config.getAsString("color-favorite")).replace("{mode}", displayMode)
                                .replace("{type}", "").replace("{tick}", tick ? " " : ""));
                        items.add(item);
                    } else {
                        item = BukkitUtils.deserializeItemStack(config.getAsString("map").replace("{color}", color).replace("{map}", entry.getKey())
                                .replace("{avaiable}", String.valueOf(entry.getValue())).replace("{bukkit_color}", config.getAsString("color-normal")).replace("{mode}", displayMode)
                                .replace("{type}", "").replace("{tick}", tick ? " " : ""));
                        sub.add(item);
                    }

                    maps.put(item, entry.getKey());
                }
            } catch (Exception ex) {
            }
        } else {
            this.arenaMap = Arena.getAsMap(groupId);
            for (Map.Entry<String, List<Arena>> entry : arenaMap.entrySet()) {
                List<Arena> ss = entry.getValue();
                if (ss.isEmpty()) {
                    continue;
                }

                String color = can ? "&a" : "&c";

                ItemStack item;
                if (account.isFavoriteMap(entry.getKey())) {
                    item = BukkitUtils.deserializeItemStack(config.getAsString("map").replace("{color}", color).replace("{map}", entry.getKey())
                            .replace("{avaiable}", String.valueOf(ss.size())).replace("{bukkit_color}", config.getAsString("color-favorite")).replace("{mode}", displayMode)
                            .replace("{type}", "").replace("{tick}", tick ? " " : ""));
                    items.add(item);
                } else {
                    item = BukkitUtils.deserializeItemStack(config.getAsString("map").replace("{color}", color).replace("{map}", entry.getKey())
                            .replace("{avaiable}", String.valueOf(ss.size())).replace("{bukkit_color}", config.getAsString("color-normal")).replace("{mode}", displayMode)
                            .replace("{type}", "").replace("{tick}", tick ? " " : ""));
                    sub.add(item);
                }

                maps.put(item, entry.getKey());
            }
        }

        items.addAll(sub);
        sub.clear();
        sub = null;
        if (lastListSize != -1 && lastListSize != items.size()) {
            items.clear();
            items = null;
            new MapsSelectorMenu(player, groupId);
            return;
        }

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < 54) {
                String stack = entry.getValue().getStack();

                stack = stack.replace("{mode}", displayMode);
                stack = stack.replace("{type}", "");
                stack = stack.replace("{tick}", tick ? " " : "");

                ItemStack item = BukkitUtils.deserializeItemStack(stack);
                this.removeSlotsWith(item, entry.getKey());
                this.actions.put(item, entry.getValue().getAction());
            }
        }
        this.setItems(items);

        this.tick = !tick;
    }

    public Map<String, Integer> getBungeeMap() {
        return CoreLobbies.getMapSelector(groupId);
    }

    public void cancel() {
        super.cancel();
        arenaMap.clear();
        arenaMap = null;
        maps.clear();
        maps = null;
        actions.clear();
        actions = null;
        groupId = null;
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
        if (evt.getPlayer().equals(player) && evt.getInventory().equals(this.getCurrentInventory())) {
            this.cancel();
        }
    }
}
