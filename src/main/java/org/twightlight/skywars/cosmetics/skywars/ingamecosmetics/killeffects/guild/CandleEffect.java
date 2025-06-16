package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.killeffects.guild;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.holograms.Hologram;
import org.twightlight.skywars.holograms.Holograms;
import org.twightlight.skywars.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CandleEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public CandleEffect() {
        super(CONFIG.getInt("candle.id"),
                CONFIG.getString("candle.name"),
                CosmeticRarity.fromName(CONFIG.getString("candle.rarity")),
                CONFIG.getBoolean("candle.buyable", true),
                CONFIG.getString("candle.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("candle.icon")),
                CONFIG.getInt("candle.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        final Location loc = getBlockUnder(victim.getLocation());
        if (loc == null)
            return;
        loc.add(0.0D, 1.0D, 0.0D);
        loc.setX(loc.getBlockX());
        loc.setZ(loc.getBlockZ());
        final Block block = loc.getBlock();
        block.setType(Material.TORCH);
        Hologram hologram = Holograms.createHologram(loc.clone().add(0.5D, 1.45D, 0.5D), "&6RIP");
        Hologram hologram1 = Holograms.createHologram(loc.clone().add(0.5D, 1.15D, 0.5D), victim.getDisplayName());

        (new BukkitRunnable() {
            public void run() {
                if (block.getType() == Material.TORCH)
                    block.setType(Material.AIR);
                ParticleEffect.CLOUD.display(0.1F, 0.2F, 0.1F, 0.01F, 10, loc, 120.0D);
                hologram1.despawn();
                hologram.despawn();
            }
        }).runTaskLater(Main.getInstance(), 100L);
    }

    private Location getBlockUnder(Location location) {
        while (location.getBlock().getType().equals(Material.AIR)) {
            if (location.getBlockY() < 0)
                return null;
            location.subtract(0.0D, 1.0D, 0.0D);
        }
        return location;
    }

}
