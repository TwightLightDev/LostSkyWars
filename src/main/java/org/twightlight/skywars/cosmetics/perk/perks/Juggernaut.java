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

public class Juggernaut extends Perk {

    private int seconds;

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("perks");

    public Juggernaut() {
        super(CONFIG.getInt("juggernaut.id"),
                CONFIG.getString("juggernaut.name"),
                CosmeticRarity.fromName(CONFIG.getString("juggernaut.rarity")),
                CONFIG.getBoolean("juggernaut.buyable", true),
                CONFIG.getString("juggernaut.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("juggernaut.icon").replace("{time}", CONFIG.getInt("juggernaut.time") + "")),
                CONFIG.getInt("juggernaut.price"),
                PerkManager.loadAllowedGroups("juggernaut"));

        this.seconds = CONFIG.getInt("juggernaut.time");

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
            evt.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * seconds, 0));
        }
    }

}
