package org.twightlight.skywars.modules.privategames;

import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.privategames.settings.GameSpeedSetting;
import org.twightlight.skywars.modules.privategames.settings.GameTimeSetting;
import org.twightlight.skywars.modules.privategames.settings.SettingTypes;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.ui.SkyWarsEvent;
import org.twightlight.skywars.world.WorldServer;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

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
        PrivateGames.getStorage().getDatabase().pullData(p, 0, "isEnable");
        gameSpeedSetting = new GameSpeedSetting(GameSpeedSetting.getBaseValue(p), p);
        gameTimeSetting = new GameTimeSetting(GameTimeSetting.getBaseValue(p), p);
        gameSpeedSetting = new GameSpeedSetting(GameSpeedSetting.getBaseValue(p), p);
        gameTimeSetting = new GameTimeSetting(GameTimeSetting.getBaseValue(p), p);

    }

    public void togglePrivateGame() {
        Integer booleanInt = PrivateGames.getStorage().getDatabase().getData(p, "isEnable", new TypeToken<Integer>() {}, 0);
        if (booleanInt == 0) {
            if (SkyWars.lostparties) {
                io.github.losteddev.parties.api.Party party = io.github.losteddev.parties.api.Party.getPartyByMember(p);
                if (party != null && party.getSize() >= 2) {
                    if (!party.getOwnerName().equalsIgnoreCase(p.getName())) {
                        p.sendMessage(Language.privategames$party$not_leader);
                        return;
                    }
                    PrivateGames.getStorage().getDatabase().pullData(p, 1, "isEnable");
                    p.sendMessage(Language.privategames$toggle$on);
                } else {
                    p.sendMessage(Language.privategames$party$not_found);
                }
            } else {
                PrivateGames.getStorage().getDatabase().pullData(p, 1, "isEnable");
                p.sendMessage(Language.privategames$toggle$on);
            }
        } else if (booleanInt == 1) {
            PrivateGames.getStorage().getDatabase().pullData(p, 0, "isEnable");
            p.sendMessage(Language.privategames$toggle$off);


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

    public void connect(Account account, WorldServer<?> server) {
        WorldServer<?> privateServer = server.cloneServer(true, server.getConfig().getId() + "_" + account.getPlayer().getUniqueId().toString());
        privateServer.setServerOwner(this);
        Map<Integer, SkyWarsEvent> newTimeLine = new TreeMap<>(Comparator.reverseOrder());
        Double multiplier = getGameSpeedSetting().getValue();
        Map<Integer, SkyWarsEvent> oldTimeLine = server.getTimeline();
        oldTimeLine.forEach((i, e) -> {
            int newPoint = (int) Math.round(i / multiplier);
            newTimeLine.put(newPoint, e);
        });
        privateServer.setTimeline(newTimeLine);
        privateServer.connect(account);
    }

    public Player getPlayer() {
        return p;
    }

    public GameSpeedSetting getGameSpeedSetting() {
        return gameSpeedSetting;
    }

    public GameTimeSetting getGameTimeSetting() {
        return gameTimeSetting;
    }
}
