package org.twightlight.skywars.modules.boosters.users;

import org.twightlight.skywars.modules.boosters.boosters.Activating;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.Queue;

public abstract class User {
    protected Queue queue;
    protected Activating activating;

    public User() {}

    public boolean activateBooster(Booster booster) {
        return activating.add(booster);
    }

    public void deactivateBooster(int booster) {
        activating.remove(booster);
    }

    public boolean addToQueue(Booster booster) {
        if (activating.isEmpty()) {
            return activateBooster(booster);
        } else {
            return queue.add(booster);
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
                .filter(b -> b.getCurrency() == currency)
                .mapToDouble(b -> b.getAmplifier() - 1)
                .sum());
    }

    public boolean hasBooster() {
        return !activating.isEmpty();
    }
}
