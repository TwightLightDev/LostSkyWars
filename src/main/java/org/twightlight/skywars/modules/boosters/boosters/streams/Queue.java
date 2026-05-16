package org.twightlight.skywars.modules.boosters.boosters.streams;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.api.event.BoosterQueueEvent;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterData;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Queue implements Stream {

    private final java.util.Deque<BoosterData> queue;
    private int cap;
    private final Booster.BoosterType type;
    private final User user;

    public Queue(User user, int cap, Booster.BoosterType type) {
        this.cap = cap;
        this.type = type;
        this.user = user;
        if (type == Booster.BoosterType.PERSONAL) {
            PlayerUser playerUser = (PlayerUser) user;
            this.queue = Boosters.getDatabase().getData(
                    Bukkit.getPlayer(playerUser.getUUID()), Booster.BoosterType.PERSONAL.getQueueColumn(),
                    new TypeToken<ConcurrentLinkedDeque<BoosterData>>() {}, new ConcurrentLinkedDeque<>());
        } else {
            this.queue = Boosters.getDatabase().getNetworkData(Booster.BoosterType.NETWORK.getQueueColumn(),
                    new TypeToken<ConcurrentLinkedDeque<BoosterData>>() {}, new ConcurrentLinkedDeque<>());
        }
    }

    public boolean add(UUID uuid, String boosterid) {
        if (queue.size() < cap && BoosterManager.getBoosters().get(boosterid) != null) {
            Booster booster = BoosterManager.getBoosters().get(boosterid);
            queue.add(new BoosterData(uuid, boosterid));
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
        for (BoosterData booster : queue) {
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

    public java.util.Deque<BoosterData> getQueue() {
        return queue;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void update(String column) {
        if (type == Booster.BoosterType.PERSONAL) {
            PlayerUser playerUser = (PlayerUser) user;
            Boosters.getDatabase().updateData(Bukkit.getPlayer(playerUser.getUUID()), queue.isEmpty() ? new ArrayList<>() : queue, column);
        } else {
            Boosters.getDatabase().updateNetworkData(queue.isEmpty() ? new ArrayList<>() : queue, column);
        }
    }

    public void promoteToTop(BoosterData booster) {
        if (queue.remove(booster)) {
            queue.addFirst(booster);
            update(type.getQueueColumn());
        }
    }

    public void promoteToTop(int pos) {
        if (pos < 0 || pos >= queue.size()) return;
        int i = 0;
        for (BoosterData booster : queue) {
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

    public List<BoosterData> getAsList() {
        return Collections.unmodifiableList(new ArrayList<>(queue));
    }

    public UUID getOwner(int pos) {
        try {
            return getAsList().get(pos).getOwner();
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
