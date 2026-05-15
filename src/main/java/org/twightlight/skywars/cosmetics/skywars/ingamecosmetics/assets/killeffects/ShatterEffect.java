package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsKillEffect;
import org.twightlight.skywars.nms.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;


public class ShatterEffect extends SkyWarsKillEffect {

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("killeffects");

    public ShatterEffect() {
        super(CONFIG.getInt("shatter.id"),
                CONFIG.getString("shatter.name"),
                CosmeticRarity.fromName(CONFIG.getString("shatter.rarity")),
                CONFIG.getBoolean("shatter.buyable", true),
                CONFIG.getString("shatter.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("shatter.icon")),
                CONFIG.getInt("shatter.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        (new BukkitRunnable() {
            double size = 0.3D;

            final Location location = victim.getLocation();

            public void run() {
                if (this.size > 2.0D || !victim.isValid())
                    cancel();
                double max = Math.ceil(this.size * 5.0D + 15.0D);
                for (double i = 0.0D; i < max; i++) {
                    double radians = Math.toRadians(360.0D / max * i);
                    double x = Math.cos(radians) * this.size;
                    double z = Math.sin(radians) * this.size;
                    this.location.add(x, 0.0D, z);
                    ParticleEffect.BLOCK_CRACK.display((ParticleEffect.ParticleData)new ParticleEffect.BlockData(Material.GLASS, (byte) 0), 0.1F, 0.0F, 0.1F, 0.3F, 30, this.location, 32.0D);
                    this.location.subtract(x, 0.0D, z);
                }
                this.location.getWorld().playSound(this.location, Sound.GLASS, 0.5F + (float)this.size / 2.0F, 0.8F);
                this.size += 0.3D;
            }
        }).runTaskTimer(SkyWars.getInstance(), 3L, 3L);
    }

    @Override
    public void killEffectPreview(Player player, Location location1) {
        BukkitTask task = (new BukkitRunnable() {
            double size = 0.3D;

            final Location location = location1.clone();

            public void run() {
                if (this.size > 2.0D || !player.isValid())
                    cancel();
                double max = Math.ceil(this.size * 5.0D + 15.0D);
                for (double i = 0.0D; i < max; i++) {
                    double radians = Math.toRadians(360.0D / max * i);
                    double x = Math.cos(radians) * this.size;
                    double z = Math.sin(radians) * this.size;
                    this.location.add(x, 0.0D, z);
                    ParticleEffect.BLOCK_CRACK.display((ParticleEffect.ParticleData)new ParticleEffect.BlockData(Material.GLASS, (byte) 0), 0.1F, 0.0F, 0.1F, 0.3F, 30, this.location, player);
                    this.location.subtract(x, 0.0D, z);
                }
                player.playSound(this.location, Sound.GLASS, 0.5F + (float)this.size / 2.0F, 0.8F);
                this.size += 0.3D;
            }
        }).runTaskTimer(SkyWars.getInstance(), 3L, 3L);

        sessionUUID.get(player.getUniqueId()).addEndConsumers((p) -> {
            task.cancel();
        });
    }
}
