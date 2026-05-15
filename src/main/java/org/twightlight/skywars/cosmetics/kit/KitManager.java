package org.twightlight.skywars.cosmetics.kit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.group.CosmeticsGroup;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitManager {

    private static final List<Kit> KITS = new ArrayList<>();
    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Kits");

    public static void setupKits() {
        KITS.clear();
        ConfigWrapper cu = ConfigWrapper.getConfig("kits");

        for (String key : cu.getKeys(false)) {
            LOGGER.log(Logger.Level.INFO, "Loading kit: " + key + "...");

            int id = cu.getInt(key + ".id");
            String name = cu.getString(key + ".name");
            int price = cu.getInt(key + ".price");
            CosmeticRarity rarity = CosmeticRarity.fromName(cu.getString(key + ".rarity"));
            String permission = cu.getString(key + ".permission", "none");
            ItemStack icon = BukkitUtils.fullyDeserializeItemStack(cu.getString(key + ".icon", null));

            List<String> allowedGroups = new ArrayList<>();
            if (cu.contains(key + ".allowed-groups")) {
                allowedGroups = cu.getStringList(key + ".allowed-groups");
            } else {
                // Default: all CosmeticsGroup IDs
                for (CosmeticsGroup cg : CosmeticsGroup.listAll()) {
                    allowedGroups.add(cg.getId());
                }
                cu.set(key + ".allowed-groups", allowedGroups);
            }

            List<ItemStack> armorList = new ArrayList<>();
            for (String armorStr : cu.getStringList(key + ".armor")) {
                armorList.add(BukkitUtils.fullyDeserializeItemStack(armorStr));
            }

            List<PotionEffect> potionEffects = new ArrayList<>();
            if (cu.contains(key + ".potion-effects")) {
                for (String potion : cu.getStringList(key + ".potion-effects")) {
                    potionEffects.add(BukkitUtils.deserializePotionEffect(potion));
                }
            }

            ItemStack[] armor = armorList.toArray(new ItemStack[armorList.size()]);
            if (armor.length != 4) {
                armor = null;
                LOGGER.log(Logger.Level.WARNING, "Invalid armor list for kit \"" + name + "\"");
            }
            armorList.clear();

            List<ItemStack> contentList = new ArrayList<>();
            for (String contentStr : cu.getStringList(key + ".content")) {
                contentList.add(BukkitUtils.fullyDeserializeItemStack(contentStr));
            }

            ItemStack[] content = contentList.toArray(new ItemStack[contentList.size()]);
            contentList.clear();

            Kit kit = new Kit(id, name, rarity, permission, icon, price, armor, content, potionEffects, allowedGroups);
            KITS.add(kit);

            LOGGER.log(Logger.Level.INFO, "Kit loaded: " + name);
        }
    }

    public static Kit getById(int id) {
        for (Kit kit : KITS) {
            if (kit.getId() == id) {
                return kit;
            }
        }
        return null;
    }

    public static List<Kit> listAll() {
        return Collections.unmodifiableList(KITS);
    }

    public static List<Kit> listForGroup(String cosmeticsGroupId) {
        List<Kit> result = new ArrayList<>();
        for (Kit kit : KITS) {
            if (kit.isAllowed(cosmeticsGroupId)) {
                result.add(kit);
            }
        }
        return result;
    }
}
