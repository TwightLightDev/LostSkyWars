package org.twightlight.skywars.arena.laboratory;

import org.twightlight.skywars.arena.ArenaConfig;

public class LaboratoryConfig extends ArenaConfig {
    private String sub_mode;

    public LaboratoryConfig(String yaml, boolean isPrivate) {
        super(yaml, isPrivate);
        this.sub_mode = config.getString("sub_mode");
    }

    public String getSubMode() {
        return sub_mode;
    }
}
