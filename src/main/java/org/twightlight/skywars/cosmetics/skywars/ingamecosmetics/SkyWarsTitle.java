package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.nms.Sound;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.utils.Logger;
import org.twightlight.skywars.utils.Logger.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkyWarsTitle extends Cosmetic {

    private String name;
    private boolean buyable;
    private String permission;
    private int coins;
    private String title;
    private ItemStack icon;

    public SkyWarsTitle(int id, String name, String permission, ItemStack icon, CosmeticRarity rarity, boolean buyable, int coins, String title) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_TITLE, rarity);
        this.name = name;
        this.permission = permission;
        this.buyable = buyable;
        this.coins = coins;
        this.title = title;
        this.icon = icon;
    }



    public boolean canBeSold() {
        return buyable;
    }

    public String getTitle() {
        return title;
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
    public String getName() {
        return Language.options$cosmetic$title + this.name;
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

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Title");
    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("titles");

    public static void setupTitles() {
        for (String key : CONFIG.getKeys(false)) {
            ConfigurationSection sec = CONFIG.getSection(key);
            int id = sec.getInt("id");
            String name = sec.getString("name");
            ItemStack icon = BukkitUtils.deserializeItemStack(sec.getString("icon"));
            CosmeticRarity rarity = CosmeticRarity.fromName(sec.getString("rarity"));
            boolean buyable = sec.getBoolean("buyable");
            int price = sec.getInt("price");
            String title = sec.getString("title").toUpperCase();
            String perm = sec.getString("permission").toUpperCase();


            SkyWarsTitle title1 = new SkyWarsTitle(id, name, perm, icon, rarity, buyable, price, title);


            CosmeticServer.SKYWARS.addCosmetic(title1);
        }
    }


}
