package org.twightlight.skywars.cosmetics.perk.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.perk.Perk;
import org.twightlight.skywars.cosmetics.perk.PerkManager;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

public class Knowledge extends Perk {

    private int level;

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("perks");

    public Knowledge() {
        super(CONFIG.getInt("knowledge.id"),
                CONFIG.getString("knowledge.name"),
                CosmeticRarity.fromName(CONFIG.getString("knowledge.rarity")),
                CONFIG.getBoolean("knowledge.buyable", true),
                CONFIG.getString("knowledge.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("knowledge.icon").replace("{level}", CONFIG.getInt("knowledge.level") + "")),
                CONFIG.getInt("knowledge.price"),
                PerkManager.loadAllowedGroups("knowledge"));

        this.level = CONFIG.getInt("knowledge.level");

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
            evt.getKiller().setLevel(evt.getKiller().getLevel() + this.level);
        }
    }

}
