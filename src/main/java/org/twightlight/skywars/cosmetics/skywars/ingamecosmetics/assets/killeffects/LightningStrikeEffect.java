package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.killeffects;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsKillEffect;
import org.twightlight.skywars.hook.PacketEventsHook;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

import java.util.UUID;


public class LightningStrikeEffect extends SkyWarsKillEffect {

    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killeffects");

    public LightningStrikeEffect() {
        super(CONFIG.getInt("lightning-strike.id"),
                CONFIG.getString("lightning-strike.name"),
                CosmeticRarity.fromName(CONFIG.getString("lightning-strike.rarity")),
                CONFIG.getBoolean("lightning-strike.buyable", true),
                CONFIG.getString("lightning-strike.permission"),
                BukkitUtils.fullyDeserializeItemStack(CONFIG.getString("lightning-strike.icon")),
                CONFIG.getInt("lightning-strike.price"));
    }

    public void execute(Player killer, Player victim, Location location) {
        location.getWorld().strikeLightningEffect(location);
    }

    @Override
    public void killEffectPreview(Player player, Location location) {

        location.getWorld().strikeLightningEffect(location);

    }
}
