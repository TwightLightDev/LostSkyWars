package org.twightlight.skywars.menu.shop;

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
import org.twightlight.skywars.cosmetics.visual.categories.*;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.MenuConfig;
import org.twightlight.skywars.config.MenuConfig.ConfigAction;
import org.twightlight.skywars.config.MenuConfig.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.menu.shop.ingamecosmetics.CosmeticsMenu;
import org.twightlight.skywars.menu.shop.well.SoulWellMenu;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.player.level.Level;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopMenu extends PlayerMenu {

    private static final MenuConfig config = MenuConfig.getByName("shop");

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
                            if (menu.equalsIgnoreCase("kitsandperks")) {
                                new KitsAndPerksMenu(player);
                            } else if (menu.equalsIgnoreCase("soulwell")) {
                                new SoulWellMenu(player, true);
                            } else if (menu.equalsIgnoreCase("cosmetics")) {
                                new CosmeticsMenu(player);
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

    public ShopMenu(Player player) {
        super(player, config.getTitle(), config.getRows());
        Account account = Database.getInstance().getAccount(player.getUniqueId());

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();

                // Kits
                List<VisualCosmetic> kitList = VisualCosmetic.listByType(VisualCosmeticType.CAGE); // kits use KitManager, but for cosmetic count:
                // Actually kits aren't VisualCosmetics in the new system. Let's use proper counts.
                int max, amount, percentage;

                // Cages
                max = VisualCosmetic.listByType(VisualCosmeticType.CAGE).size();
                amount = (int) VisualCosmetic.listByType(VisualCosmeticType.CAGE).stream().filter(cosmetic -> ((SkyWarsCage) cosmetic).has(account)).count();
                percentage = max > 0 ? (int) ((100.0 * amount) / max) : 0;
                int selectedCageId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.CAGE.getSelectionColumn());
                VisualCosmetic c = selectedCageId > 0 ? VisualCosmetic.findByTypeAndId(VisualCosmeticType.CAGE, selectedCageId) : null;
                stack = stack.replace("{cages_has}", String.valueOf(amount));
                stack = stack.replace("{cages_max}", String.valueOf(max));
                stack = stack.replace("{cages_percentage}", percentage + "%");
                stack = stack.replace("{cages_current}", c == null || !(c instanceof SkyWarsCage) ? "Glass" : ((SkyWarsCage) c).getRawName());

                // DeathCries
                max = VisualCosmetic.listByType(VisualCosmeticType.DEATH_CRY).size();
                amount = (int) VisualCosmetic.listByType(VisualCosmeticType.DEATH_CRY).stream().filter(cosmetic -> ((SkyWarsDeathCry) cosmetic).has(account)).count();
                percentage = max > 0 ? (int) ((100.0 * amount) / max) : 0;
                int selectedCryId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.DEATH_CRY.getSelectionColumn());
                c = selectedCryId > 0 ? VisualCosmetic.findByTypeAndId(VisualCosmeticType.DEATH_CRY, selectedCryId) : null;
                stack = stack.replace("{cries_has}", String.valueOf(amount));
                stack = stack.replace("{cries_max}", String.valueOf(max));
                stack = stack.replace("{cries_percentage}", percentage + "%");
                stack = stack.replace("{cries_current}", c == null || !(c instanceof SkyWarsDeathCry) ? config.getAsString("empty") : ((SkyWarsDeathCry) c).getRawName());

                // ProjectileTrail
                max = VisualCosmetic.listByType(VisualCosmeticType.TRAIL).size();
                amount = (int) VisualCosmetic.listByType(VisualCosmeticType.TRAIL).stream().filter(cosmetic -> ((SkyWarsTrail) cosmetic).has(account)).count();
                percentage = max > 0 ? (int) ((100.0 * amount) / max) : 0;
                int selectedTrailId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.TRAIL.getSelectionColumn());
                c = selectedTrailId > 0 ? VisualCosmetic.findByTypeAndId(VisualCosmeticType.TRAIL, selectedTrailId) : null;
                stack = stack.replace("{trails_has}", String.valueOf(amount));
                stack = stack.replace("{trails_max}", String.valueOf(max));
                stack = stack.replace("{trails_percentage}", percentage + "%");
                stack = stack.replace("{trails_current}", c == null || !(c instanceof SkyWarsTrail) ? config.getAsString("empty") : ((SkyWarsTrail) c).getRawName());

                // KillMessage
                max = VisualCosmetic.listByType(VisualCosmeticType.KILL_MESSAGE).size();
                amount = (int) VisualCosmetic.listByType(VisualCosmeticType.KILL_MESSAGE).stream().filter(cosmetic -> ((SkyWarsKillMessage) cosmetic).has(account)).count();
                percentage = max > 0 ? (int) ((100.0 * amount) / max) : 0;
                int selectedKmId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.KILL_MESSAGE.getSelectionColumn());
                c = selectedKmId > 0 ? VisualCosmetic.findByTypeAndId(VisualCosmeticType.KILL_MESSAGE, selectedKmId) : null;
                stack = stack.replace("{kms_has}", String.valueOf(amount));
                stack = stack.replace("{kms_max}", String.valueOf(max));
                stack = stack.replace("{kms_percentage}", percentage + "%");
                stack = stack.replace("{kms_current}", c == null || !(c instanceof SkyWarsKillMessage) ? config.getAsString("empty") : ((SkyWarsKillMessage) c).getRawName());

                // KillEffect
                max = VisualCosmetic.listByType(VisualCosmeticType.KILL_EFFECT).size();
                amount = (int) VisualCosmetic.listByType(VisualCosmeticType.KILL_EFFECT).stream().filter(cosmetic -> ((SkyWarsKillEffect) cosmetic).has(account)).count();
                percentage = max > 0 ? (int) ((100.0 * amount) / max) : 0;
                int selectedKeId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.KILL_EFFECT.getSelectionColumn());
                c = selectedKeId > 0 ? VisualCosmetic.findByTypeAndId(VisualCosmeticType.KILL_EFFECT, selectedKeId) : null;
                stack = stack.replace("{kes_has}", String.valueOf(amount));
                stack = stack.replace("{kes_max}", String.valueOf(max));
                stack = stack.replace("{kes_percentage}", percentage + "%");
                stack = stack.replace("{kes_current}", c == null || !(c instanceof SkyWarsKillEffect) ? config.getAsString("empty") : ((SkyWarsKillEffect) c).getRawName());

                // Spray
                max = VisualCosmetic.listByType(VisualCosmeticType.SPRAY).size();
                amount = (int) VisualCosmetic.listByType(VisualCosmeticType.SPRAY).stream().filter(cosmetic -> ((SkyWarsSpray) cosmetic).has(account)).count();
                percentage = max > 0 ? (int) ((100.0 * amount) / max) : 0;
                int selectedSprayId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.SPRAY.getSelectionColumn());
                c = selectedSprayId > 0 ? VisualCosmetic.findByTypeAndId(VisualCosmeticType.SPRAY, selectedSprayId) : null;
                stack = stack.replace("{sprays_has}", String.valueOf(amount));
                stack = stack.replace("{sprays_max}", String.valueOf(max));
                stack = stack.replace("{sprays_percentage}", percentage + "%");
                stack = stack.replace("{sprays_current}", c == null || !(c instanceof SkyWarsSpray) ? config.getAsString("empty") : ((SkyWarsSpray) c).getRawName());

                // Balloons
                max = VisualCosmetic.listByType(VisualCosmeticType.BALLOON).size();
                amount = (int) VisualCosmetic.listByType(VisualCosmeticType.BALLOON).stream().filter(cosmetic -> ((SkyWarsBalloon) cosmetic).has(account)).count();
                percentage = max > 0 ? (int) ((100.0 * amount) / max) : 0;
                int selectedBalloonId = account.getSelectedContainer().getGlobalSelection(VisualCosmeticType.BALLOON.getSelectionColumn());
                c = selectedBalloonId > 0 ? VisualCosmetic.findByTypeAndId(VisualCosmeticType.BALLOON, selectedBalloonId) : null;
                stack = stack.replace("{balloons_has}", String.valueOf(amount));
                stack = stack.replace("{balloons_max}", String.valueOf(max));
                stack = stack.replace("{balloons_percentage}", percentage + "%");
                stack = stack.replace("{balloons_current}", c == null || !(c instanceof SkyWarsBalloon) ? config.getAsString("empty") : ((SkyWarsBalloon) c).getRawName());

                // Symbols
                Level level = Level.getByLevel(account.getLevel());
                double currentExp = account.getExp();
                double needExp = level.getNext() == null ? 0.0 : level.getNext().getExp();
                stack = stack.replace("{exp}", StringUtils.formatPerMil(currentExp));
                stack = stack.replace("{nextExp}", needExp != 0.0 ? StringUtils.formatPerMil(needExp) : "Max");
                stack = stack.replace("{level}", level.getLevel(account));
                stack = stack.replace("{progressBar}", "8[ " + account.makeProgressBar(true) + " 8]");
                stack = stack.replace("{nextLevel}", needExp != 0.0 ? level.getNext().getLevel(account) : level.getLevel(account));
                stack = stack.replace("{display}", player.getDisplayName());

                // Stats
                stack = stack.replace("{souls}", account.getSoulsFormatted());

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
