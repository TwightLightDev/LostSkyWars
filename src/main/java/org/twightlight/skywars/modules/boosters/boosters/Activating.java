package org.twightlight.skywars.modules.boosters.boosters;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.User;

import java.util.*;

public class Activating {
    private TreeMap<Long, Booster> list;
    private int cap;
    private Booster.BoosterType type;
    private BukkitTask task;
    private Queue queue;
    private User user;

    public Activating(User user, int cap, Booster.BoosterType type, Queue queue) {
        this.cap = cap;
        this.type = type;
        this.user = user;
        if (type == Booster.BoosterType.PERSONAL) {
            this.list = Boosters.getDatabase().getData(
                    Bukkit.getPlayer(user.getUUID()), Booster.BoosterType.PERSONAL.getActivatingColumn(),
                    new TypeToken<TreeMap<Long, Booster>>() {}, new TreeMap<>());
        } else {
            this.list = Boosters.getDatabase().getServerData(Booster.BoosterType.SERVER.getActivatingColumn(),
                    new TypeToken<TreeMap<Long, Booster>>() {}, new TreeMap<>());
        }

        this.queue = queue;
    }

    public void add(Booster booster) {
        if (list.keySet().size() < cap) {
            list.put(System.currentTimeMillis() + booster.getDuration(), booster);
            check();
            update(type.getQueueColumn());
        }
    }

    public void remove(long pos) {
        list.remove(pos);
        check();
        update(type.getQueueColumn());
    }

    public void remove(int pos) {
        long target = list.keySet().toArray(new Long[0])[pos];
        remove(target);

    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public Booster.BoosterType getType() {
        return type;
    }

    public void update(String column) {
        if (type == Booster.BoosterType.PERSONAL) {
            Boosters.getDatabase().updateData(Bukkit.getPlayer(user.getUUID()), list, column);
        } else {
            Boosters.getDatabase().updateServerData(list, column);
        }
    }

    public void check() {
        if (!list.isEmpty() && task == null) {
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
                    Booster next = queue.getQueue().poll();
                    if (next != null) {
                        add(next);
                    }
                }
            }, 20L, 20L);
        } else if (list.isEmpty() && task != null) {
            task.cancel();
            task = null;
        }
    }

    public List<Booster> getActivatingBooster() {
        return new ArrayList<>(list.values());
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

}
