package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsKillEffect;
import org.twightlight.skywars.nms.particles.ParticleEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;
import org.twightlight.skywars.utils.ItemBuilder;


public class MysteryBoxEffect extends SkyWarsKillEffect {

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("killeffects");

    public MysteryBoxEffect() {
        super(CONFIG.getInt("mysterybox.id"),
                CONFIG.getString("mysterybox.name"),
                CosmeticRarity.fromName(CONFIG.getString("mysterybox.rarity")),
                CONFIG.getBoolean("mysterybox.buyable", true),
                CONFIG.getString("mysterybox.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("mysterybox.icon")),
                CONFIG.getInt("mysterybox.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        final ArmorStand stand = (ArmorStand)victim.getLocation().getWorld().spawn(victim.getLocation().subtract(0.0D, 1.54D, 0.0D), ArmorStand.class);
        stand.setMetadata("cosmetic.entity", (MetadataValue)new FixedMetadataValue(SkyWars.getInstance(), Boolean.valueOf(true)));
        ItemStack[] head = { (new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)).setSkullOwnerNMS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNkM2M0NWQ3YjgzODRlOGExOTYzZTRkYTBhZTZiMmRhZWIyYTNlOTdhYzdhMjhmOWViM2QzOTU5NzI1Nzk5ZiJ9fX0=").toItemStack() };
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
                    ParticleEffect.BLOCK_CRACK.display((ParticleEffect.ParticleData)new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte)0), 0.3F, 0.3F, 0.3F, 0.75F, 30, stand.getEyeLocation(), 120.0D);
                    cancel();
                }
                if (stand.getTicksLived() % 4 == 0)
                    stand.getWorld().playSound(stand.getEyeLocation(), Sound.NOTE_PLING, 0.6F, this.pitch);
                this.pitch = (float)(this.pitch + 0.05D);
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 1L);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {

        final ArmorStand stand = location.getWorld().spawn(location.subtract(0.0D, 1.54D, 0.0D), ArmorStand.class);
        stand.setMetadata("cosmetic.entity", (MetadataValue)new FixedMetadataValue(SkyWars.getInstance(), Boolean.valueOf(true)));
        ItemStack[] head = { (new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)).setSkullOwnerNMS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNkM2M0NWQ3YjgzODRlOGExOTYzZTRkYTBhZTZiMmRhZWIyYTNlOTdhYzdhMjhmOWViM2QzOTU5NzI1Nzk5ZiJ9fX0=").toItemStack() };
        stand.setHelmet(head[0]);
        stand.setVisible(false);
        stand.setGravity(false);
        Location loc = stand.getLocation();
        stand.teleport(loc);
        BukkitTask task = (new BukkitRunnable() {
            double y = 0.1D;

            float yaw = 16.0F;

            float pitch = 0.5F;

            public void run() {
                if (!player.isOnline()) {
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
                    ParticleEffect.BLOCK_CRACK.display((ParticleEffect.ParticleData)new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte)0), 0.3F, 0.3F, 0.3F, 0.75F, 30, stand.getEyeLocation(), player);
                    cancel();
                }
                if (stand.getTicksLived() % 4 == 0)
                    player.playSound(stand.getEyeLocation(), Sound.NOTE_PLING, 0.6F, this.pitch);
                this.pitch = (float)(this.pitch + 0.05D);
            }
        }).runTaskTimer(SkyWars.getInstance(), 0L, 1L);
        sessionUUID.get(player.getUniqueId()).addEndConsumers((p) -> {
            stand.remove();
            task.cancel();
        });
    }
}
