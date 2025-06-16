package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.killeffects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.libs.xseries.XSound;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

public class TornadoEffect extends SkyWarsKillEffect {
    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public TornadoEffect() {
        super(CONFIG.getInt("tornado.id"),
                CONFIG.getString("tornado.name"),
                CosmeticRarity.fromName(CONFIG.getString("tornado.rarity")),
                CONFIG.getBoolean("tornado.buyable", true),
                CONFIG.getString("tornado.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("tornado.icon")),
                CONFIG.getInt("tornado.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        XSound.ENTITY_GENERIC_EXPLODE.play(location, 1.0F, 1.0F);
        (new BukkitRunnable() {
            int angle = 0;

            public void run() {
                for (int l = 0; l < 4; l++) {
                    double y;
                    for (y = 0.0D; y < 7.0D; y += 0.25D) {
                        double radius = y * 0.42857142857142855D;
                        double x = Math.cos(Math.toRadians(90.0D * l + y * 30.0D - this.angle)) * radius;
                        double z = Math.sin(Math.toRadians(90.0D * l + y * 30.0D - this.angle)) * radius;
                        double finalY = y;
                        ParticleType.of("CLOUD").spawn(location.getWorld(), location.clone().add(x, finalY, z), 1, 0, 0, 0, 0);
                    }
                }
                this.angle++;
                if (this.angle == 70)
                    cancel();
            }
        }).runTaskTimer(Main.getInstance(), 2L, 0L);
    }
}
