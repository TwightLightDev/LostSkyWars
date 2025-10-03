package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects.christmas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsKillEffect;
import org.twightlight.skywars.nms.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;


public class SnowmanEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public SnowmanEffect() {
        super(CONFIG.getInt("snowman.id"),
                CONFIG.getString("snowman.name"),
                CosmeticRarity.fromName(CONFIG.getString("snowman.rarity")),
                CONFIG.getBoolean("snowman.buyable", true),
                CONFIG.getString("snowman.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("snowman.icon")),
                CONFIG.getInt("snowman.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        Snowman stand = (Snowman)victim.getLocation().getWorld().spawn(victim.getLocation().add(0.0D, 1.54D, 0.0D), Snowman.class);
        stand.setMetadata("cosmetic.entity", (MetadataValue)new FixedMetadataValue(SkyWars.getInstance(), Boolean.valueOf(true)));
        stand.setVelocity(new Vector(0.0D, 0.85D, 0.0D));
        stand.getWorld().playSound(stand.getLocation(), Sound.FIREWORK_LAUNCH, 2.0F, 0.0F);
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            ParticleEffect.SNOW_SHOVEL.display(0.0F, 0.0F, 0.0F, 0.5F, 25, stand.getLocation());
            ParticleEffect.SNOWBALL.display(0.0F, 0.0F, 0.0F, 0.5F, 25, stand.getLocation());
            stand.getWorld().playSound(stand.getLocation(), Sound.ITEM_PICKUP, 1.0F, 0.0F);
            stand.remove();
        }, 50L);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {
        Snowman stand = location.getWorld().spawn(location.clone().add(0.0D, 1.54D, 0.0D), Snowman.class);
        stand.setMetadata("cosmetic.entity", (MetadataValue)new FixedMetadataValue(SkyWars.getInstance(), Boolean.valueOf(true)));
        stand.setVelocity(new Vector(0.0D, 0.85D, 0.0D));
        player.playSound(stand.getLocation(), Sound.FIREWORK_LAUNCH, 2.0F, 0.0F);
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            ParticleEffect.SNOW_SHOVEL.display(0.0F, 0.0F, 0.0F, 0.5F, 25, stand.getLocation(), player);
            ParticleEffect.SNOWBALL.display(0.0F, 0.0F, 0.0F, 0.5F, 25, stand.getLocation(), player);
            player.playSound(stand.getLocation(), Sound.ITEM_PICKUP, 1.0F, 0.0F);
            stand.remove();
        }, 90L);
    }
}
