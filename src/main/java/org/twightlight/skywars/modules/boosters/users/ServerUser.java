package org.twightlight.skywars.modules.boosters.users;

import org.twightlight.skywars.modules.boosters.boosters.streams.Activating;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.streams.Queue;

public class ServerUser extends User {
    private static ServerUser user;

    public static void init() {
        user = new ServerUser();
        user.queue = new Queue(user, 20, Booster.BoosterType.NETWORK);
        user.activating = new Activating(user, 1, Booster.BoosterType.NETWORK, user.queue);
    }

    public static ServerUser getServerUser() {
        return user;
    }

    public static void  remove() {
        user = null;
    }
}
