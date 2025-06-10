package tk.kanaostore.losteddev.skywars.cosmetics.skywars.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsKit;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

import java.util.ArrayList;
import java.util.List;

public class NormalSkyWarsKit extends SkyWarsKit {

    private ItemStack[] armor;
    private ItemStack[] content;
    private List<PotionEffect> potionEffects;

    public NormalSkyWarsKit(int id, String name, CosmeticRarity rarity, String permission, ItemStack icon, int coins, ItemStack[] armor, ItemStack[] content, List<PotionEffect> potions) {
        super(id, name, rarity, permission, icon, coins);
        this.armor = armor;
        this.content = content;
        this.potionEffects = potions;

    }

    @Override
    public void apply(Player player) {
        player.getInventory().setArmorContents(this.armor);
        player.getInventory().addItem(this.content);
        for (PotionEffect potionEffect : potionEffects) {
            potionEffect.apply(player);
        }
    }

    @Override
    public ItemStack[] getContents() {
        return content;
    }

    public static final LostLogger LOGGER = Main.LOGGER.getModule("NormalKits");

    public static void setupKits() {
        ConfigUtils cu = ConfigUtils.getConfig("normalkits", "plugins/LostSkyWars/kits");
        for (String key : cu.getKeys(false)) {
            int id = cu.getInt(key + ".id");
            String name = cu.getString(key + ".name");
            int price = cu.getInt(key + ".price");
            CosmeticRarity rarity = CosmeticRarity.fromName(cu.getString(key + ".rarity"));
            String permission = cu.getString(key + ".permission");
            ItemStack icon = BukkitUtils.fullyDeserializeItemStack(cu.getString(key + ".icon", null));
            List<ItemStack> list = new ArrayList<>();
            for (String armor : cu.getStringList(key + ".armor")) {
                list.add(BukkitUtils.fullyDeserializeItemStack(armor));
            }
            List<PotionEffect> potionEffects = new ArrayList<>();
            if (cu.contains(key + ".potion-effects")) {
                for (String potion : cu.getStringList(key + ".potion-effects")) {
                    potionEffects.add(BukkitUtils.deserializePotionEffect(potion));
                }
            }

            ItemStack[] armor = list.toArray(new ItemStack[list.size()]);
            if (armor.length != 4) {
                armor = null;
                LOGGER.log(LostLevel.WARNING, "Invalid armor list for kit \"" + name + "\"");
            }
            list.clear();
            for (String content : cu.getStringList(key + ".content")) {
                list.add(BukkitUtils.fullyDeserializeItemStack(content));
            }

            ItemStack[] content = list.toArray(new ItemStack[list.size()]);
            list.clear();
            list = null;

            CosmeticServer.SKYWARS.addCosmetic(new NormalSkyWarsKit(id, name, rarity, permission, icon, price, armor, content, potionEffects));
        }
    }

    @Override
    public int getMode() {
        return 1;
    }
}
