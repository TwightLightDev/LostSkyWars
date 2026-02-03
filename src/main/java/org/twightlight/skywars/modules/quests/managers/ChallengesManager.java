package org.twightlight.skywars.modules.quests.managers;

import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.quests.challenges.Challenge;
import org.twightlight.skywars.modules.quests.quests.Quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChallengesManager {

    private final Map<String, Challenge> REGISTRY = new HashMap<>();

    public void registerChallenge(String id, Challenge quest) {
        REGISTRY.put(id, quest);

    }

    private void loadChallenges() {

    }

    public Challenge getChallengeFromId(String id) {
        return REGISTRY.getOrDefault(id, null);
    }

    public List<Challenge> getFromProgressionType(String progressionType) {
        return REGISTRY.values().stream().filter((challenge -> {
            return challenge.getProgressionType().equals(progressionType);
        })).collect(Collectors.toList());
    }


    public List<Challenge> getChallenges() {
        return new ArrayList<>(REGISTRY.values());
    }

    public boolean isChallengeExist(String id) {
        return REGISTRY.containsKey(id);
    }

}
