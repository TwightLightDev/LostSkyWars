package org.twightlight.skywars.modules.quests.managers;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.quests.User;
import org.twightlight.skywars.modules.quests.quests.Quest;

public class TickingManager {

    private BukkitTask ticking;

    public TickingManager() {
        ticking = new BukkitRunnable() {

            int i = 0;

            @Override
            public void run() {

                if (i ++ >= 5) {
                    for (Quest quest : Quests.getInstance().getQuestsManager().getQuests()) {
                        if (System.currentTimeMillis() >= quest.getNextRefresh()) {
                            quest.generateNextRefresh();
                            Quests.getInstance().getLogger().log(Logger.Level.INFO, "Quest with id " + quest.getId() + " has been refreshed!");
                            for (User user : User.getUsers().values()) {
                                user.getQuestHelper().resetQuest(quest);
                            }
                        }
                    }
                    i = 0;
                }

                for (User user : User.getUsers().values()) {
                    user.save();
                }

            }
        }.runTaskTimer(SkyWars.getInstance(), 0, 1200L);
    }

    public void stop() {
        ticking.cancel();
    }
}
