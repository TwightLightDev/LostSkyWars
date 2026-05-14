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
import org.twightlight.skywars.config.ConfigUtils;

import java.util.concurrent.ThreadLocalRandom;

public class LuckyCharm extends SkyWarsPerk {

    private int percentage;

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public LuckyCharm() {
        super(CONFIG.getInt("luckycharm.id"),
                CONFIG.getString("luckycharm.name"),
                CosmeticRarity.fromName(CONFIG.getString("luckycharm.rarity")),
                CONFIG.getBoolean("luckycharm.buyable", true),
                CONFIG.getString("luckycharm.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("luckycharm.icon").replace("{percentage}", CONFIG.getInt("luckycharm.percentage") + "%")),
                CONFIG.getInt("luckycharm.price"),
                SkyWarsPerk.loadAllowedGroups("luckycharm"));

        this.percentage = CONFIG.getInt("luckycharm.percentage");

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
            if (ThreadLocalRandom.current().nextInt(100) < percentage) {
                evt.getKiller().getInventory().addItem(BukkitUtils.deserializeItemStack("GOLDEN_APPLE : 1"));
            }
        }
    }

}
