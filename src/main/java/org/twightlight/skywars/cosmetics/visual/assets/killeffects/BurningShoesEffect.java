package org.twightlight.skywars.cosmetics.visual.assets.killeffects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsKillEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;


public class BurningShoesEffect extends SkyWarsKillEffect {

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("killeffects");

    public BurningShoesEffect() {
        super(CONFIG.getInt("burning-shoes.id"),
                CONFIG.getString("burning-shoes.name"),
                CosmeticRarity.fromName(CONFIG.getString("burning-shoes.rarity")),
                CONFIG.getBoolean("burning-shoes.buyable", true),
                CONFIG.getString("burning-shoes.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("burning-shoes.icon")),
                CONFIG.getInt("burning-shoes.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        (new BukkitRunnable() {
            double t = 0.0D;

            public void run() {
                this.t += 0.3D;
                for (double phi = 0.0D; phi <= 6.0D; phi += 1.5D) {
                    double x = 0.11D * (12.5D - this.t) * Math.cos(this.t + phi);
                    double y = 0.23D * this.t;
                    double z = 0.11D * (12.5D - this.t) * Math.sin(this.t + phi);
                    location.add(x, y, z);
                    ParticleType.of("FLAME").spawn(location.getWorld(), location, 1);
                    location.subtract(x, y, z);
                    if (this.t >= 12.5D) {
                        location.add(x, y, z);
                        if (phi > Math.PI)
                            cancel();
                    }
                }
            }
        }).runTaskTimer(SkyWars.getInstance(), 1L, 1L);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {

        (new BukkitRunnable() {
            double t = 0.0D;

            public void run() {
                this.t += 0.3D;
                for (double phi = 0.0D; phi <= 6.0D; phi += 1.5D) {
                    double x = 0.11D * (12.5D - this.t) * Math.cos(this.t + phi);
                    double y = 0.23D * this.t;
                    double z = 0.11D * (12.5D - this.t) * Math.sin(this.t + phi);
                    location.add(x, y, z);
                    ParticleType.of("FLAME").spawn(player, location, 1);
                    location.subtract(x, y, z);
                    if (this.t >= 12.5D) {
                        location.add(x, y, z);
                        if (phi > Math.PI)
                            cancel();
                    }
                }
            }
        }).runTaskTimer(SkyWars.getInstance(), 21L, 1L);
    }
}
