package org.twightlight.skywars.modules.boosters.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.api.yaml.YamlWrapper;
import org.twightlight.skywars.modules.boosters.boosters.Booster;

public class BoostersConfig extends YamlWrapper {

    private float[] amplifiers = new float[] {2F, 3F, 4F};
    private int[] durations = new int[] {1800, 3600, 7200, 14400, 28800, 54000, 86400, 259200, 604800, 2592000};

    public BoostersConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();
        String path = "boosters";
        for (Booster.BoosterType t : Booster.BoosterType.values()) {
            String path1 = path + "." + t.name();
            for (Booster.Currency c : Booster.Currency.values()) {
                String path2 = path1 + "_" + c.name();
                for (float a : amplifiers) {
                    String path3 = path2 + "_" + (int) (a * 100);
                    for (int d : durations) {
                        String path4 = path3 + "_" + d;
                        yml.addDefault(path4 + ".id", path4.replace("boosters.", ""));
                        yml.addDefault(path4 + ".duration", d);
                        yml.addDefault(path4 + ".currency", c.name());
                        yml.addDefault(path4 + ".amplifier", a);
                        yml.addDefault(path4 + ".affiliate", 0.05);
                        yml.addDefault(path4 + ".type", t.name());
                    }
                }
            }
        }
        yml.options().copyDefaults(true);
        save();
    }
}
