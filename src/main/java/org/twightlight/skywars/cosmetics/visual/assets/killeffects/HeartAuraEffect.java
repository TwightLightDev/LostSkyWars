package org.twightlight.skywars.cosmetics.visual.assets.killeffects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsKillEffect;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;


public class HeartAuraEffect extends SkyWarsKillEffect {

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("killeffects");

    public HeartAuraEffect() {
        super(CONFIG.getInt("heart-aura.id"),
                CONFIG.getString("heart-aura.name"),
                CosmeticRarity.fromName(CONFIG.getString("heart-aura.rarity")),
                CONFIG.getBoolean("heart-aura.buyable", true),
                CONFIG.getString("heart-aura.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("heart-aura.icon")),
                CONFIG.getInt("heart-aura.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        ParticleType particle = ParticleType.of("HEART");
        particle.spawn(location.getWorld(), location, 100, 0, 0, 0, 0.01F);

        particle.spawn(location.getWorld(), location, 100, 0.0F, 0.1F, 0.0F, 0.01F);

        particle.spawn(location.getWorld(), location, 100, 0.0F, 0.2F, 0.0F, 0.01F);

        particle.spawn(location.getWorld(), location, 100, 0.0F, 0.3F, 0.0F, 0.01F);

        particle.spawn(location.getWorld(), location, 100, 0.0F, 0.4F, 0.1F, 0.01F);

        particle.spawn(location.getWorld(), location, 100, 0.0F, 0.5F, 0.3F, 0.01F);

        particle.spawn(location.getWorld(), location, 100, 0.1F, 0.0F, 0.0F, 0.01F);

        particle.spawn(location.getWorld(), location, 100, 0.2F, 0.3F, 0.0F, 0.01F);

        particle.spawn(location.getWorld(), location, 100, 0.3F, 0.0F, 0.0F, 0.01F);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {
        ParticleType particle = ParticleType.of("HEART");
        particle.spawn(player, location, 100, 0, 0, 0, 0.01F);

        particle.spawn(player, location, 100, 0.0F, 0.1F, 0.0F, 0.01F);

        particle.spawn(player, location, 100, 0.0F, 0.2F, 0.0F, 0.01F);

        particle.spawn(player, location, 100, 0.0F, 0.3F, 0.0F, 0.01F);

        particle.spawn(player, location, 100, 0.0F, 0.4F, 0.1F, 0.01F);

        particle.spawn(player, location, 100, 0.0F, 0.5F, 0.3F, 0.01F);

        particle.spawn(player, location, 100, 0.1F, 0.0F, 0.0F, 0.01F);

        particle.spawn(player, location, 100, 0.2F, 0.3F, 0.0F, 0.01F);

        particle.spawn(player, location, 100, 0.3F, 0.0F, 0.0F, 0.01F);

    }
}
