package org.twightlight.skywars.modules.quests.managers;

import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.quests.User;
import org.twightlight.skywars.modules.quests.interfaces.ProgressGoal;
import org.twightlight.skywars.modules.quests.quests.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProgressionBuilder {

    private List<ProgressGoal> goal;
    private int progressValue;
    private User user;

    private ProgressionBuilder() {}

    public static ProgressionBuilder build() {
        ProgressionBuilder progressionBuilder = new ProgressionBuilder();
        progressionBuilder.goal = new ArrayList<>();
        progressionBuilder.progressValue = 0;
        progressionBuilder.user = null;
        return progressionBuilder;
    }

    public ProgressionBuilder addGoals(List<ProgressGoal> goal) {
        this.goal.addAll(goal);
        return this;
    }

    public ProgressionBuilder addQuestType(String type) {
        this.goal.addAll(Quests.getInstance().getQuestsManager().getFromProgressionType(type).stream().map((q) -> (ProgressGoal) q).collect(Collectors.toList()));
        return this;
    }

    public ProgressionBuilder addChallengeType(String type) {
        this.goal.addAll(Quests.getInstance().getChallengesManager().getFromProgressionType(type).stream().map((q) -> (ProgressGoal) q).collect(Collectors.toList()));
        return this;
    }

    public ProgressionBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public ProgressionBuilder singleProgress() {
        this.progressValue = 1;
        return this;
    }

    public ProgressionBuilder progress(int progressValue) {
        this.progressValue = progressValue;
        return this;
    }

    public void buildAndExecute() {
        if (user == null || goal == null) return;

        goal.forEach(q -> q.progress(user, progressValue));

        this.progressValue = 0;
        this.user = null;
        this.goal = null;
    }
}
