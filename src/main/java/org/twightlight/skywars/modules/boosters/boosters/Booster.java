package org.twightlight.skywars.modules.boosters.boosters;

import com.google.gson.Gson;
import org.twightlight.skywars.modules.libs.yaml.YamlWrapper;
import org.twightlight.skywars.player.Account;

import java.util.concurrent.CompletableFuture;

public class Booster {
    private static Gson gson = new Gson();

    private int duration;
    private Currency currency;
    private float amplifier;
    private BoosterType type;
    private CompletableFuture<Account> owner;
    private float affiliateRate;

    public static Booster createBooster(int time, Currency currency, float amplifier, float affiliateRate, BoosterType type) {
        Booster booster = new Booster();
        booster.amplifier = amplifier;
        booster.affiliateRate = affiliateRate;
        booster.duration = time;
        booster.currency = currency;
        booster.type = type;
        return booster;
    }

    public static Booster parseFromYaml(YamlWrapper wrapper, String path) {
        return createBooster(wrapper.getInt(path+".duration", 3600),
                Currency.valueOf(wrapper.getString(path+".currency", "COINS")),
                wrapper.getFloat(path + ".amplifier"),
                wrapper.getFloat(path + ".affiliate"),
                BoosterType.valueOf(wrapper.getString(path+".type", "PERSONAL"))
        );
    }

    public String parseToJson() {
        return gson.toJson(this);
    }

    public static Booster fromJson(String json) {
        return gson.fromJson(json, Booster.class);
    }

    public int getDuration() {
        return duration;
    }

    public BoosterType getType() {
        return type;
    }

    public Currency getCurrency() {
        return currency;
    }

    public float getAmplifier() {
        return amplifier;
    }

    public void setOwner(CompletableFuture<Account> owner) {
        this.owner = owner;
    }

    public CompletableFuture<Account> getOwner() {
        return owner;
    }

    public float getAffiliateRate() {
        return affiliateRate;
    }

    public enum Currency {
        COINS("Coins", "&e"),
        EXP("Exp", "&b"),
        SOULS("Souls", "&d");

        private String name;
        private String cc;
        Currency(String name, String colorcode) {
            this.name = name;
            this.cc = colorcode;
        }

        public String getName() {
            return name;
        }

        public String getColorCode() {
            return cc;
        }
    }

    public enum BoosterType {
        PERSONAL("personal_activating", "personal_queue", "personal_storage"),
        NETWORK("network_activating", "network_queue", "network_storage");


        String activating;
        String queue;
        String storage;

        BoosterType(String activating, String queue, String storage) {
            this.activating = activating;
            this.queue = queue;
            this.storage = storage;
        }

        public String getActivatingColumn() {
            return activating;
        }

        public String getQueueColumn() {
            return queue;
        }

        public String getStorageColumn() {
            return storage;
        }
    }
}
