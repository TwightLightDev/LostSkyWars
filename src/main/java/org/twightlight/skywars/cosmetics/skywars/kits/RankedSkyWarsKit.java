package org.twightlight.skywars.cosmetics.skywars.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.skywars.SkyWarsKit;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.Logger.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RankedSkyWarsKit extends SkyWarsKit {

    private ItemStack[] armor;
    private ItemStack[] content;
    private List<PotionEffect> potionEffects;

    public RankedSkyWarsKit(int id, String name, CosmeticRarity rarity, String permission, ItemStack icon, int coins, ItemStack[] armor, ItemStack[] content, List<PotionEffect> potions) {
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

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("RankedKits");

    public static void setupKits() {
        ConfigUtils cu = ConfigUtils.getConfig("rankedkits", "plugins/LostSkyWars/kits");
        Set<String> keys = cu.getKeys(false);

        for (String key : keys) {
            LOGGER.log(Level.INFO, "Loading kit: " + key);

            int id = cu.getInt(key + ".id");
            String name = cu.getString(key + ".name");
            int price = cu.getInt(key + ".price");
            CosmeticRarity rarity = CosmeticRarity.fromName(cu.getString(key + ".rarity"));
            String permission = cu.getString(key + ".permission");
            ItemStack icon = BukkitUtils.fullyDeserializeItemStack(cu.getString(key + ".icon", null));

            if (icon == null) {
                LOGGER.log(Level.WARNING, "Invalid icon for kit \"" + name + "\"");
                continue;
            }

            List<ItemStack> list = new ArrayList<>();
            for (String armorStr : cu.getStringList(key + ".armor")) {
                ItemStack armorItem = BukkitUtils.fullyDeserializeItemStack(armorStr);
                if (armorItem != null) {
                    list.add(armorItem);
                } else {
                    LOGGER.log(Level.WARNING, "Invalid armor item \"" + armorStr + "\" for kit \"" + name + "\"");
                }
            }

            List<PotionEffect> potionEffects = new ArrayList<>();
            if (cu.contains(key + ".potion-effects")) {
                for (String potion : cu.getStringList(key + ".potion-effects")) {
                    potionEffects.add(BukkitUtils.deserializePotionEffect(potion));
                }
            }

            ItemStack[] armor = list.toArray(new ItemStack[0]);
            if (armor.length != 4) {
                armor = null;
                LOGGER.log(Level.WARNING, "Invalid armor list for kit \"" + name + "\"");
            }

            list.clear();
            for (String contentStr : cu.getStringList(key + ".content")) {
                ItemStack contentItem = BukkitUtils.fullyDeserializeItemStack(contentStr);
                if (contentItem != null) {
                    list.add(contentItem);
                } else {
                    LOGGER.log(Level.WARNING, "Invalid content item \"" + contentStr + "\" for kit \"" + name + "\"");
                }
            }

            ItemStack[] content = list.toArray(new ItemStack[0]);

            CosmeticServer.SKYWARS.addCosmetic(new RankedSkyWarsKit(id, name, rarity, permission, icon, price, armor, content, potionEffects));
            LOGGER.log(Level.INFO, "Kit loaded: " + name);
        }
    }

    @Override
    public int getMode() {
        return 3;
    }
}
