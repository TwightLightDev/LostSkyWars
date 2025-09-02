package org.twightlight.skywars.modules.boosters.boosters;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.api.event.BoosterQueueEvent;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.User;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Queue {

    private java.util.Deque<Booster> queue;
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
                    new TypeToken<ConcurrentLinkedDeque<Booster>>() {}, new ConcurrentLinkedDeque<>());
        } else {
            this.queue = Boosters.getDatabase().getServerData(Booster.BoosterType.GLOBAL.getQueueColumn(),
                    new TypeToken<ConcurrentLinkedDeque<Booster>>() {}, new ConcurrentLinkedDeque<>());
        }
    }

    public void add(Booster booster) {
        if (queue.size() < cap) {
            queue.add(booster);
            update(type.getQueueColumn());
            BoosterQueueEvent e = new BoosterQueueEvent(booster, user);
            Bukkit.getPluginManager().callEvent(e);
        }
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public void remove(int pos) {
        if (pos < 0 || pos >= queue.size()) return;
        int i = 0;
        for (Booster booster : queue) {
            if (i == pos) {
                queue.remove(booster);
                update(type.getQueueColumn());
                break;
            }
            i++;
        }
    }

    public Booster.BoosterType getType() {
        return type;
    }

    public java.util.Deque<Booster> getQueue() {
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
            Boosters.getDatabase().updateServerData(queue, column);
        }
    }

    public void promoteToTop(Booster booster) {
        if (queue.remove(booster)) {
            queue.addFirst(booster);
            update(type.getQueueColumn());
        }
    }

    public void promoteToTop(int pos) {
        if (pos < 0 || pos >= queue.size()) return;
        int i = 0;
        for (Booster booster : queue) {
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
