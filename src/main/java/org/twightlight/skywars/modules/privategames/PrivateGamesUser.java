package org.twightlight.skywars.modules.privategames;

import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.privategames.settings.GameSpeedSetting;
import org.twightlight.skywars.modules.privategames.settings.GameTimeSetting;
import org.twightlight.skywars.modules.privategames.settings.SettingTypes;

public class PrivateGamesUser {

    Player p;
    GameSpeedSetting gameSpeedSetting;
    GameTimeSetting gameTimeSetting;

    public PrivateGamesUser(Player p) {
        this.p = p;
        PrivateGames.getStorage().addUser(p, this);
        PrivateGames.getStorage().getDatabase().createPlayerData(p);
        for (SettingTypes type : SettingTypes.values()) {
            PrivateGames.getStorage().getDatabase().createPlayerData(p, type);
        }
        gameSpeedSetting = new GameSpeedSetting(GameSpeedSetting.getBaseValue(p, SettingTypes.GAME_SPEED), p);
        gameTimeSetting = new GameTimeSetting(GameTimeSetting.getBaseValue(p, SettingTypes.GAME_TIME), p);

    }

    public void togglePrivateGame() {
        Integer booleanInt = PrivateGames.getStorage().getDatabase().getData(p, "isEnable", new TypeToken<Integer>() {}, 0);
        if (booleanInt == 0) {
            PrivateGames.getStorage().getDatabase().pullData(p, 1, "isEnable");
        } else if (booleanInt == 1) {
            PrivateGames.getStorage().getDatabase().pullData(p, 0, "isEnable");
        }
    }

    public boolean isEnablePrivateGame() {
        Integer booleanInt = PrivateGames.getStorage().getDatabase().getData(p, "isEnable", new TypeToken<Integer>() {}, 0);
        if (booleanInt == 1) {
            return true;
        } else if (booleanInt == 0) {
            return false;
        }
        return false;
    }

    public GameSpeedSetting getGameSpeedSetting() {
        return gameSpeedSetting;
    }

    public GameTimeSetting getGameTimeSetting() {
        return gameTimeSetting;
    }
}
