package org.twightlight.skywars.cosmetics.skywars.perks;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsPerk;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigUtils;

import java.util.concurrent.ThreadLocalRandom;

public class ArrowRecovery extends SkyWarsPerk {

    private int percentage;

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public ArrowRecovery() {
        super(CONFIG.getInt("arrowrecovery.id"),
                CONFIG.getString("arrowrecovery.name"),
                CosmeticRarity.fromName(CONFIG.getString("arrowrecovery.rarity")),
                CONFIG.getBoolean("arrowrecovery.buyable", true),
                CONFIG.getString("arrowrecovery.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("arrowrecovery.icon").replace("{percentage}", CONFIG.getInt("arrowrecovery.percentage") + "%")),
                CONFIG.getInt("arrowrecovery.price"),
                SkyWarsPerk.loadAllowedGroups("arrowrecovery"));

        this.percentage = CONFIG.getInt("arrowrecovery.percentage");

        this.register(SkyWars.getInstance());
    }

    @EventHandler
    public void onShootBow(ProjectileHitEvent evt) {
        if (evt.getEntity() instanceof Arrow) {
            if (evt.getEntity().getShooter() instanceof Player) {
                Account account = Database.getInstance().getAccount(((Player) evt.getEntity().getShooter()).getUniqueId());
                if (account == null) {
                    return;
                }
                if (!this.selected(account)) {
                    return;
                }
                if (isAbleToUse((Player) evt.getEntity().getShooter()) && ThreadLocalRandom.current().nextInt(100) < percentage) {
                    ((Player) evt.getEntity().getShooter()).getInventory().addItem(new ItemStack(Material.ARROW));
                }
            }
        }
    }

}
