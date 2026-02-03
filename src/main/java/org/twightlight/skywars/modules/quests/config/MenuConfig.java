package org.twightlight.skywars.modules.quests.config;

import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;

public class MenuConfig extends org.twightlight.skywars.modules.api.yaml.MenuConfig {

    public MenuConfig(Plugin pl, String name, String dir, String module) {
        super(pl, name, dir, module);

        addDefault("questmaster.size", 45);
        addDefault("questmaster.name", "&7Quests");
        addDefault("questmaster.update-interval", 20);

        addDefault("questmaster.items.close.material", "BARRIER");
        addDefault("questmaster.items.close.name", "&cClose");
        addDefault("questmaster.items.close.slot", 40);

        addDefault("questmaster.items.auto-accept.slot", 43);

        addDefault("questmaster.items.auto-accept.on.material", "LIME_DYE");
        addDefault("questmaster.items.auto-accept.on.amount", 1);
        addDefault("questmaster.items.auto-accept.on.name", "&aAuto-Accept Quests: ON");
        addDefault("questmaster.items.auto-accept.on.lore", Arrays.asList(
                "&7Click to automatically accept",
                "&7quests whenever you join a",
                "&7game lobby.",
                "",
                "&7Requires &bMVP&c+"
        ));

        addDefault("questmaster.items.auto-accept.off.material", "GRAY_DYE");
        addDefault("questmaster.items.auto-accept.off.amount", 1);
        addDefault("questmaster.items.auto-accept.off.name", "&cAuto-Accept Quests: OFF");
        addDefault("questmaster.items.auto-accept.off.lore", Arrays.asList(
                "&7Click to automatically accept",
                "&7quests whenever you join a",
                "&7game lobby.",
                "",
                "&7Requires &bMVP&c+"
        ));

        addDefault("questmaster.items.previous-page.material", "ARROW");
        addDefault("questmaster.items.previous-page.name", "&aPrevious Page");
        addDefault("questmaster.items.previous-page.slot", 36);

        addDefault("questmaster.items.next-page.material", "ARROW");
        addDefault("questmaster.items.next-page.name", "&aNext Page");
        addDefault("questmaster.items.next-page.slot", 44);

        finish();
    }


}
