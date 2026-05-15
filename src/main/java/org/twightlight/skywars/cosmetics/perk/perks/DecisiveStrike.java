package org.twightlight.skywars.cosmetics.perk.perks;

import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.perk.Perk;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsPerk;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

public class DecisiveStrike extends Perk {

    private int percentage;

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("perks");

    public DecisiveStrike() {
        super(CONFIG.getInt("decisivestrike.id"),
                CONFIG.getString("decisivestrike.name"),
                CosmeticRarity.fromName(CONFIG.getString("decisivestrike.rarity")),
                CONFIG.getBoolean("decisivestrike.buyable", true),
                CONFIG.getString("decisivestrike.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("decisivestrike.icon").replace("{percentage}", CONFIG.getInt("decisivestrike.percentage") + "%")),
                CONFIG.getInt("decisivestrike.price"),
                SkyWarsPerk.loadAllowedGroups("decisivestrike"));

        this.percentage = CONFIG.getInt("decisivestrike.percentage");
        this.register(null);
    }

    public int getPercentage() {
        return this.percentage;
    }
}
