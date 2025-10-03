package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.victorydances;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsVictoryDance;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.VectorUtils;


public class ThorEffect extends SkyWarsVictoryDance {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("victorydances");

    public ThorEffect() {
        super(CONFIG.getInt("thor.id"),
                CONFIG.getString("thor.name"),
                CosmeticRarity.fromName(CONFIG.getString("thor.rarity")),
                CONFIG.getBoolean("thor.buyable", true),
                CONFIG.getString("thor.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("thor.icon")),
                CONFIG.getInt("thor.price"));
    }

    public void execute(Player player) {
        player.getWorld().setThundering(true);
        player.getWorld().setTime(18000L);
        (new BukkitRunnable() {
            final int ticks = 0;

            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                Location location = player.getLocation().add(VectorUtils.randomVector().multiply(15));
                location = location.getWorld().getHighestBlockAt(location).getLocation();
                location.getWorld().strikeLightning(location);
            }
        }).runTaskTimer(SkyWars.getInstance(),0L, 6L);
        (new BukkitRunnable() {
            int ticks = 0;

            public void run() {
                if (this.ticks >= 100) {
                    cancel();
                    return;
                }
                Location location = player.getLocation().add(VectorUtils.randomVector().multiply(250));
                while (location.distance(player.getLocation()) < 100.0D)
                    location = player.getLocation().add(VectorUtils.randomVector().multiply(150));
                location = location.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ()).getLocation();
                location.getWorld().strikeLightningEffect(location);
                this.ticks++;
            }
        }).runTaskTimer(SkyWars.getInstance(),0L, 2L);
    }
}
