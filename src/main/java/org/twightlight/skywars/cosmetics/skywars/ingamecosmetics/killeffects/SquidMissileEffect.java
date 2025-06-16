package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.killeffects;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.libs.xseries.XSound;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

public class SquidMissileEffect extends SkyWarsKillEffect {
    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public SquidMissileEffect() {
        super(CONFIG.getInt("squid-missile.id"),
                CONFIG.getString("squid-missile.name"),
                CosmeticRarity.fromName(CONFIG.getString("squid-missile.rarity")),
                CONFIG.getBoolean("squid-missile.buyable", true),
                CONFIG.getString("squid-missile.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("squid-missile.icon")),
                CONFIG.getInt("squid-missile.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        final ArmorStand stand = (ArmorStand)victim.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setVisible(false);
        final Squid squid = (Squid)location.getWorld().spawnEntity(location, EntityType.SQUID);
        stand.setPassenger((Entity)squid);
        (new BukkitRunnable() {
            int i1 = 0;

            public void run() {
                this.i1++;
                squid.getLocation().setYaw(180.0F);
                stand.eject();
                stand.teleport(stand.getLocation().add(0.0D, 0.5D, 0.0D));
                stand.setPassenger((Entity)squid);
                ParticleType.of("FLAME").spawn(stand.getWorld(), stand.getLocation(), 1, 0, 0, 0, 0);
                victim.playSound(victim.getLocation(), XSound.ENTITY_CHICKEN_EGG.get(), 1.0F, 1.0F);
                if (this.i1 == 25) {
                    ItemStack stackFirework = new ItemStack(Material.FIREWORK);
                    FireworkMeta fireworkMeta = (FireworkMeta)stackFirework.getItemMeta();
                    fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).trail(false).with(FireworkEffect.Type.BALL).withColor(Color.BLACK).withFade(Color.BLACK).build());
                    fireworkMeta.setPower(1);
                    stackFirework.setItemMeta((ItemMeta)fireworkMeta);
                    Firework fw = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                    fw.setFireworkMeta(fireworkMeta);
                    fw.detonate();
                    stand.eject();
                    stand.remove();
                    squid.remove();
                    this.i1 = 0;
                    cancel();
                }
            }
        }).runTaskTimer(Main.getInstance(), 4L, 1L);
    }
}
