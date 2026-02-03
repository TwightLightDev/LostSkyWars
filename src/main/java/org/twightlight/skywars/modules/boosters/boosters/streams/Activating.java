package org.twightlight.skywars.modules.boosters.boosters.streams;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.api.event.BoosterActiveEvent;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterData;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.users.ServerUser;
import org.twightlight.skywars.modules.boosters.users.User;

import java.util.*;

public class Activating implements Stream {
    private final TreeMap<Long, BoosterData> list;
    private int cap;
    private final Booster.BoosterType type;
    private BukkitTask task;
    private final Queue queue;
    private final User user;

    public Activating(User user, int cap, Booster.BoosterType type, Queue queue) {
        this.cap = cap;
        this.type = type;
        this.user = user;
        this.queue = queue;
        if (type == Booster.BoosterType.PERSONAL) {
            PlayerUser playerUser = (PlayerUser) user;
            this.list = Boosters.getDatabase().getData(
                    Bukkit.getPlayer(playerUser.getUUID()), type.getActivatingColumn(),
                    new TypeToken<TreeMap<Long, BoosterData>>() {}, new TreeMap<>());
        } else {
            this.list = Boosters.getDatabase().getNetworkData(type.getActivatingColumn(),
                    new TypeToken<TreeMap<Long, BoosterData>>() {}, new TreeMap<>());
        }
        check();
    }

    public boolean add(UUID uuid, String boosterid) {
        if (list.size() < cap && BoosterManager.getBoosters().get(boosterid) != null) {
            Booster booster = BoosterManager.getBoosters().get(boosterid);
            list.put(System.currentTimeMillis() + booster.getDuration() * 1000L, new BoosterData(uuid, boosterid));

            BoosterActiveEvent e = new BoosterActiveEvent(booster, user);
            Bukkit.getPluginManager().callEvent(e);
            update(type.getActivatingColumn());
            check();
            return true;
        }
        return false;
    }

    public boolean remove(long pos) {
        BoosterData b = list.remove(pos);
        check();
        update(type.getActivatingColumn());
        return b != null;
    }

    public boolean remove(int pos) {
        try {
            long target = list.keySet().toArray(new Long[0])[pos];
            return remove(target);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public Booster.BoosterType getType() {
        return type;
    }

    public void update(String column) {
        if (user instanceof PlayerUser) {
            PlayerUser playerUser = (PlayerUser) user;
            Boosters.getDatabase().updateData(Bukkit.getPlayer(playerUser.getUUID()), list, column);
        } else if (user instanceof ServerUser)  {
            Boosters.getDatabase().updateNetworkData(list, column);
        }
    }

    public void check() {
        if (task == null) {
            task = Bukkit.getScheduler().runTaskTimer(Boosters.getInstance().getPlugin(), () -> {
                Iterator<Long> iterator = list.keySet().iterator();
                while (iterator.hasNext()) {
                    long key = iterator.next();
                    if (System.currentTimeMillis() > key) {
                        iterator.remove();
                        remove(key);
                    } else {
                        break;
                    }
                }

                if (list.size() < cap && !queue.isEmpty()) {
                    BoosterData next = queue.getQueue().poll();
                    if (next != null) {
                        add(next.getOwner(), next.getBoosterID());
                        queue.update(type.getQueueColumn());
                    }
                }

                if (list.isEmpty()) {
                    task.cancel();
                    task = null;
                }
            }, 20L, 20L);
        }
    }

    public List<BoosterData> getAsList() {
        return new ArrayList<>(list.values());
    }

    public List<Long> getActivatingTimeLine() {
        return new ArrayList<>(list.keySet());
    }

    public boolean isFull() {
        return list.size() >= cap;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public User getUser() {
        return user;
    }

    public UUID getOwner(int pos) {
        try {
            long target = list.keySet().toArray(new Long[0])[pos];
            return list.get(target).getOwner();
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
