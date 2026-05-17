package org.twightlight.skywars.cosmetics;

import org.twightlight.skywars.Language;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum CosmeticRarity {
    MYTHIC("§cMYTHIC", 3, "MYTHIC", 100),
    LEGENDARY("§6LEGENDARY", 10, "LEGENDARY", 75),
    EPIC("§5EPIC", 30, "EPIC", 60),
    RARE("§9RARE", 60, "RARE", 40),
    UNCOMMON("§aUNCOMMON", 80, "UNCOMMON", 20),
    COMMON("§7COMMON", 100, "COMMON", 0);

    private String name;
    private double percentage;
    private String id;
    private int weight;

    CosmeticRarity(String name, double percentage, String id, int weight) {
        this.name = name;
        this.percentage = percentage;
        this.id = id;
        this.weight = weight;
    }

    public void translate(Account account) {
        if (this == MYTHIC) {
            this.name = Language.options$rarity$mythic;
        } else if (this == LEGENDARY) {
            this.name = Language.options$rarity$legendary;
        } else if (this == EPIC) {
            this.name = Language.options$rarity$epic;
        } else if (this == RARE) {
            this.name = Language.options$rarity$rare;
        } else if (this == UNCOMMON) {
            this.name = Language.options$rarity$uncommon;
        } else {
            this.name = Language.options$rarity$common;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return StringUtils.getFirstColor(this.name);
    }

    public double getPercentage() {
        return percentage;
    }

    public int getWeight() {
        return weight;
    }

    public List<Double> getPercentages(int diviser) {
        List<Double> list = new ArrayList<>();
        for (int i = 1; i <= diviser; i++) {
            list.add((percentage / diviser) * i);
        }

        Collections.reverse(list);
        return list;
    }

    public String getUncoloredName() {
        return id;
    }

    public static CosmeticRarity getRandomRarity(double random) {
        for (CosmeticRarity rarity : values()) {
            if (random <= rarity.getPercentage()) {
                return rarity;
            }
        }

        return COMMON;
    }

    public static CosmeticRarity fromName(String name) {
        for (CosmeticRarity rarity : values()) {
            if (rarity.name().equalsIgnoreCase(name)) {
                return rarity;
            }
        }

        return COMMON;
    }
}