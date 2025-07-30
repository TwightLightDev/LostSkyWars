package org.twightlight.skywars.modules.privategames.settings;

import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.privategames.PrivateGames;

public class GameSpeedSetting extends PrivateGamesSetting<Double>{

    private static SettingTypes type = SettingTypes.GAME_SPEED;

    public GameSpeedSetting(Double value, Player p) {
        super(value, p);
    }
    public static Double getBaseValue(Player p) {
        return PrivateGames.getStorage().getDatabase().
                getData(p, type.getColumn(), new TypeToken<Double>() {},
                        1D);
    }

    @Override
    public void setValue(Double value) {
        super.setValue(value);
        PrivateGames.getStorage().getDatabase().
                updateData(p, value, type.getColumn());
    }

    @Override
    public Double getValue() {
        return super.getValue();
    }
}
