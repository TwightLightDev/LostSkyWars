package org.twightlight.skywars.cosmetics.skywars.perks;

import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsPerk;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

public class DecisiveStrike extends SkyWarsPerk {

    private int mode;
    private int percentage;

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public DecisiveStrike(int mode) {
        super(CONFIG.getInt("decisivestrike.id"),
                CONFIG.getString("decisivestrike.name"),
                CosmeticRarity.fromName(CONFIG.getString("decisivestrike.rarity")),
                CONFIG.getBoolean("decisivestrike.buyable", true),
                CONFIG.getString("decisivestrike.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("decisivestrike.icon").replace("{percentage}", CONFIG.getInt("decisivestrike.percentage") + "%")),
                CONFIG.getInt("decisivestrike.price"));
        this.mode = mode;

        this.percentage = CONFIG.getInt("decisivestrike.percentage");
        this.register(null);
    }

    public int getPercentage() {
        return this.percentage;
    }

    @Override
    public int getMode() {
        return mode;
    }
}
