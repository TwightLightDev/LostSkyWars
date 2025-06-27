package org.twightlight.skywars.ui;


import org.bukkit.event.Event;

@FunctionalInterface
public interface Executable<T extends Event> {
    void execute(T p);
}
