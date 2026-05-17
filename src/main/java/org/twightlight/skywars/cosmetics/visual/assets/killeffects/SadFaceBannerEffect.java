package org.twightlight.skywars.cosmetics.visual.assets.killeffects;

import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsKillEffect;
import org.twightlight.skywars.integration.packetevents.PacketEventsIntegration;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;

import java.util.ArrayList;
import java.util.List;

public class SadFaceBannerEffect extends SkyWarsKillEffect {

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

    @Override
    public void killEffectPreview(Player player, Location location) {
        Location loc = location.clone();
        XMaterial xMaterial = XMaterial.BARRIER;
        MaterialData matdata = xMaterial.parseItem().getData();

        int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                new Vector3i((int) location.getX(),
                        (int) location.getY()-1,
                        (int) location.getZ()), id);


        PacketEventsIntegration.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);

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
                location.clone().add(0, -1, 0).getBlock().setType(Material.AIR);

            }
        }).runTaskLater(SkyWars.getInstance(), 100L);
        block.setMetadata("cosmetic.block", (MetadataValue)new FixedMetadataValue(SkyWars.getInstance(), Boolean.valueOf(true)));
        sessionUUID.get(player.getUniqueId()).addEndConsumers((p) -> {
            block.setType(Material.AIR);
            location.clone().add(0, -1, 0).getBlock().setType(Material.AIR);
        });
    }
}
