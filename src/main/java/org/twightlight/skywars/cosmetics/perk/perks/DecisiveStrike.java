package org.twightlight.skywars.cosmetics.perk.perks;

import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.perk.Perk;
import org.twightlight.skywars.cosmetics.perk.PerkManager;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;

public class DecisiveStrike extends Perk {

    private int percentage;

    private static final YamlWrapper CONFIG = YamlWrapper.getConfig("perks");

    public DecisiveStrike() {
        super(CONFIG.getInt("decisivestrike.id"),
                CONFIG.getString("decisivestrike.name"),
                CosmeticRarity.fromName(CONFIG.getString("decisivestrike.rarity")),
                CONFIG.getBoolean("decisivestrike.buyable", true),
                CONFIG.getString("decisivestrike.permission"),
                BukkitUtils.deserializeItemStack(CONFIG.getString("decisivestrike.icon").replace("{percentage}", CONFIG.getInt("decisivestrike.percentage") + "%")),
                CONFIG.getInt("decisivestrike.price"),
                PerkManager.loadAllowedGroups("decisivestrike"));

        this.percentage = CONFIG.getInt("decisivestrike.percentage");
        this.register(null);
    }

    public int getPercentage() {
        return this.percentage;
    }
}
