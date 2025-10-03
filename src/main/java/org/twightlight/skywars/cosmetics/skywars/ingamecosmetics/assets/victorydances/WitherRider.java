package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.victorydances;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsVictoryDance;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.VectorUtils;


public class WitherRider extends SkyWarsVictoryDance {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("victorydances");

    public WitherRider() {
        super(CONFIG.getInt("wither-rider.id"),
                CONFIG.getString("wither-rider.name"),
                CosmeticRarity.fromName(CONFIG.getString("wither-rider.rarity")),
                CONFIG.getBoolean("wither-rider.buyable", true),
                CONFIG.getString("wither-rider.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("wither-rider.icon")),
                CONFIG.getInt("wither-rider.price"));

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerInteract(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                if (player.getVehicle() instanceof Wither || player.getVehicle().hasMetadata("VD")) {
                    WitherSkull witherSkull = player.getWorld().spawn(player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().normalize().multiply(2)), WitherSkull.class);
                    witherSkull.setShooter(player);
                    witherSkull.setVelocity(player.getEyeLocation().clone().getDirection().normalize().multiply(3));
                }
            }
        }, SkyWars.getInstance());
    }

    public void execute(Player winner) {
        Wither wither = (Wither)winner.getWorld().spawnEntity(winner.getLocation(), EntityType.WITHER);
        wither.setPassenger(winner);
        wither.setMetadata("VD", new FixedMetadataValue(SkyWars.getInstance(), ""));
        wither.setCustomName(ChatColor.translateAlternateColorCodes('&', "&a" + winner.getName() + "'s Wither!"));
        wither.setNoDamageTicks(2147483647);
        (new BukkitRunnable() {
            int ticks = 0;

            public void run() {
                if (this.ticks >= 200 || !winner.isOnline()) {
                    wither.remove();
                    cancel();
                    return;
                }

                if (wither.getPassenger() != winner)
                    wither.setPassenger(winner);
                Vector direction = winner.getEyeLocation().clone().getDirection().normalize().multiply(0.5D);
                wither.setVelocity(direction);
                wither.setTarget(null);

                ticks++;
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 1L);
    }
}
