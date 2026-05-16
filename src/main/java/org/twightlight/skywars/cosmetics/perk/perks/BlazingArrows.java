package org.twightlight.skywars.cosmetics.perk.perks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.perk.Perk;
import org.twightlight.skywars.cosmetics.perk.PerkManager;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

import java.util.concurrent.ThreadLocalRandom;

public class BlazingArrows extends Perk {

    private int percentage;

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("perks");

    public BlazingArrows() {
        super(CONFIG.getInt("blazingarrow.id"),
                CONFIG.getString("blazingarrow.name"),
                CosmeticRarity.fromName(CONFIG.getString("blazingarrow.rarity")),
                CONFIG.getBoolean("blazingarrow.buyable", true),
                CONFIG.getString("blazingarrow.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("blazingarrow.icon").replace("{percentage}", CONFIG.getInt("blazingarrow.percentage") + "%")),
                CONFIG.getInt("blazingarrow.price"),
                PerkManager.loadAllowedGroups("blazingarrow"));

        this.percentage = CONFIG.getInt("blazingarrow.percentage");

        this.register(SkyWars.getInstance());
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Account account = Database.getInstance().getAccount(((Player) evt.getEntity()).getUniqueId());
            if (account == null) {
                return;
            }
            if (!this.isSelected(account)) {
                return;
            }
            if (isAbleToUse((Player) evt.getEntity()) && ThreadLocalRandom.current().nextInt(100) < percentage) {
                ((Player) evt.getEntity()).getInventory().addItem(new ItemStack(Material.ARROW));
            }
        }
    }

}
