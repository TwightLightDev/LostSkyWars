package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

import java.util.Random;


public class VolcanoEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public VolcanoEffect() {
        super(CONFIG.getInt("volcano.id"),
                CONFIG.getString("volcano.name"),
                CosmeticRarity.fromName(CONFIG.getString("volcano.rarity")),
                CONFIG.getBoolean("volcano.buyable", true),
                CONFIG.getString("volcano.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("volcano.icon")),
                CONFIG.getInt("volcano.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        (new BukkitRunnable() {
            Location location = victim.getLocation().add(0.0D, 3.3D, 0.0D);

            int ticks = 0;
            Random random = new Random();
            public void run() {
                if (this.ticks++ > 40) {
                    cancel();
                    return;
                }
                if (random.nextInt(100) + 1 < 20)
                    ParticleEffect.DRIP_LAVA.display(0.4F, 0.1F, 0.4F, 1.0F, 6, this.location, location.getWorld().getPlayers());
                ParticleEffect.REDSTONE.display(0.4F, 0.1F, 0.4F, 0.0F, 5, this.location, location.getWorld().getPlayers());
                ParticleEffect.SMOKE_NORMAL.display(0.6F, 0.2F, 0.6F, 0.01F, 8, this.location, location.getWorld().getPlayers());
                ParticleEffect.SMOKE_LARGE.display(0.6F, 0.2F, 0.6F, 0.0075F, 3, this.location, location.getWorld().getPlayers());
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 0L);
    }
}
