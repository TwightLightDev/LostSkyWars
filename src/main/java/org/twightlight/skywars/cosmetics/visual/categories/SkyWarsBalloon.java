package org.twightlight.skywars.cosmetics.visual.categories;

import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.PreviewableCosmetic;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.cosmetics.visual.assets.balloons.Balloon;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.integration.packetevents.PacketEventsIntegration;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SkyWarsBalloon extends PreviewableCosmetic {

    private String name;
    private boolean buyable;
    private String permission;
    private int coins;
    private List<String> textures;
    private ItemStack icon;
    private boolean canBeFoundInBox;

    public SkyWarsBalloon(int id, String name, String permission, ItemStack icon, CosmeticRarity rarity, boolean buyable, boolean canBeFoundInBox, int coins, List<String> textures) {
        super(id, VisualCosmeticType.BALLOON, rarity);
        this.name = name;
        this.buyable = buyable;
        this.permission = permission;
        this.coins = coins;
        this.textures = textures;
        this.icon = icon;
        this.canBeFoundInBox = canBeFoundInBox;
    }
    @Override
    public void preview(Player player, Object... objects) {
        Location location = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("preview-location.balloons"));
        XMaterial xMaterial = XMaterial.OAK_FENCE;
        MaterialData matdata = xMaterial.parseItem().getData();

        int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                new Vector3i((int) location.getX(),
                        (int) location.getY(),
                        (int) location.getZ()), id);

        PacketEventsIntegration.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);

        Balloon preview = new Balloon(location,  this, Collections.singletonList(player.getUniqueId()), true);

        sessionUUID.get(player.getUniqueId()).addEndConsumers((player1) -> {

            preview.despawn();

            XMaterial xMaterial1 = XMaterial.AIR;
            MaterialData matdata1 = xMaterial1.parseItem().getData();

            int id1 = SpigotConversionUtil.fromBukkitMaterialData(matdata1).getGlobalId();

            WrapperPlayServerBlockChange packet1 = new WrapperPlayServerBlockChange(
                    new Vector3i((int) location.getX(),
                            (int) location.getY(),
                            (int) location.getZ()), id1);

            PacketEventsIntegration.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet1);
        });
    }

    public boolean isValid() {
        return !this.getTextures().isEmpty();
    }

    public boolean canBeSold() {
        return buyable;
    }

    public List<String> getTextures() {
        return textures;
    }

    @Override
    public boolean canBeFoundInBox(Player player) {
        if (this.has(Database.getInstance().getAccount(player.getUniqueId())))
            return false;
        return canBeFoundInBox;
    }

    public boolean isPermissible() {
        return !this.permission.isEmpty() && !this.permission.equals("none");
    }

    public boolean hasByPermission(Player player) {
        return !isPermissible() || player.hasPermission(this.permission);
    }

    @Override
    public boolean has(Account account) {
        if (isPermissible()) {
            return this.has(account, this.getVisualType()) || this.hasByPermission(account.getPlayer());
        }
        return account.getCosmeticHelper().hasCosmetic(this.getVisualType(), this.getId());
    }

    private boolean has(Account account, VisualCosmeticType type) {
        return account.getCosmeticHelper().hasCosmetic(type, this.getId());
    }

    @Override
    public String getName() {
        return Language.options$cosmetic$ballon + this.name;
    }

    public String getRawName() {
        return this.name;
    }

    @Override
    public ItemStack getIcon() {
        return this.getIcon("\u00a7a");
    }

    public ItemStack getIcon(String colorDisplay, String... lores) {
        ItemStack cloned = this.icon.clone();
        ItemMeta meta = cloned.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        meta.setDisplayName(colorDisplay + meta.getDisplayName());
        List<String> list = new ArrayList<>();
        if (meta.getLore() != null) {
            list.addAll(meta.getLore());
        }
        list.addAll(Arrays.asList(lores));
        meta.setLore(list);
        cloned.setItemMeta(meta);
        return cloned;
    }

    public int getCoins() {
        return coins;
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Ballons");
    protected static final YamlWrapper CONFIG = YamlWrapper.getConfig( "balloons", "plugins/LostSkyWars/cosmetics");

    public static void setupBallons() {
        CONFIG.reload();
        for (String key : CONFIG.getKeys(false)) {
            ConfigurationSection sec = CONFIG.getSection(key);
            int id = sec.getInt("id");
            String name = sec.getString("name");
            String permission = sec.getString("permission");
            ItemStack icon = BukkitUtils.deserializeItemStack(sec.getString("icon"));
            CosmeticRarity rarity = CosmeticRarity.fromName(sec.getString("rarity"));
            boolean buyable = sec.getBoolean("buyable");
            boolean canBeFoundInBox = sec.getBoolean("canBeFoundInBox", true);

            int price = sec.getInt("price");
            List<String> textures = sec.getStringList("textures");

            SkyWarsBalloon balloon = new SkyWarsBalloon(id, name, permission, icon, rarity, buyable, canBeFoundInBox, price, textures);
            if (!balloon.isValid()) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> LOGGER.log(Level.WARNING, "Invalid FrameList on Balloon \"" + key + "\""));
                continue;
            }

            VisualCosmetic.register(balloon);
        }
    }
}
