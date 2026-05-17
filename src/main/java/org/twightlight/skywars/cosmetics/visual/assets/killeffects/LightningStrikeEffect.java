package org.twightlight.skywars.cosmetics.visual.assets.killeffects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsKillEffect;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;


public class LightningStrikeEffect extends SkyWarsKillEffect {

    public LightningStrikeEffect() {
        super(CONFIG.getInt("lightning-strike.id"),
                CONFIG.getString("lightning-strike.name"),
                CosmeticRarity.fromName(CONFIG.getString("lightning-strike.rarity")),
                CONFIG.getBoolean("lightning-strike.buyable", true),
                CONFIG.getString("lightning-strike.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("lightning-strike.icon")),
                CONFIG.getInt("lightning-strike.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        location.getWorld().strikeLightningEffect(location);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {

        location.getWorld().strikeLightningEffect(location);

    }
}
