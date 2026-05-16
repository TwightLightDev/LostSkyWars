package org.twightlight.skywars.integration.epicachievements.custom;

//import dev.pixelstudios.achievements.achievement.Achievement;
//import dev.pixelstudios.achievements.libs.xutils.Events;
//import dev.pixelstudios.achievements.libs.xutils.Tasks;
//import dev.pixelstudios.achievements.storage.User;
//import dev.pixelstudios.achievements.task.progress.TaskProgress;
//import org.bukkit.event.Event;
//import org.bukkit.event.EventPriority;
//import org.twightlight.skywars.hook.epicachievements.EpicAchievementsHook;
//import org.twightlight.skywars.hook.epicachievements.ResetTaskType;


public class CustomTaskManager {

//    public <SEX extends Event, GAY extends Event> void register(ResetTaskType<SEX, GAY> taskType) {
//        EpicAchievementsHook.getTaskRegistry().register(taskType);
//        if (taskType.getResetEventClass() != null) {
//            Events.subscribe(taskType.getResetEventClass(), paramEvent -> {
//                for (Achievement achievement : EpicAchievementsHook.getTaskManager().getAchievementsByTask().getOrEmpty(taskType)) {
//                    updateProgress(taskType.onReset(achievement, paramEvent));
//                }
//            }, EventPriority.MONITOR, true);
//        }
//    }
//
//    private void updateProgress(TaskProgress paramTaskProgress) {
//        if (paramTaskProgress == null || !paramTaskProgress.isValid())
//            return;
//        User user = paramTaskProgress.getUser();
//        Achievement achievement = paramTaskProgress.getAchievement();
//        Tasks.sync(() -> {
//            user.setProgress(achievement, paramTaskProgress.getFinalProgress());
//        });
//    }
}
