package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.killeffects.christmas;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;


public class ChristmasHatEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public ChristmasHatEffect() {
        super(CONFIG.getInt("chrismas-hat.id"),
                CONFIG.getString("chrismas-hat.name"),
                CosmeticRarity.fromName(CONFIG.getString("chrismas-hat.rarity")),
                CONFIG.getBoolean("chrismas-hat.buyable", true),
                CONFIG.getString("chrismas-hat.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("chrismas-hat.icon")),
                CONFIG.getInt("chrismas-hat.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        (new BukkitRunnable() {
            final Player player = (Player)victim;
            final Player killer1 = killer;
            Location prevLoc = null;

            int ticks = 0;

            public void run() {
                this.ticks++;
                if (this.killer1 == null || !this.killer1.isOnline() || this.ticks > 100) {
                    cancel();
                    return;
                }
                Location playerLoc = this.player.getLocation();
                playerLoc.setYaw(0.0F);
                playerLoc.setPitch(0.0F);
                if (this.prevLoc == null)
                    this.prevLoc = playerLoc;
                if (!this.prevLoc.equals(playerLoc)) {
                    this.prevLoc = playerLoc;
                    return;
                }
                Location loc = this.player.getEyeLocation().add(0.0D, 0.3D, 0.0D);
                int max = 8;
                int i;
                for (i = 0; i < max; i++) {
                    double cos = Math.cos(Math.toRadians(360.0D / max * i));
                    double sin = Math.sin(Math.toRadians(360.0D / max * i));
                    ParticleEffect.REDSTONE.display(0.0F, 0.0F, 0.0F, 0.0F, 1, loc.clone().add(0.25D * cos, 0.1D, 0.25D * sin), location.getWorld().getPlayers());
                    ParticleEffect.REDSTONE.display(0.0F, 0.0F, 0.0F, 0.0F, 1, loc.clone().add(0.16D * cos, 0.2D, 0.16D * sin), location.getWorld().getPlayers());
                    ParticleEffect.REDSTONE.display(0.0F, 0.0F, 0.0F, 0.0F, 1, loc.clone().add(0.07D * cos, 0.3D, 0.07D * sin), location.getWorld().getPlayers());
                    ParticleEffect.REDSTONE.display(0.0F, 0.0F, 0.0F, 0.0F, 1, loc.clone().add(0.07D * cos, 0.4D, 0.07D * sin), location.getWorld().getPlayers());
                    ParticleEffect.REDSTONE.display((ParticleEffect.ParticleColor)new ParticleEffect.OrdinaryColor(255, 255, 255), loc.clone().add(0.35D * cos, 0.0D, 0.35D * sin), 120.0D);
                }
                for (i = 0; i < 7; i++)
                    ParticleEffect.REDSTONE.display((ParticleEffect.ParticleColor)new ParticleEffect.OrdinaryColor(255, 255, 255), loc.clone().add((Math.random() - 0.5D) / 10.0D, 0.6D, (Math.random() - 0.5D) / 10.0D), 120.0D);
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 1L);
    }
}
