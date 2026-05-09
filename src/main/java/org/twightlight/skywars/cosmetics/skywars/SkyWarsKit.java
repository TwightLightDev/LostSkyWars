package org.twightlight.skywars.cosmetics.skywars;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.kits.InsaneSkyWarsKit;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkyWarsKit extends Cosmetic {

    private String name;
    private String permission;
    private ItemStack icon;
    private int coins;

    private ItemStack[] armor;
    private ItemStack[] content;
    private List<PotionEffect> potionEffects;




    public SkyWarsKit(int id, String name, CosmeticRarity rarity, String permission, ItemStack icon, int coins, ItemStack[] armor, ItemStack[] content, List<PotionEffect> potions) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KIT, rarity);
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.coins = coins;
        this.armor = armor;
        this.content = content;
        this.potionEffects = potions;
    }


    public boolean canBeSold() {
        return coins > 0;
    }

    @Override
    public boolean has(Account account, int mode) {
        if (Language.options$ranked$freekitsandperks) {
            if (mode == 3) {
                return true;
            }
        }

        return super.has(account, mode);
    }

    @Override
    public boolean has(Account account) {
        if (isPermissible()) {
            return this.has(account, this.getMode()) || this.hasByPermission(account.getPlayer());
        }
        return this.has(account, this.getMode());
    }

    @Override
    public boolean canBeFoundInBox(Player player) {
        return Language.options$ranked$freekitsandperks ? this.getMode() != 3 && (!isPermissible() || hasByPermission(player)) : (!isPermissible() || hasByPermission(player));
    }

    public boolean isPermissible() {
        return !this.permission.isEmpty() && !this.permission.equals("none");
    }

    public boolean hasByPermission(Player player) {
        return !isPermissible() || player.hasPermission(this.permission);
    }

    @Override
    public String getName() {
        return Language.options$cosmetic$kit + this.name;
    }

    public String getRawName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    @Override
    public ItemStack getIcon() {
        return this.getIcon("§a");
    }

    public ItemStack getIcon(String colorDisplay, String... lores) {
        ItemStack cloned = icon.clone();
        ItemMeta meta = cloned.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        meta.setDisplayName(colorDisplay + meta.getDisplayName());
        List<String> list = new ArrayList<>();
        list.addAll(meta.getLore());
        list.addAll(Arrays.asList(lores));
        meta.setLore(list);
        cloned.setItemMeta(meta);
        return cloned;
    }

    public int getCoins() {
        return coins;
    }

    public void apply(Player player) {
        player.getInventory().setArmorContents(this.armor);
        player.getInventory().addItem(this.content);
        for (PotionEffect potionEffect : potionEffects) {
            potionEffect.apply(player);
        }
    }

    public ItemStack[] getContents() {
        return content;
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Kits");

    public static void setupKits() {

        for (String group : GroupManager.getGroupIds()) {
            ConfigUtils cu = ConfigUtils.getConfig(group+"_kits", "plugins/LostSkyWars/kits");
            for (String key : cu.getKeys(false)) {
                LOGGER.log(Logger.Level.INFO, "Loading kit: " + key);

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
                    LOGGER.log(Logger.Level.WARNING, "Invalid armor list for kit \"" + name + "\"");
                }
                list.clear();
                for (String content : cu.getStringList(key + ".content")) {
                    list.add(BukkitUtils.fullyDeserializeItemStack(content));
                }

                ItemStack[] content = list.toArray(new ItemStack[list.size()]);
                list.clear();
                list = null;

                CosmeticServer.SKYWARS.addCosmetic(new InsaneSkyWarsKit(id, name, rarity, permission, icon, price, armor, content, potionEffects));

                LOGGER.log(Logger.Level.INFO, "Kit loaded: " + name);
            }
        }
    }
}
