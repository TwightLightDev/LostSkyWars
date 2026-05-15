package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsKillEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;
import org.twightlight.skywars.utils.StringUtils;


public class RektEffect extends SkyWarsKillEffect {

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("killeffects");

    public RektEffect() {
        super(CONFIG.getInt("rekt.id"),
                CONFIG.getString("rekt.name"),
                CosmeticRarity.fromName(CONFIG.getString("rekt.rarity")),
                CONFIG.getBoolean("rekt.buyable", true),
                CONFIG.getString("rekt.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("rekt.icon")),
                CONFIG.getInt("rekt.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        final ArmorStand stand = (ArmorStand)victim.getWorld().spawnEntity(victim.getEyeLocation(), EntityType.ARMOR_STAND);
        stand.setSmall(true);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(StringUtils.formatColors("&6" + killer.getDisplayName() + " &ehas #rekt &6" + victim.getDisplayName() + "&ehere"));
        (new BukkitRunnable() {
            public void run() {
                stand.remove();
            }
        }).runTaskLater(SkyWars.getInstance(), 200L);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {
        final ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setSmall(true);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(StringUtils.formatColors("&6" + player.getDisplayName() + " &ehas #rekt &6" + player.getDisplayName() + " &ehere"));
        (new BukkitRunnable() {
            public void run() {
                stand.remove();
            }
        }).runTaskLater(SkyWars.getInstance(), 100L);
    }
}
