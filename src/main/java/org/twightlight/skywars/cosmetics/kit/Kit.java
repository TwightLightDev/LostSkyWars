package org.twightlight.skywars.cosmetics.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.group.CosmeticsGroup;
import org.twightlight.skywars.player.Account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kit {

    private int id;
    private String name;
    private CosmeticRarity rarity;
    private String permission;
    private ItemStack icon;
    private int coins;
    private ItemStack[] armor;
    private ItemStack[] content;
    private List<PotionEffect> potionEffects;
    private List<String> allowedGroups;

    public Kit(int id, String name, CosmeticRarity rarity, String permission, ItemStack icon, int coins,
               ItemStack[] armor, ItemStack[] content, List<PotionEffect> potions, List<String> allowedGroups) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.permission = permission;
        this.icon = icon;
        this.coins = coins;
        this.armor = armor;
        this.content = content;
        this.potionEffects = potions != null ? potions : new ArrayList<>();
        this.allowedGroups = allowedGroups != null ? allowedGroups : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return Language.options$cosmetic$kit + this.name;
    }

    public String getRawName() {
        return this.name;
    }

    public CosmeticRarity getRarity() {
        return rarity;
    }

    public String getPermission() {
        return permission;
    }

    public int getCoins() {
        return coins;
    }

    public boolean canBeSold() {
        return coins > 0;
    }

    public List<String> getAllowedGroups() {
        return allowedGroups;
    }

    public boolean isAllowed(CosmeticsGroup group) {
        if (group == null) return false;
        if (allowedGroups.isEmpty()) return true;
        return allowedGroups.contains(group.getId());
    }

    public boolean isAllowed(String groupId) {
        if (allowedGroups.isEmpty()) return true;
        return allowedGroups.contains(groupId);
    }

    public boolean has(Account account, String cosmeticsGroupId) {
        if (isPermissible() && hasByPermission(account.getPlayer())) {
            return true;
        }
        for (String groupId : allowedGroups) {
            ArenaGroup arenaGroup = GroupManager.get(groupId);
            if (arenaGroup != null && arenaGroup.hasTrait("free_kits_and_perks")) {
                return true;
            }
        }
        return account.getCosmeticHelper().hasKit(cosmeticsGroupId, id);
    }

    public void give(Account account, String cosmeticsGroupId) {
        account.getCosmeticHelper().addKit(cosmeticsGroupId, id);
    }

    public boolean isSelected(Account account, String cosmeticsGroupId) {
        return account.getSelectedContainer().getSelectedKit(cosmeticsGroupId) == this.id;
    }

    public boolean isPermissible() {
        return this.permission != null && !this.permission.isEmpty() && !this.permission.equals("none");
    }

    public boolean hasByPermission(Player player) {
        return player != null && isPermissible() && player.hasPermission(this.permission);
    }

    public boolean canBeFoundInBox(Player player) {
        return !isPermissible() || hasByPermission(player);
    }

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
}
