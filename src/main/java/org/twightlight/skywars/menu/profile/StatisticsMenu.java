package org.twightlight.skywars.menu.profile;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.menu.lobby.ProfileMenu;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.ranked.League;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class StatisticsMenu extends PlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("statistics");

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
                            if (menu.equalsIgnoreCase("profile")) {
                                new ProfileMenu(player);
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

    private Account account;
    private Map<ItemStack, ConfigAction> map = new HashMap<>();

    public StatisticsMenu(Player player) {
        this(player, Database.getInstance().getAccount(player.getUniqueId()));
    }

    public StatisticsMenu(Player player, Account account) {
        super(player, config.getTitle().replace("{target}", account.getUniqueId().equals(player.getUniqueId()) ? "" : "- " + account.getName()), config.getRows());
        if (Database.getInstance().getAccount(account.getUniqueId()) == null) {
            this.account = account;
        }

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                // Total (across solo + doubles + ranked_solo)
                stack = stack.replace("{wins}", StringUtils.formatNumber(
                        account.getSumStat("wins", "solo", "doubles", "ranked_solo")));
                stack = stack.replace("{kills}", StringUtils.formatNumber(
                        account.getSumStat("kills", "solo", "doubles", "ranked_solo")));
                stack = stack.replace("{melee}", StringUtils.formatNumber(
                        account.getSumStat("melee_kills", "solo", "doubles", "ranked_solo")));
                stack = stack.replace("{void}", StringUtils.formatNumber(
                        account.getSumStat("void_kills", "solo", "doubles", "ranked_solo")));
                stack = stack.replace("{bow}", StringUtils.formatNumber(
                        account.getSumStat("bow_kills", "solo", "doubles", "ranked_solo")));
                stack = stack.replace("{assists}", StringUtils.formatNumber(
                        account.getSumStat("assists", "solo", "doubles", "ranked_solo")));
                stack = stack.replace("{deaths}", StringUtils.formatNumber(
                        account.getSumStat("deaths", "solo", "doubles", "ranked_solo")));
                stack = stack.replace("{games}", StringUtils.formatNumber(
                        account.getSumStat("plays", "solo", "doubles", "ranked_solo")));
                stack = stack.replace("{coins}", account.getCoinsFormatted());
                stack = stack.replace("{souls}", account.getSoulsFormatted());
                stack = stack.replace("{maxsouls}", StringUtils.formatNumber(account.getMaxSouls()));

                // Solo
                stack = stack.replace("{solowins}",   account.getStatFormatted("solo", "wins"));
                stack = stack.replace("{solokills}",  account.getStatFormatted("solo", "kills"));
                stack = stack.replace("{solomelee}",  account.getStatFormatted("solo", "melee_kills"));
                stack = stack.replace("{solovoid}",   account.getStatFormatted("solo", "void_kills"));
                stack = stack.replace("{solobow}",    account.getStatFormatted("solo", "bow_kills"));
                stack = stack.replace("{soloassists}",account.getStatFormatted("solo", "assists"));
                stack = stack.replace("{solodeaths}", account.getStatFormatted("solo", "deaths"));
                stack = stack.replace("{sologames}",  account.getStatFormatted("solo", "plays"));

                // Doubles
                stack = stack.replace("{teamwins}",   account.getStatFormatted("doubles", "wins"));
                stack = stack.replace("{teamkills}",  account.getStatFormatted("doubles", "kills"));
                stack = stack.replace("{teammelee}",  account.getStatFormatted("doubles", "melee_kills"));
                stack = stack.replace("{teamvoid}",   account.getStatFormatted("doubles", "void_kills"));
                stack = stack.replace("{teambow}",    account.getStatFormatted("doubles", "bow_kills"));
                stack = stack.replace("{teamassists}",account.getStatFormatted("doubles", "assists"));
                stack = stack.replace("{teamdeaths}", account.getStatFormatted("doubles", "deaths"));
                stack = stack.replace("{teamgames}",  account.getStatFormatted("doubles", "plays"));

                // Ranked
                stack = stack.replace("{rankedwins}",   account.getStatFormatted("ranked_solo", "wins"));
                stack = stack.replace("{rankedkills}",  account.getStatFormatted("ranked_solo", "kills"));
                stack = stack.replace("{rankedmelee}",  account.getStatFormatted("ranked_solo", "melee_kills"));
                stack = stack.replace("{rankedvoid}",   account.getStatFormatted("ranked_solo", "void_kills"));
                stack = stack.replace("{rankedbow}",    account.getStatFormatted("ranked_solo", "bow_kills"));
                stack = stack.replace("{rankedassists}",account.getStatFormatted("ranked_solo", "assists"));
                stack = stack.replace("{rankeddeaths}", account.getStatFormatted("ranked_solo", "deaths"));
                stack = stack.replace("{rankedgames}",  account.getStatFormatted("ranked_solo", "plays"));

                // Elo / League now live on the profile, not on ranked stats
                stack = stack.replace("{points}", account.getEloFormatted());
                League league = account.getLeague();
                stack = stack.replace("{league}", league != null ? league.getName() : "");
                stack = stack.replace("{brave_points}", StringUtils.formatNumber(account.getBravePoints()));

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
        if (account != null) {
            this.account.destroy();
            this.account = null;
        }
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
