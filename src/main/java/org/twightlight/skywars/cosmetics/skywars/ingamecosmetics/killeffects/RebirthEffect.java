package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.killeffects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;


public class RebirthEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public RebirthEffect() {
        super(CONFIG.getInt("rebirth.id"),
                CONFIG.getString("rebirth.name"),
                CosmeticRarity.fromName(CONFIG.getString("rebirth.rarity")),
                CONFIG.getBoolean("rebirth.buyable", true),
                CONFIG.getString("rebirth.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("rebirth.icon")),
                CONFIG.getInt("rebirth.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        final Item item = victim.getWorld().dropItem(victim.getLocation(), new ItemStack(Material.DRAGON_EGG));
        item.setVelocity(new Vector(0.0D, 0.45D, 0.0D));
        item.setPickupDelay(2147483647);
        (new BukkitRunnable() {
            public void run() {
                if (item.getTicksLived() > 40 || item.isOnGround() || !item.isValid()) {
                    item.getWorld().playSound(item.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F, 1.0F);
                    ParticleEffect.SPELL_WITCH.display(0.0F, 0.0F, 0.0F, 0.3F, 20, item.getLocation(), location.getWorld().getPlayers());
                    item.remove();
                    cancel();
                }
            }
        }).runTaskTimer(SkyWars.getInstance(), 2L, 1L);
    }
}
