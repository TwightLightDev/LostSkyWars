package org.twightlight.skywars.cosmetics.visual.categories;

import org.bukkit.configuration.ConfigurationSection;
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
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;
import org.twightlight.skywars.config.YamlWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkyWarsKillMessage extends VisualCosmetic {

    private String name;
    private boolean buyable;
    private String permission;
    private int coins;
    private ItemStack icon;
    private List<String> byMelee;
    private List<String> byVoid;
    private List<String> byBow;
    private List<String> byMob;
    private boolean canBeFoundInBox;

    public SkyWarsKillMessage(int id, String name, String permission, ItemStack icon, CosmeticRarity rarity,
                              boolean buyable, boolean canBeFoundInBox, int coins, List<String> a, List<String> b, List<String> c, List<String> d) {
        super(id, VisualCosmeticType.KILL_MESSAGE, rarity);
        this.name = name;
        this.buyable = buyable;
        this.permission = permission;
        this.coins = coins;
        this.icon = icon;
        this.canBeFoundInBox = canBeFoundInBox;
        byMelee = a;
        byVoid = b;
        byBow = c;
        byMob = d;
    }

    public boolean canBeSold() {
        return buyable;
    }

    public List<String> getMeleeMessage() {
        return byMelee;
    }

    public List<String> getVoidMessage() {
        return byVoid;
    }

    public List<String> getBowMessage() {
        return byBow;
    }

    public List<String> getMobMessage() {
        return byMob;
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

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("KillMessages");
    protected static final YamlWrapper CONFIG = YamlWrapper.getConfig("killmessages", "plugins/LostSkyWars/cosmetics");

    public static void setupKM() {
        CONFIG.reload();
        for (String key : CONFIG.getKeys(false)) {
            try {
                ConfigurationSection sec = CONFIG.getSection(key);
                int id = sec.getInt("id");
                String name = sec.getString("name");
                String permission = sec.getString("permission");
                ItemStack icon = BukkitUtils.deserializeItemStack(sec.getString("icon"));
                CosmeticRarity rarity = CosmeticRarity.fromName(sec.getString("rarity"));
                boolean buyable = sec.getBoolean("buyable");
                boolean canBeFoundInBox = sec.getBoolean("canBeFoundInBox", true);

                int price = sec.getInt("price");
                List<String> a = sec.getStringList("messages.melee");
                List<String> b = sec.getStringList("messages.void");
                List<String> c = sec.getStringList("messages.bow");
                List<String> d = sec.getStringList("messages.mob");

                SkyWarsKillMessage killMessage = new SkyWarsKillMessage(id, name, permission, icon, rarity, buyable, canBeFoundInBox, price, a, b, c, d);

                VisualCosmetic.register(killMessage);
            } catch (NullPointerException e) {
                System.err.println("Cannot load kill message " + key);
            }
        }
    }
}
