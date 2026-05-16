package org.twightlight.skywars.cosmetics.visual.categories;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.config.ConfigWrapper;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkyWarsVictoryDance extends VisualCosmetic {

    private String name;
    private String permission;
    private ItemStack icon;
    private int coins;
    private boolean buyable;
    private boolean canBeFoundInBox;

    public SkyWarsVictoryDance(int id, String name, CosmeticRarity rarity, boolean buyable, String permission, ItemStack icon, int coins) {
        this(id, name, rarity, buyable, true, permission, icon, coins);
    }

    public SkyWarsVictoryDance(int id, String name, CosmeticRarity rarity, boolean buyable, boolean canBeFoundInBox, String permission, ItemStack icon, int coins) {
        super(id, VisualCosmeticType.VICTORY_DANCE, rarity);
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.coins = coins;
        this.buyable = buyable;
        this.canBeFoundInBox = canBeFoundInBox;
    }

    public abstract void execute(Player player);

    public boolean canBeSold() {
        return buyable;
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
    public String getName() {
        return Language.options$cosmetic$killeffect + this.name;
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

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("VictoryDance");
    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("victorydances");

    public static void setupVictoryDances() {
        CONFIG.reload();
    }


    private static void checkIfAbsent(String key) {
        if (CONFIG.contains(key)) {
            return;
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(SkyWars.getInstance().getResource("victorydances.yml"), "UTF-8"));
            for (String dataKey : config.getConfigurationSection(key).getKeys(false)) {
                CONFIG.set(key + "." + dataKey, config.get(key + "." + dataKey));
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
