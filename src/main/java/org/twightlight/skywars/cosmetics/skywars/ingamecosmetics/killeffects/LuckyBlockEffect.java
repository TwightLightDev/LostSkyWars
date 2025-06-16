package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.killeffects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.fastparticles.ParticleData;
import org.twightlight.libs.fastparticles.ParticleType;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.ItemBuilder;


public class LuckyBlockEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public LuckyBlockEffect() {
        super(CONFIG.getInt( "lucky-block.id"),
                CONFIG.getString("lucky-block.name"),
                CosmeticRarity.fromName(CONFIG.getString("lucky-block.rarity")),
                CONFIG.getBoolean("lucky-block.buyable", true),
                CONFIG.getString("lucky-block.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("lucky-block.icon")),
                CONFIG.getInt("lucky-block.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        final ArmorStand stand = (ArmorStand)victim.getLocation().getWorld().spawn(victim.getLocation().subtract(0.0D, 1.54D, 0.0D), ArmorStand.class);
        stand.setMetadata("cosmetic.entity", (MetadataValue)new FixedMetadataValue(Main.getInstance(), Boolean.valueOf(true)));
        stand.setMarker(true);
        ItemStack[] head = { (new ItemBuilder(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal())).setSkullOwnerNMS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGI5MmNiNDMzMzNhYTYyMWM3MGVlZjRlYmYyOTliYTQxMmI0NDZmZTEyZTM0MWNjYzU4MmYzMTkyMTg5In19fQ==").toItemStack() };
        stand.setHelmet(head[0]);
        stand.setVisible(false);
        stand.setGravity(false);
        Location loc = stand.getLocation();
        stand.teleport(loc);
        (new BukkitRunnable() {
            double y = 0.1D;

            float yaw = 16.0F;

            float pitch = 0.5F;

            public void run() {
                if (!killer.isOnline()) {
                    cancel();
                    return;
                }
                Location loc = stand.getLocation();
                loc.setYaw(loc.getYaw() + this.yaw);
                stand.teleport(loc.add(0.0D, this.y, 0.0D));
                this.y -= 0.00365D;
                this.yaw -= 0.4F;
                if (this.y <= 0.0D) {
                    stand.remove();
                    stand.getWorld().playSound(stand.getEyeLocation(), Sound.DIG_STONE, 1.0F, 1.0F);
                    ParticleData data = ParticleData.createBlockData(Material.REDSTONE_BLOCK, (byte)0);
                    ParticleType.of("BLOCK_CRACK").spawn(loc.getWorld(), stand.getEyeLocation(), 30 , 0.3F, 0.3F, 0.3F, 0.75F, data);
                    cancel();
                }
                if (stand.getTicksLived() % 4 == 0)
                    stand.getWorld().playSound(stand.getEyeLocation(), Sound.NOTE_PLING, 0.6F, this.pitch);
                this.pitch = (float)(this.pitch + 0.05D);
            }
        }).runTaskTimer(Main.getInstance(),0L, 1L);
    }
}
