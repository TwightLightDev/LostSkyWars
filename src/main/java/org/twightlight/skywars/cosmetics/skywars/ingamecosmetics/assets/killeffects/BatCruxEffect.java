package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsKillEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class BatCruxEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public BatCruxEffect() {
        super(CONFIG.getInt("batcrux.id"),
                CONFIG.getString("batcrux.name"),
                CosmeticRarity.fromName(CONFIG.getString("batcrux.rarity")),
                CONFIG.getBoolean("batcrux.buyable", true),
                CONFIG.getString("batcrux.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("batcrux.icon")),
                CONFIG.getInt("batcrux.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        List<Bat> bats = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Bat bat = (Bat)location.getWorld().spawnEntity(location, EntityType.BAT);
            bat.setVelocity(bat.getLocation().getDirection().multiply(0.7D).setY(2));
            bat.setCustomNameVisible(true);
            int number = i + 1;
            bat.setCustomName(StringUtils.formatColors("&7" + victim.getName() + "'s horcrux #" + number));
            bats.add(bat);
        }
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            if (!bats.isEmpty())
                for (Bat bat : bats) {
                    Location batLocation = bat.getLocation().clone();
                    ParticleType.of("SMOKE_LARGE").spawn(batLocation.getWorld(), batLocation, 15, 3, 3, 3, 1);
                }
        }, 5L);
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            for (Bat bat : bats)
                bat.remove();
            bats.clear();
        }, 80L);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {
        List<Bat> bats = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Bat bat = (Bat)location.getWorld().spawnEntity(location, EntityType.BAT);
            bat.setVelocity(bat.getLocation().getDirection().multiply(0.7D).setY(2));
            bat.setCustomNameVisible(true);
            int number = i + 1;
            bat.setCustomName(StringUtils.formatColors("&7" + player.getName() + "'s horcrux #" + number));
            bats.add(bat);
        }
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            if (!bats.isEmpty())
                for (Bat bat : bats) {
                    Location batLocation = bat.getLocation().clone();
                    ParticleType.of("SMOKE_LARGE").spawn(player, batLocation, 15, 3, 3, 3, 1);
                }
        }, 5L);
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), () -> {
            for (Bat bat : bats)
                bat.remove();
            bats.clear();
        }, 80L);
    }
}
