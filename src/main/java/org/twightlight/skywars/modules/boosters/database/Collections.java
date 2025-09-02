package org.twightlight.skywars.modules.boosters.database;

import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Collections {
    private static final List<Booster> boosters = new ArrayList<>();

    public static void init() {
        Set<String> boosterPaths = Boosters.getBoostersConfig().getYml().getConfigurationSection("boosters").getKeys(false);
        for (String b : boosterPaths) {
            boosters.add(Booster.parseFromYaml(Boosters.getBoostersConfig(), "boosters." + b));
        }
        Boosters.getInstance().getLogger().log(Logger.Level.INFO, "Loaded " + boosters.size() + " boosters!");

    }

    public static List<Booster> getBoosters() {
        return boosters;
    }
}
