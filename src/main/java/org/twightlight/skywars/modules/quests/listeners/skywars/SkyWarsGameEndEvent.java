package org.twightlight.skywars.modules.quests.listeners.skywars;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.quests.User;
import org.twightlight.skywars.modules.quests.managers.ProgressionBuilder;

public class SkyWarsGameEndEvent implements Listener {
    @EventHandler
    public void onPlayerWin(org.twightlight.skywars.api.event.game.SkyWarsGameEndEvent e) {
        for (Player winner : e.getWinnerTeam().getMembers()) {
            if (User.getUser(winner) == null) return;
            ProgressionBuilder.
                    build().
                    addQuestType("lsw_game_win").
                    setUser(User.getUser(winner)).
                    singleProgress().
                    buildAndExecute();
        }
    }
}
