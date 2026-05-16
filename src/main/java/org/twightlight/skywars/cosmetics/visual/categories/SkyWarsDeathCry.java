package org.twightlight.skywars.cosmetics.visual.categories;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.nms.enums.Sound;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkyWarsDeathCry extends VisualCosmetic {

    private String name;
    private boolean buyable;
    private int coins;
    private String permission;
    private Sound sound;
    private float volume;
    private float pitch;
    private ItemStack icon;
    private boolean canBeFoundInBox;

    public SkyWarsDeathCry(int id, String name, String permission, ItemStack icon, CosmeticRarity rarity, boolean buyable, boolean canBeFoundInBox, int coins, String sound, float volume, float pitch) {
        super(id, VisualCosmeticType.DEATH_CRY, rarity);
        this.name = name;
        this.buyable = buyable;
        this.permission = permission;
        this.coins = coins;
        this.sound = Sound.valueOf(sound);
        this.volume = volume;
        this.pitch = pitch;
        this.icon = icon;
        this.canBeFoundInBox = canBeFoundInBox;
    }

    public boolean isValid() {
        return sound != null;
    }

    public boolean canBeSold() {
        return buyable;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
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
        return Language.options$cosmetic$deathcry + this.name;
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

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("DeathCries");
    private static final ConfigWrapper CONFIG = ConfigWrapper.getConfig("deathcries");

    public static void setupDeathCries() {
        CONFIG.reload();
        for (String key : CONFIG.getKeys(false)) {
            ConfigurationSection sec = CONFIG.getSection(key);
            int id = sec.getInt("id");
            String name = sec.getString("name");
            ItemStack icon = BukkitUtils.deserializeItemStack(sec.getString("icon"));
            CosmeticRarity rarity = CosmeticRarity.fromName(sec.getString("rarity"));
            boolean buyable = sec.getBoolean("buyable");
            boolean canBeFoundInBox = sec.getBoolean("canBeFoundInBox", true);
            int price = sec.getInt("price");
            String sound = sec.getString("sound").toUpperCase();
            String permission = sec.getString("permission");
            float volume = (float) sec.getDouble("volume");
            float pitch = (float) sec.getDouble("pitch");

            SkyWarsDeathCry cry = new SkyWarsDeathCry(id, name, permission, icon, rarity, buyable, canBeFoundInBox, price, sound, volume, pitch);
            if (!cry.isValid()) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(),
                        () -> LOGGER.log(Level.WARNING, "Invalid Sound \"" + sound + "\" on DeathCry \"" + key + "\""));
                continue;
            }

            VisualCosmetic.register(cry);
        }
    }

    public static void createNew(Object[] arr) {
        int id = 1;
        String key = (String) arr[1];
        String sound = ((Sound) arr[2]).name();
        float volume = (float) arr[3];
        float pitch = (float) arr[4];
        int price = (int) arr[5];
        CosmeticRarity rarity = (CosmeticRarity) arr[6];
        boolean buyable = (boolean) arr[7];
        CONFIG.createSection(key);
        ConfigurationSection sec = CONFIG.getSection(key);

        VisualCosmetic c = VisualCosmetic.listByType(VisualCosmeticType.DEATH_CRY).stream().filter(cosmetic -> cosmetic.getId() == 1).findAny().orElse(null);
        while (c != null) {
            id++;
            int copyId = id;
            c = VisualCosmetic.listByType(VisualCosmeticType.DEATH_CRY).stream().filter(cosmetic -> cosmetic.getId() == copyId).findAny().orElse(null);
        }
        sec.set("id", id);
        sec.set("name", (String) arr[0]);
        sec.set("price", price);
        sec.set("rarity", rarity.name());
        sec.set("buyable", buyable);
        sec.set("canBeFoundInBox", true);
        sec.set("sound", sound);
        sec.set("permission", "none");
        sec.set("volume", volume);
        sec.set("pitch", pitch);
        sec.set("icon", "BARRIER : 1 : display=" + arr[0] + " : lore=&7Change that on deathcries.yml\\n ");
        CONFIG.save();

        VisualCosmetic.register(new SkyWarsDeathCry(id, (String) arr[0], "none",
                BukkitUtils.deserializeItemStack("BARRIER : 1 : display=" + arr[0] + " : lore=&7Change that on deathcries.yml\\n "), rarity, buyable, true, price, sound, volume, pitch));
    }
}
