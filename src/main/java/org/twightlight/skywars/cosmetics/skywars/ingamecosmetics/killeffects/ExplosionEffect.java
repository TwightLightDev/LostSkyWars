package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.killeffects;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;


public class ExplosionEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public ExplosionEffect() {
        super(CONFIG.getInt( "explosion.id"),
                CONFIG.getString("explosion.name"),
                CosmeticRarity.fromName(CONFIG.getString("explosion.rarity")),
                CONFIG.getBoolean("explosion.buyable", true),
                CONFIG.getString("explosion.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("explosion.icon")),
                CONFIG.getInt("explosion.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        final TNTPrimed tnt = victim.getWorld().spawn(victim.getLocation().add(0.0D, 6.0D, 0.0D), TNTPrimed.class);
        tnt.setFuseTicks(2147483647);
        (new BukkitRunnable() {
            public void run() {
                if (!killer.isOnline()) {
                    cancel();
                    return;
                }
                if (!tnt.isValid()) {
                    cancel();
                    return;
                }
                if (tnt.isOnGround()) {
                    ParticleType.of("EXPLOSION_HUGE").spawn(victim.getWorld(), tnt.getLocation(), 1, 0.0F, 0.0F, 0.0F, 0.0F);
                    tnt.getWorld().playSound(tnt.getLocation(), Sound.EXPLODE, 1.5F, 1.0F);
                    tnt.remove();
                    cancel();
                    return;
                }
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 0L);
    }
}
