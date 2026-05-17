package org.twightlight.skywars.cosmetics.visual.assets.killeffects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsKillEffect;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;
import org.twightlight.skywars.utils.player.RenderUtils;
import org.twightlight.skywars.utils.math.VectorUtils;

import java.awt.*;
import java.awt.image.BufferedImage;


public class CryingEffect extends SkyWarsKillEffect {

    public CryingEffect() {
        super(CONFIG.getInt("crying.id"),
                CONFIG.getString("crying.name"),
                CosmeticRarity.fromName(CONFIG.getString("crying.rarity")),
                CONFIG.getBoolean("crying.buyable", true),
                CONFIG.getString("crying.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("crying.icon")),
                CONFIG.getInt("crying.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        (new BukkitRunnable() {
            final double stepX = 1.0D;

            final double stepY = 1.0D;

            final double size = 0.15D;

            int i = 0;

            final String text = ":(";

            final Font font = new Font(text, 0, 12);

            final BufferedImage image = RenderUtils.stringToBufferedImage(this.font, text, 90);

            final boolean invert = false;

            final Location loc = victim.getEyeLocation();

            public void run() {
                if (killer == null || !killer.isOnline()) {
                    cancel();
                    return;
                }
                if (this.font == null) {
                    cancel();
                    return;
                }
                if (this.i > 26) {
                    cancel();
                    return;
                }
                this.i++;
                Location location = this.loc;
                try {
                    for (int y = 0; y < this.image.getHeight(); y = (int)(y + 1.0D)) {
                        int x;
                        for (x = 0; x < this.image.getWidth(); x = (int)(x + 1.0D)) {
                            int clr = this.image.getRGB(x, y);
                            if (Color.black.getRGB() == clr) {
                                Vector v = (new Vector(this.image.getWidth() / 2.0F - x, this.image.getHeight() / 2.0F - y, 0.0F)).multiply(0.15D);
                                VectorUtils.rotateAroundAxisY(v, (-location.getYaw() * 0.017453292F));
                                ParticleType.of("FLAME").spawn(loc.getWorld(), this.loc.add(v).add(0.0D, 0.4D, 0.0D), 1, 0.0F, 0.0F, 0.0F, 5.0E-4F);
                                this.loc.subtract(v).subtract(0.0D, 0.4D, 0.0D);
                            }
                        }
                    }
                } catch (Exception ex) {
                    cancel();
                    ex.printStackTrace();
                }
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 3L);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {
        (new BukkitRunnable() {
            final double stepX = 1.0D;

            final double stepY = 1.0D;

            final double size = 0.15D;

            int i = 0;

            final String text = ":(";

            final Font font = new Font(text, 0, 12);

            final BufferedImage image = RenderUtils.stringToBufferedImage(this.font, text, 90);

            final boolean invert = false;

            final Location loc = location.clone().add(0, 1.6, 0);

            public void run() {
                if (player == null || !player.isOnline()) {
                    cancel();
                    return;
                }
                if (this.font == null) {
                    cancel();
                    return;
                }
                if (this.i > 26) {
                    cancel();
                    return;
                }
                this.i++;
                Location location = this.loc;
                try {
                    for (int y = 0; y < this.image.getHeight(); y = (int)(y + 1.0D)) {
                        int x;
                        for (x = 0; x < this.image.getWidth(); x = (int)(x + 1.0D)) {
                            int clr = this.image.getRGB(x, y);
                            if (Color.black.getRGB() == clr) {
                                Vector v = (new Vector(this.image.getWidth() / 2.0F - x, this.image.getHeight() / 2.0F - y, 0.0F)).multiply(0.15D);
                                VectorUtils.rotateAroundAxisY(v, (-location.getYaw() * 0.017453292F));
                                ParticleType.of("FLAME").spawn(player, this.loc.add(v).add(0.0D, 0.4D, 0.0D), 1, 0.0F, 0.0F, 0.0F, 5.0E-4F);
                                this.loc.subtract(v).subtract(0.0D, 0.4D, 0.0D);
                            }
                        }
                    }
                } catch (Exception ex) {
                    cancel();
                    ex.printStackTrace();
                }
            }
        }).runTaskTimer(SkyWars.getInstance(), 10L, 3L);
    }
}
