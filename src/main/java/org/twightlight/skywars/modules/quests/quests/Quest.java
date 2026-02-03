package org.twightlight.skywars.modules.quests.quests;

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

public class Quest implements Refreshable, ProgressGoal, Identifiable {
    //General
    private String id;

    //Settings
    private ChronoUnit refreshTimeUnit;
    private int refreshTimeMultiplication;
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

    private Quest() {}

    public static Quest createQuest(String id, ConfigurationSection section) {
        Quest quest = new Quest();

        quest.id = id;

        quest.refreshTimeUnit = ChronoUnit.valueOf(section.getString("settings.schedule.unit"));
        quest.refreshTimeMultiplication = Integer.parseInt(section.getString("settings.schedule.value"));
        quest.progressionType = section.getString("settings.type");
        quest.requiredAmount = Integer.parseInt(section.getString("settings.condition"));

        quest.displayName = section.getString("placeholders.display-name");
        quest.shortDescription = section.getString("placeholders.short-description");
        quest.shortDescription = section.getString("placeholders.short-description");
        quest.startMessages = section.getStringList("placeholders.start");
        quest.completedMessages = section.getStringList("placeholders.complete");

        quest.pages = section.getIntegerList("menu.pages");
        quest.slot = section.getInt("menu.slot");

        quest.rewards = section.getStringList("rewards");

        if (quest.refreshTimeMultiplication * quest.refreshTimeUnit.getDuration().toMillis() < 6000L) {
            Quests.getInstance().getLogger().log(Logger.Level.WARNING, "Refreshing time of this quest is less than 5 minutes! Minimum time allowed is 5 minutes! Your quest is still loaded without any problem except the refreshing time is set to 5 minutes!");
        }

        quest.itemBuilder = (user -> {
            String path = "";

            switch (user.getQuestHelper().getQuestStatus(quest)) {
                case NOT_STARTED:
                    path = "quest-not-started";
                    break;
                case COMPLETED:
                    path = "quest-completed";
                    break;
                case STARTING:
                    path = "quest-starting";
                    break;
            }

            ItemBuilder itemBuilder = ItemBuilder.parse(section, "menu." + path);
            List<String> lore = itemBuilder.getLore();
            lore = lore.stream().map(line -> quest.applyPlaceholders(user, line)).collect(Collectors.toList());
            itemBuilder.setLore(lore);
            return itemBuilder.toItemStack();
        });

        return quest;
    }


    public ChronoUnit getRefreshTimeUnit() {
        return refreshTimeUnit;
    }

    public int getRefreshTimeMultiplication() {
        return refreshTimeMultiplication;
    }

    public void generateNextRefresh() {

        if (getNextRefresh() == 0L) {
            Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());

            LocalDate currentDateUTC = instant.atZone(ZoneOffset.UTC).toLocalDate();

            ZonedDateTime MidnightUTC = currentDateUTC.atStartOfDay(ZoneOffset.UTC);

            Quests.getInstance().getDatabase().getSQLHelper().setNextRefresh(this, MidnightUTC.toInstant().toEpochMilli() + (refreshTimeMultiplication * refreshTimeUnit.getDuration().toMillis()));

        } else {
            long current = getNextRefresh();

            while (System.currentTimeMillis() >= current) {
                current += refreshTimeMultiplication * refreshTimeUnit.getDuration().toMillis();
            }
            Quests.getInstance().getDatabase().getSQLHelper().setNextRefresh(this, current);


        }
    }

    @Override
    public long getNextRefresh() {

        return Quests.getInstance().getDatabase().getSQLHelper().getNextRefresh(this);
    }

    @Override
    public String getProgressionType() {
        return progressionType;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getRequiredAmount() {
        return requiredAmount;
    }

    @Override
    public void progress(User user, int amount) {
        user.getQuestHelper().progress(this, amount);
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
        return applyPlaceholders(s).replace("{currentprogress}", user.getQuestHelper().getCurrentProgress(this) + "").replace("{status}", user.getQuestHelper().getQuestStatus(this).name());
    }
}
