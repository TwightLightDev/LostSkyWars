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
import org.twightlight.skywars.cosmetics.group.CosmeticsGroup;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.BukkitUtils;
import org.twightlight.skywars.config.ConfigUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkyWarsKit extends Cosmetic {

    private String name;
    private String permission;
    private ItemStack icon;
    private int coins;
    private ItemStack[] armor;
    private ItemStack[] content;
    private List<PotionEffect> potionEffects;
    private List<String> allowedGroups;  //This should be CosmeticGroup id, not ArenaGroup id

    public SkyWarsKit(int id, String name, CosmeticRarity rarity, String permission, ItemStack icon, int coins,
                      ItemStack[] armor, ItemStack[] content, List<PotionEffect> potions, List<String> allowedGroups) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KIT, rarity);
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.coins = coins;
        this.armor = armor;
        this.content = content;
        this.potionEffects = potions != null ? potions : new ArrayList<>();
        this.allowedGroups = allowedGroups != null ? allowedGroups : new ArrayList<>();
    }

    public boolean canBeSold() {
        return coins > 0;
    }

    public List<String> getAllowedGroups() {
        return allowedGroups;
    }

    public boolean isAllowedInGroup(CosmeticsGroup group) {
        if (group == null) return false;
        if (allowedGroups.isEmpty()) return true;
        return allowedGroups.contains(group.getId());
    }

    public boolean isAllowedInGroup(String groupId) {
        if (allowedGroups.isEmpty()) return true;
        return allowedGroups.contains(groupId);
    }

    @Override
    public boolean has(Account account, int mode) {
        ArenaGroup group = GroupManager.get(allowedGroups.isEmpty() ? "solo" : allowedGroups.get(0));
        if (group != null && group.hasTrait("free_kits_and_perks")) {
            return true;
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
        return !isPermissible() || hasByPermission(player);
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
        return this.getIcon("\u00a7a");
    }

    public ItemStack getIcon(String colorDisplay, String... lores) {
        ItemStack cloned = icon.clone();
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

    public void apply(Player player) {
        if (this.armor != null) {
            player.getInventory().setArmorContents(this.armor);
        }
        if (this.content != null) {
            player.getInventory().addItem(this.content);
        }
        for (PotionEffect potionEffect : potionEffects) {
            potionEffect.apply(player);
        }
    }

    public ItemStack[] getContents() {
        return content;
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Kits");

    public static void setupKits() {
        ConfigUtils cu = ConfigUtils.getConfig("kits");

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
                for (ArenaGroup group : GroupManager.getGroups()) {
                    allowedGroups.add(group.getId());
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

            CosmeticServer.SKYWARS.addCosmetic(new SkyWarsKit(id, name, rarity, permission, icon, price, armor, content, potionEffects, allowedGroups));

            LOGGER.log(Logger.Level.INFO, "Kit loaded: " + name);
        }
    }
}
