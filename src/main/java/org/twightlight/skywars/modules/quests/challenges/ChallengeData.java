package org.twightlight.skywars.modules.quests.challenges;

import org.twightlight.skywars.modules.quests.Quests;

public class ChallengeData {
    private String challengeId;
    private int currentProgress;
    private long resetTime;

    public static ChallengeData createChallengeData(Challenge challenge) {
        ChallengeData data = new ChallengeData();
        data.challengeId = challenge.getId();
        data.currentProgress = 0;
        data.resetTime = challenge.getNextRefresh();
        return data;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public String getChallengeId() {
        return challengeId;
    }


    public ChallengeData setChallengeId(String challengeId) {
        this.challengeId = challengeId;
        return this;
    }

    public ChallengeData setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        return this;
    }


    public long getResetTime() {
        return resetTime;
    }

    public void reset() {
        currentProgress = 0;
        resetTime = Quests.getInstance().getChallengesManager().getChallengeFromId(challengeId).getNextRefresh();
    }
}
