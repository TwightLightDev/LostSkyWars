package org.twightlight.skywars.arena.group;

import org.bukkit.configuration.ConfigurationSection;
import org.twightlight.skywars.arena.ui.enums.SkyWarsEvent;
import org.twightlight.skywars.cosmetics.group.CosmeticsGroup;
import org.twightlight.skywars.utils.StringUtils;

import java.util.*;

public class ArenaGroup {

    private final String id;
    private final String display;
    private final int teamSize;
    private final List<String> traits;
    private final Map<String, Double> rewards;
    private final List<String> scoreboardWaiting;
    private final List<String> scoreboardIngame;
    private final int gameTime;
    private final List<Integer> refillTimes;
    private final int dragonTime;
    private final String tutorial;
    private CosmeticsGroup cosmeticsGroup;
    public ArenaGroup(String id, ConfigurationSection section) {
        String display = section.getString("display", id);
        int teamSize = section.getInt("team-size", 1);
        List<String> traits = section.getStringList("traits");

        Map<String, Double> rewards = new HashMap<>();
        ConfigurationSection rewardsSection = section.getConfigurationSection("rewards");
        if (rewardsSection != null) {
            for (String rewardKey : rewardsSection.getKeys(false)) {
                rewards.put(rewardKey, rewardsSection.getDouble(rewardKey));
            }
        }

        List<String> scoreboardWaiting = new ArrayList<>();
        List<String> scoreboardIngame = new ArrayList<>();
        ConfigurationSection sbSection = section.getConfigurationSection("scoreboard");
        if (sbSection != null) {
            scoreboardWaiting = sbSection.getStringList("waiting");
            scoreboardIngame = sbSection.getStringList("ingame");
        }

        int gameTime = 1200;
        List<Integer> refillTimes = Arrays.asList(900, 600);
        int dragonTime = 300;
        ConfigurationSection timelineSection = section.getConfigurationSection("timeline");
        if (timelineSection != null) {
            gameTime = timelineSection.getInt("game", 1200);
            refillTimes = timelineSection.getIntegerList("refills");
            dragonTime = timelineSection.getInt("dragon", 300);
        }

        String tutorial = section.getString("tutorial");

        this.id = id;
        this.display = display;
        this.teamSize = teamSize;
        this.traits = traits != null ? traits : new ArrayList<>();
        this.rewards = rewards != null ? rewards : new HashMap<>();
        this.scoreboardWaiting = scoreboardWaiting != null ? scoreboardWaiting : new ArrayList<>();
        this.scoreboardIngame = scoreboardIngame != null ? scoreboardIngame : new ArrayList<>();
        this.gameTime = gameTime;
        this.refillTimes = refillTimes != null ? refillTimes : new ArrayList<>();
        this.dragonTime = dragonTime;
        this.tutorial = tutorial;

        String cosmeticsGroupID = section.getString("cosmetics_group");
        if (!CosmeticsGroup.exists(cosmeticsGroupID)) {
            CosmeticsGroup.create(cosmeticsGroupID);
        }

        CosmeticsGroup group = CosmeticsGroup.getFromID(cosmeticsGroupID);
        group.add(this);
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return StringUtils.formatColors(display);
    }

    public String getRawDisplay() {
        return display;
    }

    public String getColoredName() {
        return StringUtils.formatColors(display);
    }

    public String getStrippedName() {
        return StringUtils.stripColors(getColoredName());
    }

    public int getTeamSize() {
        return teamSize;
    }

    public List<String> getTraits() {
        return Collections.unmodifiableList(traits);
    }

    public boolean hasTrait(String trait) {
        return traits.contains(trait.toLowerCase());
    }

    public Map<String, Double> getRewards() {
        return Collections.unmodifiableMap(rewards);
    }

    public double getReward(String key) {
        return rewards.getOrDefault(key, 0.0);
    }

    public int getRewardInt(String key) {
        return (int) getReward(key);
    }

    public List<String> getScoreboardWaiting() {
        return scoreboardWaiting;
    }

    public List<String> getScoreboardIngame() {
        return scoreboardIngame;
    }

    public int getGameTime() {
        return gameTime;
    }

    public List<Integer> getRefillTimes() {
        return refillTimes;
    }

    public int getDragonTime() {
        return dragonTime;
    }

    public String getTutorial() {
        return tutorial;
    }

    public Map<Integer, SkyWarsEvent> buildTimeline() {
        Map<Integer, SkyWarsEvent> timeline = new TreeMap<>(Collections.reverseOrder());
        timeline.put(gameTime, SkyWarsEvent.End);
        for (int refill : refillTimes) {
            timeline.put(refill, SkyWarsEvent.Refill);
        }
        if (dragonTime > 0) {
            timeline.put(dragonTime, SkyWarsEvent.Doom);
        }
        timeline.put(0, SkyWarsEvent.End);
        return timeline;
    }

    public boolean isSolo() {
        return teamSize == 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArenaGroup other = (ArenaGroup) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ArenaGroup{id=" + id + ", display=" + display + ", teamSize=" + teamSize + "}";
    }

    public CosmeticsGroup getCosmeticsGroup() {
        return cosmeticsGroup;
    }
}
