package org.twightlight.skywars.cosmetics.visual;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.cosmetics.CosmeticRarity;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.cosmetics.kit.KitManager;
import org.twightlight.skywars.cosmetics.perk.PerkManager;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsSymbol;
import org.twightlight.skywars.cosmetics.visual.categories.*;
import org.twightlight.skywars.player.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class VisualCosmetic {

    private int id;
    private VisualCosmeticType visualType;
    private CosmeticRarity rarity;

    public VisualCosmetic(int id, VisualCosmeticType visualType, CosmeticRarity rarity) {
        this.id = id;
        this.visualType = visualType;
        this.rarity = rarity;
    }

    public int getId() {
        return id;
    }

    public VisualCosmeticType getVisualType() {
        return visualType;
    }

    public CosmeticRarity getRarity() {
        return rarity;
    }

    public boolean has(Account account) {
        return account.getCosmeticHelper().hasCosmetic(visualType, id);
    }

    public void give(Account account) {
        account.getCosmeticHelper().addCosmetic(visualType, id);
    }

    public boolean selected(Account account) {
        return account.getSelectedContainer().getGlobalSelection(visualType.getSelectionColumn()) == this.id;
    }

    public abstract String getName();

    public abstract String getRawName();

    public abstract int getCoins();

    public abstract boolean canBeFoundInBox(Player player);

    public abstract ItemStack getIcon();

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Cosmetics");

    private static final List<VisualCosmetic> ALL_COSMETICS = new ArrayList<>();

    public static void register(VisualCosmetic cosmetic) {
        ALL_COSMETICS.add(cosmetic);
    }

    public static void unregisterByClass(Class<?> clazz) {
        ALL_COSMETICS.removeIf(c -> clazz.isAssignableFrom(c.getClass()));
    }

    public static List<VisualCosmetic> listAll() {
        return Collections.unmodifiableList(ALL_COSMETICS);
    }

    public static List<VisualCosmetic> listByType(VisualCosmeticType type) {
        List<VisualCosmetic> result = new ArrayList<>();
        for (VisualCosmetic cosmetic : ALL_COSMETICS) {
            if (cosmetic.getVisualType() == type) {
                result.add(cosmetic);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends VisualCosmetic> List<T> listByClass(Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (VisualCosmetic cosmetic : ALL_COSMETICS) {
            if (clazz.isAssignableFrom(cosmetic.getClass())) {
                result.add((T) cosmetic);
            }
        }
        return result;
    }

    public static VisualCosmetic findByTypeAndId(VisualCosmeticType type, int id) {
        for (VisualCosmetic cosmetic : ALL_COSMETICS) {
            if (cosmetic.getVisualType() == type && cosmetic.getId() == id) {
                return cosmetic;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends VisualCosmetic> T findByTypeAndId(VisualCosmeticType type, int id, Class<T> clazz) {
        for (VisualCosmetic cosmetic : ALL_COSMETICS) {
            if (cosmetic.getVisualType() == type && cosmetic.getId() == id && clazz.isAssignableFrom(cosmetic.getClass())) {
                return (T) cosmetic;
            }
        }
        return null;
    }

    public static void setupCosmetics() {
        ALL_COSMETICS.clear();

        SkyWarsCage.setupCages();
        KitManager.setupKits();
        PerkManager.setupPerks();
        SkyWarsDeathCry.setupDeathCries();
        SkyWarsTrail.setupProjectileTrails();
        SkyWarsKillMessage.setupKM();
        SkyWarsSpray.setupSprays();
        SkyWarsKillEffect.setupKillEffects();
        SkyWarsBalloon.setupBallons();
        SkyWarsSymbol.setupSymbols();

        int kitCount = KitManager.listAll().size();
        int perkCount = PerkManager.listAll().size();
        int cosmeticCount = ALL_COSMETICS.size();
        LOGGER.log(Level.INFO, "Loaded " + kitCount + " kits, " + perkCount + " perks, " + cosmeticCount + " visual cosmetics!");
    }
}
