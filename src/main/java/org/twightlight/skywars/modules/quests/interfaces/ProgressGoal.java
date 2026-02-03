package org.twightlight.skywars.modules.quests.interfaces;

import org.twightlight.skywars.modules.quests.User;

public interface ProgressGoal {
    void progress(User user, int amount);
    String getProgressionType();
    int getRequiredAmount();
    void complete(User user);
}
