package org.twightlight.skywars.modules.quests.managers;

import org.twightlight.skywars.Logger;
import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.quests.quests.Quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestsManager {
    private final Map<String, Quest> REGISTRY = new HashMap<>();

    public QuestsManager() {
        loadQuests();
    }

    public void registerQuest(String id, Quest quest) {
        try {
            REGISTRY.put(id, quest);
            Quests.getInstance().getLogger().log(Logger.Level.INFO, "Registered quest with id: " + id);
            Quests.getInstance().getDatabase().getSQLHelper().createQuestData(quest);
        } catch (RuntimeException e) {
            Quests.getInstance().getLogger().log(Logger.Level.WARNING, "Failed to register quest with id: " + id);

            throw new RuntimeException(e);
        }

    }

    private void loadQuests() {
        for (String id : Quests.getInstance().getQuestsConfig().getYml().getConfigurationSection("quests").getKeys(false)) {
            registerQuest(id, Quest.createQuest(id, Quests.getInstance().getQuestsConfig().getYml().getConfigurationSection("quests." + id)));
        }
    }

    public Quest getQuestFromId(String id) {
        return REGISTRY.getOrDefault(id, null);
    }

    public List<Quest> getFromProgressionType(String progressionType) {
        return REGISTRY.values().stream().filter((quest -> {
            return quest.getProgressionType().equals(progressionType);
        })).collect(Collectors.toList());
    }


    public List<Quest> getQuests() {
        return new ArrayList<>(REGISTRY.values());
    }

    public boolean isQuestExist(String id) {
        return REGISTRY.containsKey(id);
    }
}
