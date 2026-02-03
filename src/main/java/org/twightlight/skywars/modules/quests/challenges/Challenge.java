package org.twightlight.skywars.modules.quests.challenges;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.quests.User;
import org.twightlight.skywars.modules.quests.interfaces.Identifiable;
import org.twightlight.skywars.modules.quests.interfaces.ProgressGoal;
import org.twightlight.skywars.modules.quests.interfaces.Refreshable;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.ItemBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Challenge implements Refreshable, Identifiable, ProgressGoal {
    //General
    private String id;

    //Settings
    private ChronoUnit refreshTimeUnit = ChronoUnit.DAYS;
    private int refreshTimeMultiplication = 1;
    private String progressionType;
    private int requiredAmount;

    //Placeholders
    private String displayName;
    private String shortDescription;
    private List<String> startMessages;
    private List<String> completedMessages;

    //Menu
    private Function<User, ItemStack> itemBuilder;
    private int slot;
    private List<Integer> pages;

    //Rewards;
    private List<String> rewards;

    public static Challenge createChallenge(String id, ConfigurationSection section) {
        Challenge challenge = new Challenge();

        challenge.id = id;

        challenge.progressionType = section.getString("settings.type");
        challenge.requiredAmount = Integer.parseInt(section.getString("settings.condition"));

        challenge.displayName = section.getString("placeholders.display-name");
        challenge.shortDescription = section.getString("placeholders.short-description");
        challenge.shortDescription = section.getString("placeholders.short-description");
        challenge.startMessages = section.getStringList("placeholders.start");
        challenge.completedMessages = section.getStringList("placeholders.complete");

        challenge.pages = section.getIntegerList("menu.pages");
        challenge.slot = section.getInt("menu.slot");

        challenge.rewards = section.getStringList("rewards");

        if (challenge.refreshTimeMultiplication * challenge.refreshTimeUnit.getDuration().toMillis() < 6000L) {
            Quests.getInstance().getLogger().log(Logger.Level.WARNING, "Refreshing time of this challenge is less than 5 minutes! Minimum time allowed is 5 minutes! Your challenge is still loaded without any problem except the refreshing time is set to 5 minutes!");
        }

        challenge.itemBuilder = (user -> {
            String path = "";

            ItemBuilder itemBuilder = ItemBuilder.parse(section, "menu." + path);
            List<String> lore = itemBuilder.getLore();
            lore = lore.stream().map(line -> challenge.applyPlaceholders(user, line)).collect(Collectors.toList());
            itemBuilder.setLore(lore);
            return itemBuilder.toItemStack();
        });

        return challenge;
    }

    public ChronoUnit getRefreshTimeUnit() {
        return refreshTimeUnit;
    }

    public int getRefreshTimeMultiplication() {
        return refreshTimeMultiplication;
    }

    @Override
    public void generateNextRefresh() {

    }

    public long getNextRefresh() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());

        LocalDate currentDateUTC = instant.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate nextDateUTC = currentDateUTC.plusDays(1);

        ZonedDateTime nextMidnightUTC = nextDateUTC.atStartOfDay(ZoneOffset.UTC);

        return nextMidnightUTC.toInstant().toEpochMilli();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void progress(User user, int amount) {
        user.getChallengesHelper().progress(this, amount);
    }

    @Override
    public String getProgressionType() {
        return progressionType;
    }

    @Override
    public int getRequiredAmount() {
        return requiredAmount;
    }

    @Override
    public void complete(User user) {
        user.sendMessage(getCompletedMessages());
        for (String reward : rewards) {
            String[] elements = reward.split(" ", 2);

            String type = elements[0];
            if (type.equals("[exp]")) {
                Account account = Database.getInstance().getAccount(user.getPlayer().getUniqueId());
                account.addExp(Double.parseDouble(elements[1]));
            } else if (type.equals("[coins]")) {
                Account account = Database.getInstance().getAccount(user.getPlayer().getUniqueId());
                account.addStat("coins", Integer.parseInt(elements[1]));
            } else if (type.equals("[souls]")) {
                Account account = Database.getInstance().getAccount(user.getPlayer().getUniqueId());
                account.addStat("souls", Integer.parseInt(elements[1]));
            } else if (type.equals("[message]")) {
                user.sendMessage(elements[1]);
            } else if (type.equals("[command]")) {
                if (SkyWars.placeholderapi) {
                    user.getPlayer().performCommand(PlaceholderAPI.setPlaceholders(user.getPlayer(), elements[1]));
                }
                user.getPlayer().performCommand(elements[1].replace("{player}", user.getPlayer().getName()));
            }
        }
    }

    public void start(User user) {
        user.sendMessage(getStartMessages());
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Function<User, ItemStack> getItemBuilder() {
        return itemBuilder;
    }

    public int getSlot() {
        return slot;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public List<String> getStartMessages() {
        return startMessages.stream().map(this::applyPlaceholders).collect(Collectors.toList());
    }

    public List<String> getCompletedMessages() {
        return completedMessages.stream().map(this::applyPlaceholders).collect(Collectors.toList());
    }

    public String applyPlaceholders(String s) {
        return s.replace("{displayname}", displayName).replace("{description}", shortDescription).replace("{requiredprogress}", requiredAmount + "");
    }

    public String applyPlaceholders(User user, String s) {
        return applyPlaceholders(s).replace("{currentprogress}", user.getChallengesHelper().getCurrentProgress(this) + "").replace("{timesLeft}", user.getChallengesHelper().getRemainingChallengesCompletionTimes() + "");
    }
}
