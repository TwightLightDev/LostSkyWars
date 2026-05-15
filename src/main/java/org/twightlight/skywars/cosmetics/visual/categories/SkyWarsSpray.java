package org.twightlight.skywars.cosmetics.visual.categories;

import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.PreviewableCosmetic;
import org.twightlight.skywars.cosmetics.VisualCosmetic;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.hook.PacketEventsHook;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;
import org.twightlight.skywars.utils.RenderUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SkyWarsSpray extends PreviewableCosmetic {

    private String name;
    private boolean buyable;
    private String permission;
    private int coins;
    private ItemStack icon;
    private BufferedImage img;
    private boolean canBeFoundInBox;

    public SkyWarsSpray(int id, String name, String permission, ItemStack icon, CosmeticRarity rarity,
                        boolean buyable, boolean canBeFoundInBox, int coins, BufferedImage img) {
        super(id, VisualCosmeticType.SPRAY, rarity);
        this.name = name;
        this.buyable = buyable;
        this.permission = permission;
        this.coins = coins;
        this.icon = icon;
        this.img = img;
        this.canBeFoundInBox = canBeFoundInBox;
    }

    @Override
    public void preview(Player player, Object... objects) {
        BufferedImage image = getImage();

        Location location = BukkitUtils.deserializeLocation(PREVIEWCONFIG.getString("preview-location.sprays"));

        XMaterial xMaterial = XMaterial.BARRIER;
        MaterialData matdata = xMaterial.parseItem().getData();
        int id = SpigotConversionUtil.fromBukkitMaterialData(matdata).getGlobalId();

        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
                new Vector3i((int) location.getX(), (int) location.getY(), (int) location.getZ()), id);
        PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet);

        int entityId = SpigotReflectionUtil.generateEntityId();
        NMS.getMapHelper().createMap(entityId, Collections.singletonList(player), location.getBlock().getRelative(getBlockFace(location.getYaw())).getLocation(), getBlockFace(location.getYaw()), image);

        sessionUUID.get(player.getUniqueId()).addEndConsumers((player1) -> {
            WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(entityId);
            PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, destroyPacket);

            XMaterial xMaterial1 = XMaterial.AIR;
            MaterialData matdata1 = xMaterial1.parseItem().getData();
            int id1 = SpigotConversionUtil.fromBukkitMaterialData(matdata1).getGlobalId();

            WrapperPlayServerBlockChange packet1 = new WrapperPlayServerBlockChange(
                    new Vector3i((int) location.getX(), (int) location.getY(), (int) location.getZ()), id1);
            PacketEventsHook.getPacketEventsAPI().getPlayerManager().sendPacket(player, packet1);
        });
    }

    public static BlockFace getBlockFace(float yaw) {
        yaw = (yaw % 360 + 360) % 360;
        if (yaw >= 45 && yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return BlockFace.NORTH;
        } else if (yaw >= 225 && yaw < 315) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }

    public boolean canBeSold() {
        return buyable;
    }

    public BufferedImage getImage() {
        return img;
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
            return account.getCosmeticHelper().hasCosmetic(this.getVisualType(), this.getId()) || this.hasByPermission(account.getPlayer());
        }
        return account.getCosmeticHelper().hasCosmetic(this.getVisualType(), this.getId());
    }

    @Override
    public String getName() {
        return Language.options$cosmetic$spray + this.name;
    }

    public String getRawName() {
        return this.name;
    }

    @Override
    public ItemStack getIcon() {
        return this.getIcon("a");
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

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Sprays");
    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("sprays");

    public static void setupSprays() {
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
            String url = sec.getString("url", null);
            BufferedImage img = null;
            if (url != null) {
                try {
                    img = RenderUtils.loadImage(url);
                } catch (IOException e) {
                    throw new RuntimeException("cFailed to load image: " + e.getMessage());
                }
            }
            String fileStr = sec.getString("file", null);
            if (fileStr != null) {
                try {
                    File imageFile = new File(SkyWars.getInstance().getDataFolder().getPath() + "/sprays/" + fileStr);
                    img = RenderUtils.loadImage(imageFile);
                } catch (IOException e) {
                    throw new RuntimeException("cFailed to load image: " + e.getMessage());
                }
            }
            SkyWarsSpray spray = new SkyWarsSpray(id, name, permission, icon, rarity, buyable, canBeFoundInBox, price, img);

            VisualCosmetic.register(spray);
        }
    }
}
