package org.twightlight.skywars.modules.boosters.config;

import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;

public class MenuConfig extends org.twightlight.skywars.modules.api.yaml.MenuConfig {

    public MenuConfig(Plugin pl, String name, String dir, String module) {
        super(pl, name, dir, module);

        addDefault("mainmenu.size", 36);
        addDefault("mainmenu.name", "&7Boosters");

        addDefault("mainmenu.items.close.material", "BARRIER");
        addDefault("mainmenu.items.close.name", "&cClose");
        addDefault("mainmenu.items.close.slot", 31);

        addDefault("mainmenu.items.personal-booster.slot", 12);
        addDefault("mainmenu.items.personal-booster.no-personal-booster.material", "GOLD_INGOT");
        addDefault("mainmenu.items.personal-booster.no-personal-booster.name", "&cYou don't have any personal booster!");
        addDefault("mainmenu.items.personal-booster.no-personal-booster.lore", Collections.singletonList("&eClick to browse!"));

        addDefault("mainmenu.items.network-booster.slot", 14);
        addDefault("mainmenu.items.network-booster.no-network-booster.material", "GOLD_BLOCK");
        addDefault("mainmenu.items.network-booster.no-network-booster.name", "&cThere is no activating network booster!");
        addDefault("mainmenu.items.network-booster.no-network-booster.lore", Collections.singletonList("&eClick to browse!"));

        addDefault("storage.size", 54);
        addDefault("storage.name", "&7Storage");
        addDefault("storage.usableslots", Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44));

        addDefault("storage.items.boosters.material", "POTION");

        addDefault("storage.items.back.material", "BARRIER");
        addDefault("storage.items.back.name", "&aBack");
        addDefault("storage.items.back.slot", 49);

        addDefault("storage.items.previous-page.material", "ARROW");
        addDefault("storage.items.previous-page.name", "&aPrevious Page");
        addDefault("storage.items.previous-page.slot", 45);

        addDefault("storage.items.next-page.material", "ARROW");
        addDefault("storage.items.next-page.name", "&aNext Page");
        addDefault("storage.items.next-page.slot", 53);

        addDefault("activating.size", 36);
        addDefault("activating.name", "&7Activating");
        addDefault("activating.usableslots.personal", Arrays.asList(11, 13, 15));
        addDefault("activating.usableslots.network", Arrays.asList(13));

        addDefault("activating.items.boosters.material", "POTION");

        addDefault("activating.items.slot-empty.material", "GLASS");
        addDefault("activating.items.slot-empty.name", "&aThis slot has no booster!");

        addDefault("activating.items.slot-locked.material", "BEDROCK");
        addDefault("activating.items.slot-locked.name", "&cThis booster slot is unavailable for you!");

        addDefault("activating.items.back.material", "BARRIER");
        addDefault("activating.items.back.name", "&aBack");
        addDefault("activating.items.back.slot", 31);

        addDefault("activating.items.storage.material", "CHEST");
        addDefault("activating.items.storage.name", "&aStorage");
        addDefault("activating.items.storage.slot", 32);

        addDefault("activating.items.queue.material", "EMERALD");
        addDefault("activating.items.queue.name", "&aQueue");
        addDefault("activating.items.queue.slot", 30);

        addDefault("activating.items.previous-page.material", "ARROW");
        addDefault("activating.items.previous-page.name", "&aPrevious Page");
        addDefault("activating.items.previous-page.slot", 27);

        addDefault("activating.items.next-page.material", "ARROW");
        addDefault("activating.items.next-page.name", "&aNext Page");
        addDefault("activating.items.next-page.slot", 35);

        addDefault("queue.size", 36);
        addDefault("queue.name", "&7Queue");
        addDefault("queue.usableslots.personal", Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16, 17));
        addDefault("queue.usableslots.network", Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16, 17));

        addDefault("queue.items.back.material", "BARRIER");
        addDefault("queue.items.back.name", "&aBack");
        addDefault("queue.items.back.slot", 31);

        addDefault("queue.items.empty-queue.material", "GOLD_BLOCK");
        addDefault("queue.items.empty-queue.name", "&aThere is no pending booster!");
        addDefault("queue.items.empty-queue.slot", 13);

        addDefault("queue.items.previous-page.material", "ARROW");
        addDefault("queue.items.previous-page.name", "&aPrevious Page");
        addDefault("queue.items.previous-page.slot", 27);

        addDefault("queue.items.next-page.material", "ARROW");
        addDefault("queue.items.next-page.name", "&aNext Page");
        addDefault("queue.items.next-page.slot", 35);

        addDefault("confirm.size", 27);
        addDefault("confirm.name", "&7Are you sure?");

        addDefault("confirm.items.confirm.material", "STAINED_CLAY");
        addDefault("confirm.items.confirm.name", "&aConfirm");
        addDefault("confirm.items.confirm.data", 13);
        addDefault("confirm.items.confirm.slot", 15);

        addDefault("confirm.items.cancel.material", "STAINED_CLAY");
        addDefault("confirm.items.cancel.name", "&cCancel");
        addDefault("confirm.items.cancel.data", 14);
        addDefault("confirm.items.cancel.slot", 11);
        finish();
    }


}
