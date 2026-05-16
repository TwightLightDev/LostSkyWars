package org.twightlight.skywars.cosmetics.perk.perks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.perk.Perk;
import org.twightlight.skywars.cosmetics.perk.PerkManager;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

public class EnderMastery extends Perk {

    private int percentage;

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("perks");

    public EnderMastery() {
        super(CONFIG.getInt("endermastery.id"), CONFIG.getString("endermastery.name"), CosmeticRarity.fromName(CONFIG.getString("endermastery.rarity")),
                CONFIG.getBoolean("endermastery.buyable", true),
                CONFIG.getString("endermastery.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("endermastery.icon").replace("{percentage}", CONFIG.getInt("endermastery.percentage") + "%")),
                CONFIG.getInt("endermastery.price"),
                PerkManager.loadAllowedGroups("endermastery"));

        this.percentage = CONFIG.getInt("endermastery.percentage");

        this.register(SkyWars.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        if (evt.getCause() == TeleportCause.ENDER_PEARL) {
            Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
            if (account == null) {
                return;
            }
            if (!this.isSelected(account)) {
                return;
            }
            if (account.getArena() != null) {
                evt.setCancelled(true);
                double damage = 5.0;

                if (isAbleToUse(evt.getPlayer())) {
                    damage = damage - ((percentage * damage) / 100.0);
                }

                evt.getPlayer().teleport(evt.getTo());
                if (damage > 0.0) {
                    evt.getPlayer().damage(damage);
                }
            }
        }
    }

}
