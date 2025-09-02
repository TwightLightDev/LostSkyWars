package org.twightlight.skywars.cosmetics;

import org.bukkit.entity.Player;
import org.twightlight.skywars.utils.ConfigUtils;

public abstract class PreviewableCosmetic extends Cosmetic {
    protected static final ConfigUtils CONFIG = ConfigUtils.getConfig("locations");

    public PreviewableCosmetic(int id, CosmeticServer server, CosmeticType type, CosmeticRarity rarity) {
        super(id, server, type, rarity);
    }

    public abstract void preview(Player player);
}
