package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Main;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.LostLogger;
import org.twightlight.skywars.utils.RenderUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkyWarsSpray extends Cosmetic {

    private String name;
    private boolean buyable;
    private String permission;
    private int coins;
    private ItemStack icon;
    private BufferedImage img;

    public SkyWarsSpray(int id, String name, String permission, ItemStack icon, CosmeticRarity rarity,
                        boolean buyable, int coins, BufferedImage img) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_SPRAY, rarity);
        this.name = name;
        this.buyable = buyable;
        this.permission = permission;
        this.coins = coins;
        this.icon = icon;
        this.img = img;
    }


    public boolean canBeSold() {
        return buyable;
    }

    public BufferedImage getImage() {
        return img;
    }

    @Override
    public boolean canBeFoundInBox(Player player) {
        return !isPermissible() || !this.hasByPermission(player);
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
            return this.has(account, this.getMode()) || this.hasByPermission(account.getPlayer());
        }
        return this.has(account, this.getMode());    }

    @Override
    public String getName() {
        return Language.options$cosmetic$spray + this.name;
    }

    public String getRawName() {
        return this.name;
    }

    @Override
    public ItemStack getIcon() {
        return this.getIcon("§a");
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

    public static final LostLogger LOGGER = Main.LOGGER.getModule("Sprays");
    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("sprays");

    public static void setupSprays() {
        for (String key : CONFIG.getKeys(false)) {
            ConfigurationSection sec = CONFIG.getSection(key);
            int id = sec.getInt("id");
            String name = sec.getString("name");
            String permission = sec.getString("permission");
            ItemStack icon = BukkitUtils.deserializeItemStack(sec.getString("icon"));
            CosmeticRarity rarity = CosmeticRarity.fromName(sec.getString("rarity"));
            boolean buyable = sec.getBoolean("buyable");
            int price = sec.getInt("price");
            String url = sec.getString("url", null);
            BufferedImage img = null;
            if (url != null) {
                try {
                    img = RenderUtils.loadImage(url);
                } catch (IOException e) {
                    throw new RuntimeException("§cFailed to load image: " + e.getMessage());
                }
            }
            String fileStr = sec.getString("file", null);
            if (fileStr != null) {
                try {
                    File imageFile = new File(Main.getInstance().getDataFolder().getPath() + "/sprays/" + fileStr);
                    img = RenderUtils.loadImage(imageFile);
                } catch (IOException e) {
                    throw new RuntimeException("§cFailed to load image: " + e.getMessage());
                }
            }
            SkyWarsSpray spray = new SkyWarsSpray(id, name, permission, icon, rarity, buyable, price, img);

            CosmeticServer.SKYWARS.addCosmetic(spray);
        }
    }
}

