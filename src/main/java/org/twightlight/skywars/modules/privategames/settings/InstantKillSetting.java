package org.twightlight.skywars.modules.privategames.settings;

import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.privategames.PrivateGames;

public class InstantKillSetting extends PrivateGamesSetting<Integer>{

    private static SettingTypes type = SettingTypes.INSTANT_KILL;

    public InstantKillSetting(int value, Player p) {
        super(value, p);
    }
    public static Integer getBaseValue(Player p) {
        return PrivateGames.getStorage().getDatabase().
                getData(p, type.getColumn(), new TypeToken<Integer>() {},
                        0);
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(value);
        PrivateGames.getStorage().getDatabase().
                updateData(p, value, type.getColumn());
    }

    @Override
    public Integer getValue() {
        return super.getValue();
    }
}
