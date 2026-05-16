package org.twightlight.skywars.menu.shop.ingamecosmetics;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.symbol.SymbolsMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.balloon.BalloonsMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.cage.CagesMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.cry.DeathCriesMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.killeffect.KillEffectsMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.killmessage.KillMessagesMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.spray.SpraysMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.title.TitlesMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.trail.ProjectileTrailsMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.victorydance.VictoryDancesMenu;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.level.Level;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmeticsMenu extends PlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("cosmetics");

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
                            if (menu.equalsIgnoreCase("cages")) {
                                new CagesMenu(player);
                            } else if (menu.equalsIgnoreCase("deathcry")) {
                                new DeathCriesMenu(player);
                            } else if (menu.equalsIgnoreCase("killmessage")) {
                                new KillMessagesMenu(player);
                            } else if (menu.equalsIgnoreCase("killeffect")) {
                                new KillEffectsMenu(player);
                            } else if (menu.equalsIgnoreCase("spray")) {
                                new SpraysMenu(player);
                            } else if (menu.equalsIgnoreCase("trail")) {
                                new ProjectileTrailsMenu(player);
                            } else if (menu.equalsIgnoreCase("balloon")) {
                                new BalloonsMenu(player);
                            } else if (menu.equalsIgnoreCase("victorydance")) {
                                new VictoryDancesMenu(player);
                            } else if (menu.equalsIgnoreCase("title")) {
                                new TitlesMenu(player);
                            } else if (menu.equalsIgnoreCase("symbols")) {
                                new SymbolsMenu(player);
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

    private static String getSelectedName(Account account, VisualCosmeticType type, String fallback) {
        int selectedId = account.getSelectedContainer().getGlobalSelection(type.getSelectionColumn());
        if (selectedId <= 0) return fallback;
        VisualCosmetic c = VisualCosmetic.findByTypeAndId(type, selectedId);
        return c != null ? c.getRawName() : fallback;
    }

    private static String getCosmeticStats(Account account, VisualCosmeticType type) {
        List<VisualCosmetic> all = VisualCosmetic.listByType(type);
        int max = all.size();
        int amount = (int) all.stream().filter(cosmetic -> cosmetic.has(account)).count();
        int percentage = max > 0 ? (int) ((100.0 * amount) / max) : 0;
        return amount + ";" + max + ";" + percentage;
    }

    public CosmeticsMenu(Player player) {
        super(player, config.getTitle(), config.getRows());
        Account account = Database.getInstance().getAccount(player.getUniqueId());

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                // Cages
                String[] stats = getCosmeticStats(account, VisualCosmeticType.CAGE).split(";");
                stack = stack.replace("{cages_has}", stats[0]);
                stack = stack.replace("{cages_max}", stats[1]);
                stack = stack.replace("{cages_percentage}", stats[2] + "%");
                stack = stack.replace("{cages_current}", getSelectedName(account, VisualCosmeticType.CAGE, "Glass"));

                // DeathCries
                stats = getCosmeticStats(account, VisualCosmeticType.DEATH_CRY).split(";");
                stack = stack.replace("{cries_has}", stats[0]);
                stack = stack.replace("{cries_max}", stats[1]);
                stack = stack.replace("{cries_percentage}", stats[2] + "%");
                stack = stack.replace("{cries_current}", getSelectedName(account, VisualCosmeticType.DEATH_CRY, config.getAsString("empty")));

                // ProjectileTrail
                stats = getCosmeticStats(account, VisualCosmeticType.TRAIL).split(";");
                stack = stack.replace("{trails_has}", stats[0]);
                stack = stack.replace("{trails_max}", stats[1]);
                stack = stack.replace("{trails_percentage}", stats[2] + "%");
                stack = stack.replace("{trails_current}", getSelectedName(account, VisualCosmeticType.TRAIL, config.getAsString("empty")));

                // KillMessage
                stats = getCosmeticStats(account, VisualCosmeticType.KILL_MESSAGE).split(";");
                stack = stack.replace("{kms_has}", stats[0]);
                stack = stack.replace("{kms_max}", stats[1]);
                stack = stack.replace("{kms_percentage}", stats[2] + "%");
                stack = stack.replace("{kms_current}", getSelectedName(account, VisualCosmeticType.KILL_MESSAGE, config.getAsString("empty")));

                // KillEffect
                stats = getCosmeticStats(account, VisualCosmeticType.KILL_EFFECT).split(";");
                stack = stack.replace("{kes_has}", stats[0]);
                stack = stack.replace("{kes_max}", stats[1]);
                stack = stack.replace("{kes_percentage}", stats[2] + "%");
                stack = stack.replace("{kes_current}", getSelectedName(account, VisualCosmeticType.KILL_EFFECT, config.getAsString("empty")));

                // VictoryDance
                stats = getCosmeticStats(account, VisualCosmeticType.VICTORY_DANCE).split(";");
                stack = stack.replace("{vds_has}", stats[0]);
                stack = stack.replace("{vds_max}", stats[1]);
                stack = stack.replace("{vds_percentage}", stats[2] + "%");
                stack = stack.replace("{vds_current}", getSelectedName(account, VisualCosmeticType.VICTORY_DANCE, config.getAsString("empty")));

                // Spray
                stats = getCosmeticStats(account, VisualCosmeticType.SPRAY).split(";");
                stack = stack.replace("{sprays_has}", stats[0]);
                stack = stack.replace("{sprays_max}", stats[1]);
                stack = stack.replace("{sprays_percentage}", stats[2] + "%");
                stack = stack.replace("{sprays_current}", getSelectedName(account, VisualCosmeticType.SPRAY, config.getAsString("empty")));

                // Balloons
                stats = getCosmeticStats(account, VisualCosmeticType.BALLOON).split(";");
                stack = stack.replace("{balloons_has}", stats[0]);
                stack = stack.replace("{balloons_max}", stats[1]);
                stack = stack.replace("{balloons_percentage}", stats[2] + "%");
                stack = stack.replace("{balloons_current}", getSelectedName(account, VisualCosmeticType.BALLOON, config.getAsString("empty")));

                // Titles
                stats = getCosmeticStats(account, VisualCosmeticType.TITLE).split(";");
                stack = stack.replace("{titles_has}", stats[0]);
                stack = stack.replace("{titles_max}", stats[1]);
                stack = stack.replace("{titles_percentage}", stats[2] + "%");
                stack = stack.replace("{titles_current}", getSelectedName(account, VisualCosmeticType.TITLE, config.getAsString("empty")));

                // Symbols / Level
                Level level = Level.getByLevel(account.getLevel());
                double currentExp = account.getExp();
                double needExp = level.getNext() == null ? 0.0 : level.getNext().getExp();
                stack = stack.replace("{exp}", StringUtils.formatPerMil(currentExp));
                stack = stack.replace("{nextExp}", needExp != 0.0 ? StringUtils.formatPerMil(needExp) : "Max");
                stack = stack.replace("{level}", level.getLevel(account));
                stack = stack.replace("{progressBar}", "8[ " + account.makeProgressBar(true) + " 8]");
                stack = stack.replace("{nextLevel}", needExp != 0.0 ? level.getNext().getLevel(account) : level.getLevel(account));
                stack = stack.replace("{display}", player.getDisplayName());

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
