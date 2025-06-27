package org.twightlight.skywars.modules.privategames.settings;

import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.privategames.PrivateGames;

public class GameTimeSetting extends PrivateGamesSetting<String>{

    public GameTimeSetting(String value, Player p) {
        super(value, p);
    }
    public static String getBaseValue(Player p, SettingTypes type) {
        return PrivateGames.getStorage().getDatabase().
                getData(p, type.getColumn(), new TypeToken<String>() {},
                        GameTime.DAY.getName());
    }

    public void setValue(GameTime value) {
        super.setValue(value.getName());
        PrivateGames.getStorage().getDatabase().
        pullData(p, value.getName(), SettingTypes.GAME_SPEED.getColumn());
    }

    @Override
    public String getValue() {
        return super.getValue();
    }

    public enum GameTime {
        DAY("DAY"),
        NOON("NOON"),
        AFTERNOON("AFTERNOON"),
        NIGHT("NIGHT");

        private String name;

        GameTime(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
