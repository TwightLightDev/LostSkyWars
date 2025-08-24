package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.SkyWarsKillEffect;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;

public class SadFaceBannerEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public SadFaceBannerEffect() {
        super(CONFIG.getInt("sadface.id"),
                CONFIG.getString("sadface.name"),
                CosmeticRarity.fromName(CONFIG.getString("sadface.rarity")),
                CONFIG.getBoolean("sadface.buyable", true),
                CONFIG.getString("sadface.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("sadface.icon")),
                CONFIG.getInt("sadface.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        Location loc = getBlockUnder(victim.getLocation());
        if (loc == null)
            return;
        loc.add(0.0D, 1.0D, 0.0D);
        final Block block = loc.getBlock();
        Material material = Material.STANDING_BANNER;
        block.setType(material);
        Banner banner = (Banner)block.getState();
        banner.setBaseColor(DyeColor.BLACK);
        List<Pattern> patterns = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.SKULL));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL_MIRROR));
        banner.setPatterns(patterns);
        banner.update();
        (new BukkitRunnable() {
            public void run() {
                block.setType(Material.AIR);
            }
        }).runTaskLater(SkyWars.getInstance(), 300L);
        block.setMetadata("cosmetic.block", (MetadataValue)new FixedMetadataValue(SkyWars.getInstance(), Boolean.valueOf(true)));
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
