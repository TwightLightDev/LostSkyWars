package org.twightlight.skywars.modules.boosters.boosters;

import com.google.gson.Gson;

public class Booster {
    private static Gson gson = new Gson();

    private int duration;
    private Currency currency;
    private float amplifier;
    private BoosterType type;

    public static Booster createBooster(int time, Currency currency, float amplifier, BoosterType type) {
        Booster booster = new Booster();
        booster.amplifier = amplifier;
        booster.duration = time;
        booster.currency = currency;
        booster.type = type;
        return booster;
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

    public enum Currency {
        COINS,
        EXP,
        SOULS
    }

    public enum BoosterType {
        PERSONAL("personal_activating", "personal_queue", "personal_storage"),
        SERVER("server_activating", "server_queue", "server_activating");

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
