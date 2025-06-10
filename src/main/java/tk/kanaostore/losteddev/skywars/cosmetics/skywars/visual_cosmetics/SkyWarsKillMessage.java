package tk.kanaostore.losteddev.skywars.cosmetics.skywars.visual_cosmetics;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkyWarsKillMessage extends Cosmetic {

    private String name;
    private boolean buyable;
    private int coins;
    private ItemStack icon;
    private List<String> byMelee;
    private List<String> byVoid;
    private List<String> byBow;
    private List<String> byMob;

    public SkyWarsKillMessage(int id, String name, ItemStack icon, CosmeticRarity rarity,
                              boolean buyable, int coins, List<String> a, List<String> b, List<String> c, List<String> d) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KILLMESSAGE, rarity);
        this.name = name;
        this.buyable = buyable;
        this.coins = coins;
        this.icon = icon;
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
        return true;
    }

    @Override
    public String getName() {
        return Language.options$cosmetic$trail + this.name;
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

    public static final LostLogger LOGGER = Main.LOGGER.getModule("KillMessages");
    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("killmessages");

    public static void setupKM() {
        for (String key : CONFIG.getKeys(false)) {
            ConfigurationSection sec = CONFIG.getSection(key);
            int id = sec.getInt("id");
            String name = sec.getString("name");
            ItemStack icon = BukkitUtils.deserializeItemStack(sec.getString("icon"));
            CosmeticRarity rarity = CosmeticRarity.fromName(sec.getString("rarity"));
            boolean buyable = sec.getBoolean("buyable");
            int price = sec.getInt("price");
            List<String> a = sec.getStringList("messages.melee");
            List<String> b = sec.getStringList("messages.void");
            List<String> c = sec.getStringList("messages.bow");
            List<String> d = sec.getStringList("messages.mob");

            SkyWarsKillMessage killMessage = new SkyWarsKillMessage(id, name, icon, rarity, buyable, price, a, b, c, d);

            CosmeticServer.SKYWARS.addCosmetic(killMessage);
        }
    }
}

