package org.twightlight.skywars.modules.quests.menus;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.api.menus.ModulesMenuHolder;
import org.twightlight.skywars.modules.quests.Quests;
import org.twightlight.skywars.modules.quests.User;
import org.twightlight.skywars.modules.quests.menus.utils.QMenu;
import org.twightlight.skywars.modules.quests.quests.QuestStatus;
import org.twightlight.skywars.utils.ItemBuilder;
import org.twightlight.skywars.utils.StringUtils;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;


public class QuestsMasterMenu {
    public static void open(User viewer, int page) {
        QMenu menu = QMenu.createMenu(Quests.getInstance().getMenuConfig().getInt("questmaster.size"), StringUtils.formatColors(Quests.getInstance().getMenuConfig().getString("questmaster.name")));

        Item close = new Item((e) -> {
            e.getWhoClicked().closeInventory();
        }, (p) -> {
            return ItemBuilder.parse(Quests.getInstance().getMenuConfig().getYml(), "quests.questmaster.items.close").toItemStack();
        });

        menu.setItem(Quests.getInstance().getMenuConfig().getInt("questmaster.items.close.slot"), close);

        Item autoaccept = new Item((e) -> {

            if (!viewer.getPlayer().hasPermission("quests.autoaccept")) {
                viewer.sendMessage(Quests.getInstance().getLangConfig().getString("messages.need-rank"));
                viewer.getPlayer().closeInventory();
            }
            viewer.getQuestHelper().setAutoAccept(!viewer.getQuestHelper().isAutoAccept());
            menu.update(viewer.getPlayer(), Quests.getInstance().getMenuConfig().getInt("questmaster.items.auto-accept.slot"));
        }, (p) -> {
            String autoacceptpath = viewer.getQuestHelper().isAutoAccept() ? "auto-accept.on" : "auto-accept.off";

            return ItemBuilder.parse(Quests.getInstance().getMenuConfig().getYml(), "quests.questmaster.items." + autoacceptpath).toItemStack();
        });

        menu.setItem(Quests.getInstance().getMenuConfig().getInt("questmaster.items.auto-accept.slot"), autoaccept);

        Quests.getInstance().getQuestsManager().getQuestsByPage(page).forEach((q) -> {

            Item item = new Item((e) -> {
                Player player = (Player) e.getWhoClicked();
                User user = User.getUser(player);

                if (user == null) return;

                QuestStatus status = user.getQuestHelper().getQuestStatus(q);

                switch (status) {
                    case NOT_STARTED:
                        user.getQuestHelper().startQuest(q);
                        menu.update(viewer.getPlayer(), q.getSlot());
                        break;
                    case STARTING:
                        List<String> messages = Quests.getInstance().getLangConfig().getList("messages.quest-already-started").stream().map((s) -> q.applyPlaceholders(user, s)).collect(Collectors.toList());
                        user.sendMessage(messages);
                        break;
                    case COMPLETED:
                        String messages1 = q.applyPlaceholders(user, Quests.getInstance().getLangConfig().getString("messages.quest-completed"));
                        user.sendMessage(messages1);
                        break;
                }
            }, q.getItemBuilder());

            menu.setItem(q.getSlot(), item);
        });

        menu.open(viewer.getPlayer());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (viewer.getPlayer() == null || !viewer.getPlayer().isOnline()) {
                    cancel();
                    return;
                }
                if (viewer.getPlayer().getOpenInventory().getTopInventory() == null) {
                    cancel();
                    return;
                }
                if (viewer.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof ModulesMenuHolder) {
                    ModulesMenuHolder holder = (ModulesMenuHolder) viewer.getPlayer().getOpenInventory().getTopInventory().getHolder();
                    if (!menu.equals(holder.getMenu())) {
                        cancel();
                        return;
                    }
                }

                menu.update(viewer.getPlayer(), menu.getSlotsList());
            }
        }.runTaskTimer(SkyWars.getInstance(), Quests.getInstance().getMenuConfig().getInt("questmaster.update-interval"), Quests.getInstance().getMenuConfig().getInt("questmaster.update-interval"));

    }

}