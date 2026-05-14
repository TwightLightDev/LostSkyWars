package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsKillEffect;
import org.twightlight.skywars.nms.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigUtils;
import org.twightlight.skywars.utils.VectorUtils;

import java.util.Random;


public class RainbowEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    private static final Color[] COLORS = new Color[] { Color.fromRGB(146, 65, 146),
            Color.fromRGB(1, 37, 184),
            Color.fromRGB(0, 162, 232),
            Color.fromRGB(126, 213, 40),
            Color.fromRGB(254, 203, 1),
            Color.fromRGB(255, 127, 89),
            Color.fromRGB(250, 11, 17) };

    public RainbowEffect() {
        super(CONFIG.getInt("rainbow.id"),
                CONFIG.getString("rainbow.name"),
                CosmeticRarity.fromName(CONFIG.getString("rainbow.rarity")),
                CONFIG.getBoolean("rainbow.buyable", true),
                CONFIG.getString("rainbow.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("rainbow.icon")),
                CONFIG.getInt("rainbow.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        if (killer == null || !killer.isOnline())
            return;
        (new BukkitRunnable() {
            final double MAX = 20.0D;

            int ticks = 0;

            final Random random = new Random();

            final double eyeHeight = victim.getEyeHeight();

            final Location location = victim.getLocation();

            public void run() {
                if (this.ticks++ > 20) {
                    cancel();
                    return;
                }
                for (float i = 0.0F; i < 20.0D; i++) {
                    double scaled = i / 20.0D;
                    double x = (scaled - 0.5D) * 2.4D;
                    for (int j = 0; j < COLORS.length; j++) {
                        double multiplier = 1.0D + (double) j / COLORS.length;
                        Vector vector = new Vector(x * multiplier * 1.3D, this.eyeHeight, 0.0D);
                        VectorUtils.rotateAroundAxisY(vector, (-this.location.getYaw() * 0.017453292F));
                        double y = Math.sin(scaled * Math.PI) * multiplier * 0.95D;
                        ParticleEffect.REDSTONE.display((ParticleEffect.ParticleColor)new ParticleEffect.OrdinaryColor(COLORS[j].getRed(), COLORS[j].getGreen(), COLORS[j].getBlue()), this.location.add(vector).add(0.0D, y, 0.0D), 128.0D);
                        if ((i == 0.0F || i == 19.0D) && this.random.nextBoolean())
                            ParticleEffect.CLOUD.display(0.0F, 0.0F, 0.0F, 0.002F, 1, this.location.add(vector), location.getWorld().getPlayers());
                    }
                }
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 3L);
    }

    @Override
    public void killEffectPreview(Player player, Location location1) {

        if (player == null || !player.isOnline())
            return;
        (new BukkitRunnable() {

            int ticks = 0;

            final Random random = new Random();

            final Location location = location1.clone();

            final double eyeHeight = location.clone().add(0, 1.6, 0).getY();

            public void run() {
                if (this.ticks++ > 30) {
                    cancel();
                    return;
                }
                for (float i = 0.0F; i < 20.0D; i++) {
                    double scaled = i / 20.0D;
                    double x = (scaled - 0.5D) * 2.4D;
                    for (int j = 0; j < COLORS.length; j++) {
                        double multiplier = 1.0D + (double) j / COLORS.length;
                        Vector vector = new Vector(x * multiplier * 1.3D, this.eyeHeight, 0.0D);
                        VectorUtils.rotateAroundAxisY(vector, (-this.location.getYaw() * 0.017453292F));
                        double y = Math.sin(scaled * Math.PI) * multiplier * 0.95D;
                        ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(COLORS[j].getRed(), COLORS[j].getGreen(), COLORS[j].getBlue()), this.location.add(vector).add(0.0D, y, 0.0D), player);
                        if ((i == 0.0F || i == 19.0D) && this.random.nextBoolean())
                            ParticleEffect.CLOUD.display(0.0F, 0.0F, 0.0F, 0.002F, 1, this.location.add(vector), player);
                    }
                }
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 3L);
    }
}
