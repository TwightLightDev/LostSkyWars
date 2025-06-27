package org.twightlight.skywars.modules.privategames.settings;

import org.bukkit.entity.Player;

public abstract class PrivateGamesSetting<T> {
    protected T value;
    protected Player p;
    public PrivateGamesSetting(T value, Player p) {
        this.value = value;
        this.p = p;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }


}
