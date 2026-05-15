package org.twightlight.skywars.cosmetics.skywars.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsPerk;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

public class Nourishment extends SkyWarsPerk {


    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("perks");

    public Nourishment() {
        super(CONFIG.getInt("nourishment.id"),
                CONFIG.getString("nourishment.name"),
                CosmeticRarity.fromName(CONFIG.getString("nourishment.rarity")),
                CONFIG.getBoolean("nourishment.buyable", true),
                CONFIG.getString("nourishment.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("nourishment.icon")),
                CONFIG.getInt("nourishment.price"),
                SkyWarsPerk.loadAllowedGroups("nourishment"));

        this.register(SkyWars.getInstance());
    }

    @EventHandler
    public void onPlayerDeath(SkyWarsPlayerDeathEvent evt) {
        if (evt.isKilled() && isAbleToUse(evt.getKiller())) {
            Account account = Database.getInstance().getAccount(((Player) evt.getKiller()).getUniqueId());
            if (account == null) {
                return;
            }
            if (!this.selected(account)) {
                return;
            }
            evt.getKiller().setFoodLevel(20);
            evt.getKiller().setSaturation(5.0F);
        }
    }
}
