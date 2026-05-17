package org.twightlight.skywars.cosmetics.perk.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.config.YamlWrapper;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.perk.Perk;
import org.twightlight.skywars.cosmetics.perk.PerkManager;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;

import java.util.concurrent.ThreadLocalRandom;

public class VoidMaster extends Perk {

    private int percentage;

    private static final YamlWrapper CONFIG = YamlWrapper.getConfig("perks");

    public VoidMaster() {
        super(CONFIG.getInt("voidmaster.id"),
                CONFIG.getString("voidmaster.name"),
                CosmeticRarity.fromName(CONFIG.getString("voidmaster.rarity")),
                CONFIG.getBoolean("voidmaster.buyable", true),
                CONFIG.getString("voidmaster.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("voidmaster.icon").replace("{percentage}", CONFIG.getInt("voidmaster.percentage") + "%")),
                CONFIG.getInt("voidmaster.price"),
                PerkManager.loadAllowedGroups("voidmaster"));

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
            if (!this.isSelected(account)) {
                return;
            }
            if (ThreadLocalRandom.current().nextInt(100) < percentage) {
                evt.getKiller().getInventory().addItem(BukkitUtils.deserializeItemStack("ENDER_PEARL : 1"));
            }
        }
    }

}
