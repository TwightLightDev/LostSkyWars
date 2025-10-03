package org.twightlight.skywars.modules.boosters.boosters.streams;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.api.event.BoosterQueueEvent;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.User;
import org.twightlight.skywars.utils.Pair;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Queue {

    private java.util.Deque<Pair<UUID, String>> queue;
    private int cap;
    private Booster.BoosterType type;
    private User user;

    public Queue(User user, int cap, Booster.BoosterType type) {
        this.cap = cap;
        this.type = type;
        this.user = user;
        if (type == Booster.BoosterType.PERSONAL) {
            PlayerUser playerUser = (PlayerUser) user;
            this.queue = Boosters.getDatabase().getData(

                    Bukkit.getPlayer(playerUser.getUUID()), Booster.BoosterType.PERSONAL.getQueueColumn(),
                    new TypeToken<ConcurrentLinkedDeque<Pair<UUID, String>>>() {}, new ConcurrentLinkedDeque<>());
        } else {
            this.queue = Boosters.getDatabase().getNetworkData(Booster.BoosterType.NETWORK.getQueueColumn(),
                    new TypeToken<ConcurrentLinkedDeque<Pair<UUID, String>>>() {}, new ConcurrentLinkedDeque<>());
        }
    }

    public boolean add(UUID uuid, String boosterid) {
        if (queue.size() < cap && BoosterManager.getBoosters().get(boosterid) != null) {
            Booster booster = BoosterManager.getBoosters().get(boosterid);
            queue.add(new Pair<>(uuid, boosterid));
            update(type.getQueueColumn());
            BoosterQueueEvent e = new BoosterQueueEvent(booster, user);
            Bukkit.getPluginManager().callEvent(e);
            return true;
        }
        return false;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public boolean remove(int pos) {
        if (pos < 0 || pos >= queue.size()) return false;
        int i = 0;
        for (Pair<UUID, String> booster : queue) {
            if (i == pos) {
                queue.remove(booster);
                update(type.getQueueColumn());
                break;
            }
            i++;
        }
        return true;
    }

    public Booster.BoosterType getType() {
        return type;
    }

    public java.util.Deque<Pair<UUID, String>> getQueue() {
        return queue;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void update(String column) {
        if (type == Booster.BoosterType.PERSONAL) {
            PlayerUser playerUser = (PlayerUser) user;
            Boosters.getDatabase().updateData(Bukkit.getPlayer(playerUser.getUUID()), queue, column);
        } else {
            Boosters.getDatabase().updateNetworkData(queue, column);
        }
    }

    public void promoteToTop(Pair<UUID, String> booster) {
        if (queue.remove(booster)) {
            queue.addFirst(booster);
            update(type.getQueueColumn());
        }
    }

    public void promoteToTop(int pos) {
        if (pos < 0 || pos >= queue.size()) return;
        int i = 0;
        for (Pair<UUID, String> booster : queue) {
            if (i == pos) {
                promoteToTop(booster);
                break;
            }
            i++;
        }
    }
    public User getUser() {
        return user;
    }

}
