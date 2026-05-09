package org.twightlight.skywars.cosmetics.skywars;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.api.server.GameServer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.arena.group.ArenaGroup;
import org.twightlight.skywars.arena.group.GroupManager;
import org.twightlight.skywars.cosmetics.Cosmetic;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.cosmetics.CosmeticServer;
import org.twightlight.skywars.cosmetics.CosmeticType;
import org.twightlight.skywars.cosmetics.skywars.perks.*;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.ConfigUtils;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class SkyWarsPerk extends Cosmetic implements Listener {

    private String name;
    private String permission;
    private ItemStack icon;
    private int coins;
    private boolean buyable;
    private List<String> allowedGroups;

    public SkyWarsPerk(int id, String name, CosmeticRarity rarity, boolean buyable, String permission, ItemStack icon, int coins, List<String> allowedGroups) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, rarity);
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.coins = coins;
        this.buyable = buyable;
        this.allowedGroups = allowedGroups != null ? allowedGroups : new ArrayList<>();
    }

    public void register(Plugin plugin) {
        CosmeticServer.SKYWARS.addCosmetic(this);
        if (plugin != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    public boolean canBeSold() {
        return buyable;
    }

    public List<String> getAllowedGroups() {
        return allowedGroups;
    }

    public boolean isAllowedInGroup(ArenaGroup group) {
        if (group == null) return false;
        if (allowedGroups.isEmpty()) return true;
        return allowedGroups.contains(group.getId());
    }

    public boolean isAbleToUse(Player player) {
        if (player == null) {
            return false;
        }

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) return false;

        Arena server = account.getArena();

        boolean able = true;
        if (server == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
            able = false;
        }

        if (!has(account) && (!isPermissible() || !hasByPermission(player))) {
            able = false;
        }

        if (server != null) {
            ArenaGroup group = server.getGroup();
            if (!isAllowedInGroup(group)) {
                able = false;
            }
        }

        return able;
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
        return Language.options$cosmetic$perk + this.name;
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

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Perks");
    private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

    public static void setupPerks() {
        checkIfAbsent("arrowrecovery");
        new ArrowRecovery();

        checkIfAbsent("blazingarrow");
        new BlazingArrows();

        checkIfAbsent("bulldozer");
        new Bulldozer();

        checkIfAbsent("endermastery");
        new EnderMastery();

        checkIfAbsent("juggernaut");
        new Juggernaut();

        checkIfAbsent("knowledge");
        new Knowledge();

        checkIfAbsent("nourishment");
        new Nourishment();

        checkIfAbsent("luckycharm");
        new LuckyCharm();

        checkIfAbsent("voidmaster");
        new VoidMaster();

        checkIfAbsent("decisivestrike");
        DecisiveStrike ds = new DecisiveStrike();
        decisiveStrike = String.valueOf(ds.getId());
    }

    private static String decisiveStrike;

    public static boolean isDecisiveStrike(Player player) {
        DecisiveStrike ds = Cosmetic.findFrom(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, 1, decisiveStrike, DecisiveStrike.class);
        if (ds != null) {
            Account account = Database.getInstance().getAccount(player.getUniqueId());
            if (account == null) {
                return false;
            }
            if (!ds.selected(account)) {
                return false;
            }
            return ds.isAbleToUse(player) && ThreadLocalRandom.current().nextInt(100) < ds.getPercentage();
        }

        return false;
    }

    public static List<String> loadAllowedGroups(String key) {
        if (CONFIG.contains(key + ".allowed-groups")) {
            return CONFIG.getStringList(key + ".allowed-groups");
        }
        List<String> defaultGroups = new ArrayList<>();
        for (ArenaGroup group : GroupManager.getGroups()) {
            defaultGroups.add(group.getId());
        }
        CONFIG.set(key + ".allowed-groups", defaultGroups);
        return defaultGroups;
    }

    private static void checkIfAbsent(String key) {
        if (CONFIG.contains(key + ".id")) {
            return;
        }

        try {
            FileConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(SkyWars.getInstance().getResource("perks.yml"), "UTF-8"));
            ConfigurationSection section = defaults.getConfigurationSection(key);
            if (section != null) {
                for (String dataKey : section.getKeys(true)) {
                    if (!section.isConfigurationSection(dataKey)) {
                        CONFIG.set(key + "." + dataKey, section.get(dataKey));
                    }
                }
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
