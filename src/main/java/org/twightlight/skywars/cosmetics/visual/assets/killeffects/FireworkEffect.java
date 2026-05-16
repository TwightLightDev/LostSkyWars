package org.twightlight.skywars.cosmetics.visual.assets.killeffects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsKillEffect;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

import java.util.Random;


public class FireworkEffect extends SkyWarsKillEffect {

    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("killeffects");

    public FireworkEffect() {
        super(CONFIG.getInt("firework.id"),
                CONFIG.getString("firework.name"),
                CosmeticRarity.fromName(CONFIG.getString("firework.rarity")),
                CONFIG.getBoolean("firework.buyable", true),
                CONFIG.getString("firework.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("firework.icon")),
                CONFIG.getInt("firework.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        Firework fire = (Firework) victim.getWorld().spawnEntity(victim.getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = fire.getFireworkMeta();
        int random = new Random().nextInt(5) + 1;
        Color color = random == 1 ? Color.BLUE : random == 2 ? Color.RED : random == 3 ? Color.GREEN : random == 4 ? Color.MAROON : Color.ORANGE;
        meta.addEffect(org.bukkit.FireworkEffect.builder().withColor(color).with(org.bukkit.FireworkEffect.Type.STAR).build());
        meta.setPower(1);
        fire.setFireworkMeta(meta);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {
        Firework fire = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = fire.getFireworkMeta();
        int random = new Random().nextInt(5) + 1;
        Color color = random == 1 ? Color.BLUE : random == 2 ? Color.RED : random == 3 ? Color.GREEN : random == 4 ? Color.MAROON : Color.ORANGE;
        meta.addEffect(org.bukkit.FireworkEffect.builder().withColor(color).with(org.bukkit.FireworkEffect.Type.STAR).build());
        meta.setPower(1);
        fire.setFireworkMeta(meta);
    }
}
