package org.twightlight.skywars.modules.quests.quests;

public class QuestData {
    private String questId;
    private int currentProgress;
    private long resetTime;
    private QuestStatus state;

    public static QuestData createQuestData(Quest quest) {
        QuestData data = new QuestData();
        data.questId = quest.getId();
        data.resetTime = quest.getNextRefresh();
        data.currentProgress = 0;
        data.state = QuestStatus.STARTING;
        return data;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public String getQuestId() {
        return questId;
    }

    public long getResetTime() {
        return resetTime;
    }

    public QuestData setQuestId(String questId) {
        this.questId = questId;
        return this;
    }

    public QuestData setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        return this;
    }

    public QuestData setResetTime(long resetTime) {
        this.resetTime = resetTime;
        return this;
    }

    public QuestStatus getStatus() {
        return state;
    }

    public void setCompletionState(QuestStatus completed) {
        this.state = completed;
    }
}
