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
import org.twightlight.skywars.utils.ConfigUtils;

import java.util.concurrent.ThreadLocalRandom;

public class VoidMaster extends SkyWarsPerk {

    private int percentage;

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public VoidMaster() {
        super(CONFIG.getInt("voidmaster.id"),
                CONFIG.getString("voidmaster.name"),
                CosmeticRarity.fromName(CONFIG.getString("voidmaster.rarity")),
                CONFIG.getBoolean("voidmaster.buyable", true),
                CONFIG.getString("voidmaster.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("voidmaster.icon").replace("{percentage}", CONFIG.getInt("voidmaster.percentage") + "%")),
                CONFIG.getInt("voidmaster.price"),
                SkyWarsPerk.loadAllowedGroups("voidmaster"));

        this.percentage = CONFIG.getInt("voidmaster.percentage");

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
                evt.getKiller().getInventory().addItem(BukkitUtils.deserializeItemStack("ENDER_PEARL : 1"));
            }
        }
    }

}
