package org.twightlight.skywars.cosmetics.skywars.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsPerk;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

public class Knowledge extends SkyWarsPerk {

    private int mode;
    private int level;

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public Knowledge(int mode) {
        super(CONFIG.getInt("knowledge.id"),
                CONFIG.getString("knowledge.name"),
                CosmeticRarity.fromName(CONFIG.getString("knowledge.rarity")),
                CONFIG.getBoolean("knowledge.buyable", true),
                CONFIG.getString("knowledge.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("knowledge.icon").replace("{level}", CONFIG.getInt("knowledge.level") + "")),
                CONFIG.getInt("knowledge.price"));
        this.mode = mode;

        this.level = CONFIG.getInt("knowledge.level");

        this.register(Main.getInstance());
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
            evt.getKiller().setLevel(evt.getKiller().getLevel() + this.level);
        }
    }

    @Override
    public int getMode() {
        return mode;
    }
}
