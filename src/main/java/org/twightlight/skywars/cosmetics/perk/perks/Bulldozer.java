package org.twightlight.skywars.cosmetics.perk.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.perk.Perk;
import org.twightlight.skywars.cosmetics.perk.PerkManager;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

public class Bulldozer extends Perk {

    private int seconds;

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("perks");

    public Bulldozer() {
        super(CONFIG.getInt("bulldozer.id"),
                CONFIG.getString("bulldozer.name"),
                CosmeticRarity.fromName(CONFIG.getString("bulldozer.rarity")),
                CONFIG.getBoolean("bulldozer.buyable", true),
                CONFIG.getString("bulldozer.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("bulldozer.icon").replace("{time}", CONFIG.getInt("bulldozer.time") + "")),
                CONFIG.getInt("bulldozer.price"),
                PerkManager.loadAllowedGroups("bulldozer"));

        this.seconds = CONFIG.getInt("bulldozer.time");

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
            evt.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * seconds, 0));
        }
    }

}
