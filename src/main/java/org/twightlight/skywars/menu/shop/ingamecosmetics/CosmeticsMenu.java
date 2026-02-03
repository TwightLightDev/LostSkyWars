package org.twightlight.skywars.menu.shop.ingamecosmetics;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.*;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.menu.ConfigMenu;
import org.twightlight.skywars.menu.ConfigMenu.ConfigAction;
import org.twightlight.skywars.menu.ConfigMenu.ConfigItem;
import org.twightlight.skywars.menu.api.PlayerMenu;
import org.twightlight.skywars.menu.shop.SymbolsMenu;
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
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CosmeticsMenu extends PlayerMenu {

    private static final ConfigMenu config = ConfigMenu.getByName("cosmetics");

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
                            }  else if (menu.equalsIgnoreCase("spray")) {
                                new SpraysMenu(player);
                            } else if (menu.equalsIgnoreCase("trail")) {
                                new ProjectileTrailsMenu(player);
                            } else if (menu.equalsIgnoreCase("balloon")) {
                                new BalloonsMenu(player);
                            } else if (menu.equalsIgnoreCase("victorydance")) {
                                 new VictoryDancesMenu(player);
                            } else if (menu.equalsIgnoreCase("title")) {
                                 new TitlesMenu(player);
                            }  else if (menu.equalsIgnoreCase("symbols")) {
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

    public CosmeticsMenu(Player player) {
        super(player, config.getTitle(), config.getRows());
        Account account = Database.getInstance().getAccount(player.getUniqueId());

        for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
                String stack = entry.getValue().getStack();


                // Cages
                int max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_CAGE).size();
                int amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_CAGE).stream().filter(cosmetic -> ((SkyWarsCage) cosmetic).has(account)).count();
                int percentage = (int) ((100.0 * amount) / max);
                Cosmetic c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_CAGE, 1);
                stack = stack.replace("{cages_has}", String.valueOf(amount));
                stack = stack.replace("{cages_max}", String.valueOf(max));
                stack = stack.replace("{cages_percentage}", percentage + "%");
                stack = stack.replace("{cages_current}", c == null || !(c instanceof SkyWarsCage) ? "Glass" : ((SkyWarsCage) c).getRawName());

                // DeathCries
                max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_DEATHCRY).size();
                amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_DEATHCRY).stream().filter(cosmetic -> ((SkyWarsDeathCry) cosmetic).has(account)).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_DEATHCRY, 1);
                stack = stack.replace("{cries_has}", String.valueOf(amount));
                stack = stack.replace("{cries_max}", String.valueOf(max));
                stack = stack.replace("{cries_percentage}", percentage + "%");
                stack = stack.replace("{cries_current}", c == null || !(c instanceof SkyWarsDeathCry) ? config.getAsString("empty") : ((SkyWarsDeathCry) c).getRawName());

                // ProjectileTrail
                max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_TRAIL).size();
                amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_TRAIL).stream().filter(cosmetic -> ((SkyWarsTrail) cosmetic).has(account)).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_TRAIL, 1);
                stack = stack.replace("{trails_has}", String.valueOf(amount));
                stack = stack.replace("{trails_max}", String.valueOf(max));
                stack = stack.replace("{trails_percentage}", percentage + "%");
                stack = stack.replace("{trails_current}", c == null || !(c instanceof SkyWarsTrail) ? config.getAsString("empty") : ((SkyWarsTrail) c).getRawName());

                // KillMessage
                max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_KILLMESSAGE).size();
                amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_KILLMESSAGE).stream().filter(cosmetic -> ((SkyWarsKillMessage) cosmetic).has(account)).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KILLMESSAGE, 1);
                stack = stack.replace("{kms_has}", String.valueOf(amount));
                stack = stack.replace("{kms_max}", String.valueOf(max));
                stack = stack.replace("{kms_percentage}", percentage + "%");
                stack = stack.replace("{kms_current}", c == null || !(c instanceof SkyWarsKillMessage) ? config.getAsString("empty") : ((SkyWarsKillMessage) c).getRawName());

                // KillEffect
                max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_KILLEFFECT).size();
                amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_KILLEFFECT).stream().filter(cosmetic -> ((SkyWarsKillEffect) cosmetic).has(account)).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KILLEFFECT, 1);
                stack = stack.replace("{kes_has}", String.valueOf(amount));
                stack = stack.replace("{kes_max}", String.valueOf(max));
                stack = stack.replace("{kes_percentage}", percentage + "%");
                stack = stack.replace("{kes_current}", c == null || !(c instanceof SkyWarsKillEffect) ? config.getAsString("empty") : ((SkyWarsKillEffect) c).getRawName());
                // VictoryDance
                max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_VICTORYDANCE).size();
                amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_VICTORYDANCE).stream().filter(cosmetic -> ((SkyWarsVictoryDance) cosmetic).has(account)).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_VICTORYDANCE, 1);
                stack = stack.replace("{vds_has}", String.valueOf(amount));
                stack = stack.replace("{vds_max}", String.valueOf(max));
                stack = stack.replace("{vds_percentage}", percentage + "%");
                stack = stack.replace("{vds_current}", c == null || !(c instanceof SkyWarsVictoryDance) ? config.getAsString("empty") : ((SkyWarsVictoryDance) c).getRawName());

                // Spray
                max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_SPRAY).size();
                amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_SPRAY).stream().filter(cosmetic -> ((SkyWarsSpray) cosmetic).has(account)).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_SPRAY, 1);
                stack = stack.replace("{sprays_has}", String.valueOf(amount));
                stack = stack.replace("{sprays_max}", String.valueOf(max));
                stack = stack.replace("{sprays_percentage}", percentage + "%");
                stack = stack.replace("{sprays_current}", c == null || !(c instanceof SkyWarsSpray) ? config.getAsString("empty") : ((SkyWarsSpray) c).getRawName());

                // Balloons
                max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_BALLOON).size();
                amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_BALLOON).stream().filter(cosmetic -> ((SkyWarsBalloon) cosmetic).has(account)).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_BALLOON, 1);
                stack = stack.replace("{balloons_has}", String.valueOf(amount));
                stack = stack.replace("{balloons_max}", String.valueOf(max));
                stack = stack.replace("{balloons_percentage}", percentage + "%");
                stack = stack.replace("{balloons_current}", c == null || !(c instanceof SkyWarsBalloon) ? config.getAsString("empty") : ((SkyWarsBalloon) c).getRawName());

                // Titles
                max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_TITLE).size();
                amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_TITLE).stream().filter(cosmetic -> ((SkyWarsTitle) cosmetic).has(account)).count();
                percentage = (int) ((100.0 * amount) / max);
                c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_TITLE, 1);
                stack = stack.replace("{titles_has}", String.valueOf(amount));
                stack = stack.replace("{titles_max}", String.valueOf(max));
                stack = stack.replace("{titles_percentage}", percentage + "%");
                stack = stack.replace("{titles_current}", c == null || !(c instanceof SkyWarsTitle) ? config.getAsString("empty") : ((SkyWarsTitle) c).getRawName());

                // Symbols
                Level level = Level.getByLevel(account.getLevel());
                double currentExp = account.getExp();
                double needExp = level.getNext() == null ? 0.0 : level.getNext().getExp();
                stack = stack.replace("{exp}", StringUtils.formatPerMil(currentExp));
                stack = stack.replace("{nextExp}", needExp != 0.0 ? StringUtils.formatPerMil(needExp) : "Max");
                stack = stack.replace("{level}", level.getLevel(account));
                stack = stack.replace("{progressBar}", "§8[ " + account.makeProgressBar(true) + " §8]");
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
