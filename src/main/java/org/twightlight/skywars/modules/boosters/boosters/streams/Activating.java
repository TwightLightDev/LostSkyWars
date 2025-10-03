package org.twightlight.skywars.modules.boosters.boosters.streams;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.boosters.api.event.BoosterActiveEvent;
import org.twightlight.skywars.modules.boosters.users.User;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.Pair;

import java.util.*;

public class Activating {
    private TreeMap<Long, Pair<UUID, String>> list;
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
            PlayerUser playerUser = (PlayerUser) user;
            this.list = Boosters.getDatabase().getData(
                    Bukkit.getPlayer(playerUser.getUUID()), Booster.BoosterType.PERSONAL.getActivatingColumn(),
                    new TypeToken<TreeMap<Long, Pair<UUID, String>>>() {}, new TreeMap<>());
        } else {
            this.list = Boosters.getDatabase().getNetworkData(Booster.BoosterType.NETWORK.getActivatingColumn(),
                    new TypeToken<TreeMap<Long, Pair<UUID, String>>>() {}, new TreeMap<>());
        }

        this.queue = queue;
    }

    public boolean add(UUID uuid, String boosterid) {
        if (list.keySet().size() < cap && BoosterManager.getBoosters().get(boosterid) != null) {
            Booster booster = BoosterManager.getBoosters().get(boosterid);
            list.put(System.currentTimeMillis() + booster.getDuration(), new Pair<>(uuid, boosterid));
            check();
            update(type.getQueueColumn());
            BoosterActiveEvent e = new BoosterActiveEvent(booster, user);
            Bukkit.getPluginManager().callEvent(e);
            return true;
        }
        return false;
    }

    public boolean remove(long pos) {
        Pair<UUID, String> b = list.remove(pos);
        check();
        update(type.getQueueColumn());
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
        if (type == Booster.BoosterType.PERSONAL) {
            PlayerUser playerUser = (PlayerUser) user;
            Boosters.getDatabase().updateData(Bukkit.getPlayer(playerUser.getUUID()), list, column);
        } else {
            Boosters.getDatabase().updateNetworkData(list, column);
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
                    Pair<UUID, String> next = queue.getQueue().poll();
                    if (next != null) {
                        add(next.getKey(), next.getValue());
                    }
                }
            }, 20L, 20L);
        } else if (list.isEmpty() && task != null) {
            task.cancel();
            task = null;
        }
    }

    public List<Pair<UUID, String>> getActivatingBooster() {
        return new ArrayList<>(list.values());
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public User getUser() {
        return user;
    }
}
