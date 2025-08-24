package org.twightlight.skywars.modules.boosters;

import org.twightlight.skywars.modules.boosters.boosters.Activating;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.Queue;

public class ServerManager {
    private static Queue queue;
    private static Activating activating;

    public static void init() {

        queue = new Queue(null, 20, Booster.BoosterType.SERVER);
        activating = new Activating(null, 1, Booster.BoosterType.SERVER, queue);
    }
}
