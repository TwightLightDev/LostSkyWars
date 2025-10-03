package org.twightlight.skywars.modules.boosters.users;

import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.boosters.streams.Activating;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.streams.Queue;

import java.util.UUID;

public abstract class User {
    protected Queue queue;
    protected Activating activating;

    public User() {}

    public boolean activateBooster(UUID uuid, String booster) {
        return activating.add(uuid, booster);
    }

    public void deactivateBooster(int booster) {
        activating.remove(booster);
    }

    public boolean addToQueue(UUID uuid, String booster) {
        if (activating.isEmpty()) {
            return activateBooster(uuid, booster);
        } else {
            return queue.add(uuid, booster);
        }
    }

    public Activating getActivatingStream() {
        return activating;
    }

    public Queue getQueueStream() {
        return queue;
    }

    public void removeFromQueue(int booster) {
        queue.remove(booster);
    }


    public float getTotalMultiplier(Booster.Currency currency) {
        return (float) (1F + activating.getActivatingBooster().stream()
                .filter(b -> {
                    Booster booster = BoosterManager.getBoosters().get(b.getValue());
                    if (booster != null) {
                        return booster.getCurrency() == currency;
                    } else {
                        return false;
                    }
                })
                .mapToDouble(b -> {
                    Booster booster = BoosterManager.getBoosters().get(b.getValue());
                    if (booster != null) {
                        return booster.getAmplifier() - 1;
                    } else {
                        return 0;
                    }
                })
                .sum());
    }

    public boolean hasBooster() {
        return !activating.isEmpty();
    }


}
