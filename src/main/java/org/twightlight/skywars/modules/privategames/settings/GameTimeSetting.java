package org.twightlight.skywars.modules.privategames.settings;

import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.privategames.PrivateGames;

public class GameTimeSetting extends PrivateGamesSetting<String>{
    private static SettingTypes type = SettingTypes.GAME_TIME;

    public GameTimeSetting(String value, Player p) {
        super(value, p);
    }
    public static String getBaseValue(Player p) {
        return PrivateGames.getStorage().getDatabase().
                getData(p, type.getColumn(), new TypeToken<String>() {},
                        GameTime.DAY.getName());
    }

    public void setValue(GameTime value) {
        super.setValue(value.getName());
        PrivateGames.getStorage().getDatabase().
                updateData(p, value.getName(), type.getColumn());
    }

    @Override
    public String getValue() {
        return super.getValue();
    }

    public enum GameTime {
        DAY("DAY", 0L),
        NOON("NOON", 6000L),
        AFTERNOON("AFTERNOON", 12000L),
        NIGHT("NIGHT", 18000L);

        private String name;
        private long time;

        GameTime(String name, long time) {
            this.name = name;
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public long getTime() {
            return time;
        }
    }
}
