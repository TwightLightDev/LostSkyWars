package org.twightlight.skywars.cosmetics.skywars;

import org.bukkit.Bukkit;
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
import org.twightlight.skywars.api.server.SkyWarsServer;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.arena.group.ArenaGroup;
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

    public SkyWarsPerk(int id, String name, CosmeticRarity rarity, boolean buyable, String permission, ItemStack icon, int coins) {
        super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, rarity);
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.coins = coins;
        this.buyable = buyable;
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

    /**
     * Maps a group id to the old kit/perk mode index used by CosmeticType.
     * 1 = normal groups (solo, doubles)
     * 2 = insane groups (solo_insane, doubles_insane)
     * 3 = ranked groups (ranked_solo, ranked_doubles)
     * Returns -1 for groups that don't use kits/perks (e.g. duels).
     */
    public static int getKitIndexForGroup(ArenaGroup group) {
        if (group == null) return -1;
        String id = group.getId();
        if (id.contains("insane")) return 2;
        if (id.contains("ranked")) return 3;
        if (id.equals("duels")) return -1;
        return 1;
    }

    public boolean isAbleToUse(Player player) {
        if (player == null) {
            return false;
        }

        Account account = Database.getInstance().getAccount(player.getUniqueId());
        SkyWarsServer server = account.getServer();

        boolean able = true;
        if (server == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
            able = false;
        }

        if (Language.options$ranked$freekitsandperks ? !has(account) : !hasByPermission(player) || !has(account)) {
            able = false;
        }

        if (server != null) {
            ArenaGroup group = server.getGroup();
            int kitIndex = getKitIndexForGroup(group);
            if (kitIndex == -1 || kitIndex != this.getMode()) {
                able = false;
            }
        }

        account = null;
        server = null;
        return able;
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
        new ArrowRecovery(1);
        new ArrowRecovery(2);
        if (CONFIG.getBoolean("arrowrecovery.ranked")) {
            new ArrowRecovery(3);
        }

        new BlazingArrows(1);
        new BlazingArrows(2);
        if (CONFIG.getBoolean("blazingarrow.ranked")) {
            new BlazingArrows(3);
        }

        new Bulldozer(1);
        new Bulldozer(2);
        if (CONFIG.getBoolean("bulldozer.ranked")) {
            new Bulldozer(3);
        }

        checkIfAbsent("endermastery");
        new EnderMastery(1);
        new EnderMastery(2);
        if (CONFIG.getBoolean("endermastery.ranked")) {
            new EnderMastery(3);
        }

        checkIfAbsent("juggernaut");
        new Juggernaut(1);
        new Juggernaut(2);
        if (CONFIG.getBoolean("juggernaut.ranked")) {
            new Juggernaut(3);
        }

        checkIfAbsent("knowledge");
        new Knowledge(1);
        new Knowledge(2);
        if (CONFIG.getBoolean("knowledge.ranked")) {
            new Knowledge(3);
        }

        checkIfAbsent("nourishment");
        new Nourishment(1);
        new Nourishment(2);
        if (CONFIG.getBoolean("nourishment.ranked")) {
            new Nourishment(3);
        }

        checkIfAbsent("luckycharm");
        new LuckyCharm(1);
        new LuckyCharm(2);
        if (CONFIG.getBoolean("luckycharm.ranked")) {
            new LuckyCharm(3);
        }

        checkIfAbsent("voidmaster");
        new VoidMaster(1);
        new VoidMaster(2);
        if (CONFIG.getBoolean("voidmaster.ranked")) {
            new VoidMaster(3);
        }

        checkIfAbsent("decisivestrike");
        DecisiveStrike st = new DecisiveStrike(1);
        new DecisiveStrike(2);
        if (CONFIG.getBoolean("decisivestrike.ranked")) {
            new DecisiveStrike(3);
        }

        decisiveStrike = String.valueOf(st.getId());
    }

    private static String decisiveStrike;

    public static boolean isDecisiveStrike(Player player, int mode) {
        DecisiveStrike ds = Cosmetic.findFrom(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, mode, decisiveStrike, DecisiveStrike.class);
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

    private static void checkIfAbsent(String key) {
        if (CONFIG.contains(key)) {
            return;
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(SkyWars.getInstance().getResource("perks.yml"), "UTF-8"));
            for (String dataKey : config.getConfigurationSection(key).getKeys(false)) {
                CONFIG.set(key + "." + dataKey, config.get(key + "." + dataKey));
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
