package org.twightlight.skywars.cosmetics.perk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.group.CosmeticsGroup;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Perk implements Listener {

    private int id;
    private String name;
    private CosmeticRarity rarity;
    private boolean buyable;
    private String permission;
    private ItemStack icon;
    private int coins;
    private List<String> allowedGroups;

    public Perk(int id, String name, CosmeticRarity rarity, boolean buyable, String permission,
                ItemStack icon, int coins, List<String> allowedGroups) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.buyable = buyable;
        this.permission = permission;
        this.icon = icon;
        this.coins = coins;
        this.allowedGroups = allowedGroups != null ? allowedGroups : new ArrayList<>();
    }

    public void register(Plugin plugin) {
        PerkManager.register(this);
        if (plugin != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return Language.options$cosmetic$perk + this.name;
    }

    public String getRawName() {
        return this.name;
    }

    public CosmeticRarity getRarity() {
        return rarity;
    }

    public int getCoins() {
        return coins;
    }

    public boolean canBeSold() {
        return buyable;
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

    public boolean isAllowed(ArenaGroup arenaGroup) {
        if (arenaGroup == null) return false;
        if (arenaGroup.getCosmeticsGroup() == null) return allowedGroups.isEmpty();
        return isAllowed(arenaGroup.getCosmeticsGroup());
    }

    public boolean has(Account account, String cosmeticsGroupId) {
        if (isPermissible() && hasByPermission(account.getPlayer())) {
            return true;
        }
        return account.getCosmeticHelper().hasPerk(cosmeticsGroupId, id);
    }


    public boolean has(Account account) {
        if (isPermissible() && hasByPermission(account.getPlayer())) {
            return true;
        }
        Arena arena = account.getArena();
        if (arena != null) {
            ArenaGroup group = arena.getGroup();
            if (group != null && group.getCosmeticsGroup() != null) {
                return has(account, group.getCosmeticsGroup().getId());
            }
        }
        if (!allowedGroups.isEmpty()) {
            return has(account, allowedGroups.get(0));
        }
        return false;
    }

    public void give(Account account, String cosmeticsGroupId) {
        account.getCosmeticHelper().addPerk(cosmeticsGroupId, id);
    }

    public boolean isSelected(Account acc) {
        Arena arena = acc.getArena();
        if (arena == null) return false;
        ArenaGroup group = arena.getGroup();
        if (group == null) return false;
        CosmeticsGroup cGroup = acc.getArena().getGroup().getCosmeticsGroup();
        if (cGroup == null) return false;
        return acc.getSelectedContainer().getSelectedPerk(cGroup.getId()) == this.id;
    }

    public boolean isSelected(Account account, String cosmeticsGroupId) {
        return account.getSelectedContainer().getSelectedKit(cosmeticsGroupId) == this.id;
    }

    public boolean isAbleToUse(Player player) {
        if (player == null) return false;

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) return false;

        Arena server = account.getArena();
        if (server == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
            return false;
        }

        if (!has(account)) {
            return false;
        }

        ArenaGroup group = server.getGroup();
        if (!isAllowed(group)) {
            return false;
        }

        return true;
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
}
