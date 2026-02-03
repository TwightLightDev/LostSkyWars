package org.twightlight.skywars.modules.quests;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.quests.challenges.Challenge;
import org.twightlight.skywars.modules.quests.challenges.ChallengeData;
import org.twightlight.skywars.modules.quests.database.SQLHelper;
import org.twightlight.skywars.modules.quests.quests.Quest;
import org.twightlight.skywars.modules.quests.quests.QuestData;
import org.twightlight.skywars.modules.quests.quests.QuestStatus;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class User {
    private UUID uuid;

    private QuestHelper questHelper;
    private ChallengesHelper challengesHelper;


    private static Map<UUID, User> userMap = new HashMap<>();

    public User(Player p) {
        this.uuid = p.getUniqueId();
        Quests.getInstance().getDatabase().getSQLHelper().createPlayerData(p);
        userMap.put(uuid, this);
        Map<String, ChallengeData> challengesData = Quests.getInstance().getDatabase().getSQLHelper().getData("quests", "player", p.getUniqueId().toString(), "challengesdata", new TypeToken<Map<String, ChallengeData>>() {}, new HashMap<>());
        int completedChallenges = Quests.getInstance().getDatabase().getSQLHelper().getData("quests_profiles", "player", p.getUniqueId().toString(), "completedchallenges", new TypeToken<Integer>() {}, 0);
        int challengesCompleteTimesLeft = Quests.getInstance().getDatabase().getSQLHelper().getData("quests_profiles", "player", p.getUniqueId().toString(), "challengesleft", new TypeToken<Integer>() {}, 0);
        long nextchallengesrefresh = Long.parseLong(Quests.getInstance().getDatabase().getSQLHelper().getData("quests_profiles", "player", p.getUniqueId().toString(), "nextchallengesrefresh", new TypeToken<String>() {}, "0"));



        Map<String, QuestData> questsData = Quests.getInstance().getDatabase().getSQLHelper().getData("quests", "player", p.getUniqueId().toString(), "questsdata", new TypeToken<Map<String, QuestData>>() {}, new HashMap<>());
        int completedQuests = Quests.getInstance().getDatabase().getSQLHelper().getData("quests_profiles", "player", p.getUniqueId().toString(), "completedquests", new TypeToken<Integer>() {}, 0);
        int autoaccept = Quests.getInstance().getDatabase().getSQLHelper().getData("quests_profiles", "player", p.getUniqueId().toString(), "autoaccept", new TypeToken<Integer>() {}, 0);


        questHelper = new QuestHelper(this, autoaccept, completedQuests, questsData);
        challengesHelper = new ChallengesHelper(this, completedChallenges, challengesData, challengesCompleteTimesLeft, nextchallengesrefresh);
    }


    public ChallengesHelper getChallengesHelper() {
        return challengesHelper;
    }

    public QuestHelper getQuestHelper() {
        return questHelper;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public static User getUser(Player p) {
        return userMap.getOrDefault(p.getUniqueId(), null);
    }

    public static User removeUser(User user) {
        return userMap.remove(user.uuid);
    }

    public static Map<UUID, User> getUsers() {
        return userMap;
    }

    public void save() {
        questHelper.save();
        challengesHelper.save();
    }

    public void reload() {}

    public void destroy() {}

    public void sendMessage(String msg) {
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public void sendMessage(List<String> msgs) {
        Player p = Bukkit.getPlayer(uuid);
        msgs.forEach(line -> {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        });
    }


    public static class QuestHelper {

        private User user;
        private boolean autoaccept;
        private int questCompleted;

        private Map<String, QuestData> questsData;

        public QuestHelper(User user, int autoaccept, int questCompleted, Map<String, QuestData> questsData) {
            this.user = user;
            this.questsData = questsData;
            this.questCompleted = questCompleted;
            this.autoaccept = autoaccept != 0;

            for (QuestData questData : questsData.values()) {
                if (Quests.getInstance().getQuestsManager().isQuestExist(questData.getQuestId())) {
                    Quest quest = Quests.getInstance().getQuestsManager().getQuestFromId(questData.getQuestId());
                    if (System.currentTimeMillis() >= questData.getResetTime()) {
                        resetQuest(quest);
                    }
                } else {
                    questsData.remove(questData.getQuestId());
                }
            }

            if (this.autoaccept) {
                for (Quest quest : Quests.getInstance().getQuestsManager().getQuests()) {
                    startQuest(quest);
                }
            }
        }

        public QuestStatus getQuestStatus(Quest quest) {
            if (isQuestActivating(quest.getId())) {
                return getQuestData(quest.getId()).getStatus();
            } else {
                return QuestStatus.NOT_STARTED;
            }
        }

        public void forceComplete(Quest quest, boolean callComplete) {
            if (callComplete) quest.complete(user);
            if (isQuestActivating(quest.getId())) {
                questsData.put(quest.getId(), QuestData.createQuestData(quest));
            }
            getQuestData(quest.getId()).setCompletionState(QuestStatus.COMPLETED);
        }

        public void startQuest(String id) {
            Quest quest = Quests.getInstance().getQuestsManager().getQuestFromId(id);
            quest.start(user);
            questsData.put(id, QuestData.createQuestData(quest));
        }

        public void startQuest(Quest quest) {
            quest.start(user);
            questsData.put(quest.getId(), QuestData.createQuestData(quest));
        }

        public boolean isAutoAccept() {
            return autoaccept;
        }

        public boolean progress(Quest quest) {
            return progress(quest, 1);
        }

        public boolean progress(Quest quest, int amount) {
            if (!isQuestActivating(quest.getId())) return false;

            QuestData data = getQuestData(quest.getId());
            int currentProgress = data.getCurrentProgress();

            if (currentProgress + amount >= quest.getRequiredAmount()) {
                data.setCurrentProgress(quest.getRequiredAmount());
                quest.complete(user);
                data.setCompletionState(QuestStatus.COMPLETED);
            } else {
                data.setCurrentProgress(currentProgress + amount);
            }
            return true;
        }

        public boolean isQuestActivating(String id) {
            return questsData.containsKey(id);
        }

        public int getQuestCompleted() {
            return questCompleted;
        }

        public void resetQuest(Quest quest) {
            if (getQuestStatus(quest) != QuestStatus.NOT_STARTED) {
                questsData.remove(quest.getId());
            }
        }

        public void setAutoAccept(boolean autoaccept) {
            this.autoaccept = autoaccept;
        }

        public void save() {
            SQLHelper helper = Quests.getInstance().getDatabase().getSQLHelper();

            helper.updateData(helper.getProfilesDBName(), "player", user.getPlayer().getUniqueId().toString(), questCompleted,  "completedquests");
            helper.updateData(helper.getProgressDBName(), "player", user.getPlayer().getUniqueId().toString(), questsData,  "questsdata");
            helper.setAutoAccept(user, autoaccept);
        }

        public int getCurrentProgress(Quest quest) {
            if (!isQuestActivating(quest.getId())) return 0;

            return getQuestData(quest.getId()).getCurrentProgress();
        }

        public QuestData getQuestData(String id) {
            return questsData.get(id);
        }
    }

    public static class ChallengesHelper {
        private final User user;
        private int challengesCompleted;

        private final Map<String, ChallengeData> challengesData;

        private int challengesCompletedTimesLeft;
        private long nextchallengesrefresh;

        public ChallengesHelper(User user, int challengesCompleted, Map<String, ChallengeData> challengesData, int challengesCompletedTimesLeft, long nextchallengesrefresh) {
            this.user = user;
            this.challengesCompleted = challengesCompleted;
            this.challengesData = challengesData;
            this.challengesCompletedTimesLeft = challengesCompletedTimesLeft;
            this.nextchallengesrefresh = nextchallengesrefresh;
            for (ChallengeData challengeData : challengesData.values()) {
                if (!Quests.getInstance().getChallengesManager().isChallengeExist(challengeData.getChallengeId())) {
                    challengesData.remove(challengeData.getChallengeId());
                }
                if (System.currentTimeMillis() >= challengeData.getResetTime()) {
                    challengeData.reset();
                }
            }

            for (Challenge challenge : Quests.getInstance().getChallengesManager().getChallenges()) {
                if (!challengesData.containsKey(challenge.getId())) {
                    challengesData.put(challenge.getId(), ChallengeData.createChallengeData(challenge));
                }
            }

            if (System.currentTimeMillis() >= nextchallengesrefresh) {
                this.challengesCompletedTimesLeft = 10;

                Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());

                LocalDate currentDateUTC = instant.atZone(ZoneOffset.UTC).toLocalDate();
                LocalDate nextDateUTC = currentDateUTC.plusDays(1);

                ZonedDateTime nextMidnightUTC = nextDateUTC.atStartOfDay(ZoneOffset.UTC);

                this.nextchallengesrefresh = nextMidnightUTC.toInstant().toEpochMilli();

                SQLHelper helper = Quests.getInstance().getDatabase().getSQLHelper();

                helper.updateData(helper.getProfilesDBName(), "player", user.getPlayer().getUniqueId().toString(), this.challengesCompletedTimesLeft,  "challengesleft");
                helper.updateData(helper.getProfilesDBName(), "player", user.getPlayer().getUniqueId().toString(), this.nextchallengesrefresh + "",  "nextchallengesrefresh");

            }
        }

        public void save() {
            SQLHelper helper = Quests.getInstance().getDatabase().getSQLHelper();

            helper.updateData(helper.getProfilesDBName(), "player", user.getPlayer().getUniqueId().toString(), challengesCompleted,  "completedchallenges");
            helper.updateData(helper.getProgressDBName(), "player", user.getPlayer().getUniqueId().toString(), challengesData,  "challengesdata");
            helper.updateData(helper.getProfilesDBName(), "player", user.getPlayer().getUniqueId().toString(), challengesCompletedTimesLeft,  "challengesleft");
            helper.updateData(helper.getProfilesDBName(), "player", user.getPlayer().getUniqueId().toString(), nextchallengesrefresh + "",  "nextchallengesrefresh");
        }


        public void forceComplete(Challenge challenge, boolean callComplete) {
            if (callComplete) challenge.complete(user);
            challengesCompleted ++;
            challengesCompletedTimesLeft --;
        }

        public boolean progress(Challenge challenge) {
            return progress(challenge, 1);
        }

        public boolean progress(Challenge challenge, int amount) {

            ChallengeData data = getChallengeData(challenge.getId());
            int currentProgress = data.getCurrentProgress();

            if (currentProgress + amount >= challenge.getRequiredAmount()) {
                data.setCurrentProgress(currentProgress + amount - challenge.getRequiredAmount());
                challenge.complete(user);
                challengesCompleted ++;
                challengesCompletedTimesLeft --;
            } else {
                data.setCurrentProgress(currentProgress + amount);
            }
            return true;
        }


        public void resetChallengeCompletionTimes() {
            challengesCompletedTimesLeft = 10;
        }


        public int getCurrentProgress(Challenge challenge) {

            return getChallengeData(challenge.getId()).getCurrentProgress();
        }

        public ChallengeData getChallengeData(String id) {
            return challengesData.get(id);
        }

        public int getRemainingChallengesCompletionTimes() {
            return challengesCompletedTimesLeft;
        }
    }
}
